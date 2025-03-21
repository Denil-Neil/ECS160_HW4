package com.ecs160.hw4;
import java.util.HashMap;
import java.util.Map;

// Leaf class for Composite pattern
public class Post implements PostComponent {
    private String uri;
    private String author;
    private String text;
    private int likeCount;
    private Map<String, Object> decorations = new HashMap<>();

    public Post(String uri, String author, String text, int likeCount) {
        this.uri = uri;
        this.author = author;
        this.text = text;
        this.likeCount = likeCount;
    }

    @Override
    public int getLikeCount() {
        return likeCount;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public void accept(PostVisitor visitor) {
        visitor.visit(this);
    }

    // Decorator pattern implementation
    public void addDecoration(String key, Object value) {
        decorations.put(key, value);
    }

    public Object getDecoration(String key) {
        return decorations.get(key);
    }

    public boolean hasDecoration(String key) {
        return decorations.containsKey(key);
    }
}