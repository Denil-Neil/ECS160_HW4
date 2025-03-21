package com.ecs160.hw4;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Main <analysis-type> <json-file>");
            System.out.println("  analysis-type: 'weighted' or 'non-weighted'");
            System.out.println("  json-file: path to the JSON file to analyze");
            return;
        }

        // Set configuration
        String analysisType = args[0];
        String jsonFilePath = args[1];

        Configuration config = Configuration.getInstance();
        config.setConfig(analysisType, jsonFilePath);

        // Parse JSON file
        List<PostComponent> components = BlueskyJsonParser.parseJson(jsonFilePath);

        // Analyze posts
        analyzeData(components);

        // Generate hashtags for top 10 most-liked posts
        generateHashtags(components, 10);

        // Print hashtags
        printHashtags(components);
    }

    private static void analyzeData(List<PostComponent> components) {
        // Count unique authors
        AuthorCountVisitor authorVisitor = new AuthorCountVisitor();
        for (PostComponent component : components) {
            component.accept(authorVisitor);
        }
        System.out.println("Number of unique authors: " + authorVisitor.getUniqueAuthorCount());

        // Calculate average likes
        AverageLikesVisitor likesVisitor = new AverageLikesVisitor();
        for (PostComponent component : components) {
            component.accept(likesVisitor);
        }
        System.out.printf("Average likes per post: %.2f\n", likesVisitor.getAverageLikes());

        // Find top 10 most-liked posts
        TopLikedPostsVisitor topPostsVisitor = new TopLikedPostsVisitor(10);
        for (PostComponent component : components) {
            component.accept(topPostsVisitor);
        }

        System.out.println("\nTop 10 most-liked posts:");
        List<PostComponent> topPosts = topPostsVisitor.getTopLikedPosts();
        for (int i = 0; i < topPosts.size(); i++) {
            PostComponent post = topPosts.get(i);
            System.out.printf("%d. %s (%d likes): %s\n",
                    i + 1, post.getAuthor(), post.getLikeCount(),
                    post.getText().length() > 50 ? post.getText().substring(0, 47) + "..." : post.getText());
        }
    }

    private static void generateHashtags(List<PostComponent> components, int limit) {
        HashtagVisitor hashtagVisitor = new HashtagVisitor(limit);

        // Visit all components to collect them
        for (PostComponent component : components) {
            component.accept(hashtagVisitor);
        }

        // Generate hashtags for top posts
        hashtagVisitor.generateHashtags();
    }

    private static void printHashtags(List<PostComponent> components) {
        // First, find the top 10 most-liked posts
        TopLikedPostsVisitor topPostsVisitor = new TopLikedPostsVisitor(10);
        for (PostComponent component : components) {
            component.accept(topPostsVisitor);
        }

        List<PostComponent> topPosts = topPostsVisitor.getTopLikedPosts();

        System.out.println("\nHashtags for top 10 most-liked posts and their replies:");

        // Print hashtags for the top 10 posts and their replies
        for (PostComponent component : topPosts) {
            String hashtag = "#bskypost"; // Default hashtag

            if (component instanceof Post) {
                Post post = (Post) component;
                if (post.hasDecoration("hashtag")) {
                    hashtag = (String) post.getDecoration("hashtag");
                }
                System.out.printf("%s (%d likes): %s\n  Hashtag: %s\n",
                        post.getAuthor(), post.getLikeCount(),
                        post.getText().length() > 50 ? post.getText().substring(0, 47) + "..." : post.getText(),
                        hashtag);

            } else if (component instanceof Thread) {
                Thread thread = (Thread) component;
                if (thread.hasDecoration("hashtag")) {
                    hashtag = (String) thread.getDecoration("hashtag");
                }

                // Print the main post of the thread
                System.out.printf("%s (%d likes): %s\n  Hashtag: %s\n",
                        thread.getAuthor(), thread.getLikeCount(),
                        thread.getText().length() > 50 ? thread.getText().substring(0, 47) + "..." : thread.getText(),
                        hashtag);

                // Print the replies
                for (Post reply : thread.getReplies()) {
                    String replyHashtag = "#bskypost"; // Default hashtag for replies
                    if (reply.hasDecoration("hashtag")) {
                        replyHashtag = (String) reply.getDecoration("hashtag");
                    }
                    System.out.printf("  ↪ %s (%d likes): %s\n    Hashtag: %s\n",
                            reply.getAuthor(), reply.getLikeCount(),
                            reply.getText().length() > 50 ? reply.getText().substring(0, 47) + "..." : reply.getText(),
                            replyHashtag);
                }
            }
        }
    }
}