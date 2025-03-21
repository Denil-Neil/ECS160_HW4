// Concrete visitor for calculating average likes
package com.ecs160.hw4;
public class AverageLikesVisitor implements PostVisitor {
    private int totalLikes = 0;
    private int postCount = 0;

    @Override
    public void visit(Post post) {
        totalLikes += post.getLikeCount();
        postCount++;
    }

    @Override
    public void visit(Thread thread) {
        totalLikes += thread.getLikeCount();
        postCount++;
    }

    public double getAverageLikes() {
        return postCount > 0 ? (double) totalLikes / postCount : 0;
    }
}