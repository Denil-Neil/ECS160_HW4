package com.ecs160.hw4;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

// Concrete visitor for finding top liked posts
public class TopLikedPostsVisitor implements PostVisitor {
    private List<PostComponent> allPosts = new ArrayList<>();
    private int limit;

    public TopLikedPostsVisitor(int limit) {
        this.limit = limit;
    }

    @Override
    public void visit(Post post) {
        allPosts.add(post);
    }

    @Override
    public void visit(Thread thread) {
        allPosts.add(thread);
    }

    public List<PostComponent> getTopLikedPosts() {
        // Sort all posts by like count (descending)
        allPosts.sort((p1, p2) -> p2.getLikeCount() - p1.getLikeCount());

        // Return the top N posts
        int resultSize = Math.min(limit, allPosts.size());
        return allPosts.subList(0, resultSize);
    }
}