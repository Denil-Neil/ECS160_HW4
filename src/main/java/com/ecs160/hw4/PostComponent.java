// Component interface for Composite pattern
package com.ecs160.hw4;
public interface PostComponent {
    int getLikeCount();
    String getAuthor();
    String getText();
    String getUri();
    void accept(PostVisitor visitor);
}