package com.ecs160.hw4;
import java.util.ArrayList;
import java.util.List;

// Composite class for Composite pattern
public class Thread implements PostComponent {
    private Post mainPost;
    private List<Post> replies = new ArrayList<>();

    public Thread(Post mainPost) {
        this.mainPost = mainPost;
    }

    public void addReply(Post reply) {
        replies.add(reply);
    }

    public List<Post> getReplies() {
        return replies;
    }

    @Override
    public int getLikeCount() {
        int totalLikes = mainPost.getLikeCount();
        if (Configuration.getInstance().isWeighted()) {
            for (Post reply : replies) {
                totalLikes += reply.getLikeCount();
            }
        }
        return totalLikes;
    }

    @Override
    public String getAuthor() {
        return mainPost.getAuthor();
    }

    @Override
    public String getText() {
        return mainPost.getText();
    }

    @Override
    public String getUri() {
        return mainPost.getUri();
    }

    @Override
    public void accept(PostVisitor visitor) {
        visitor.visit(this);
        for (Post reply : replies) {
            reply.accept(visitor);
        }
    }

    // Decorator pattern implementation via delegation
    public void addDecoration(String key, Object value) {
        mainPost.addDecoration(key, value);
    }

    public Object getDecoration(String key) {
        return mainPost.getDecoration(key);
    }

    public boolean hasDecoration(String key) {
        return mainPost.hasDecoration(key);
    }
}