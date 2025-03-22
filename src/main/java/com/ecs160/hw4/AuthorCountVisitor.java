package com.ecs160.hw4;

import java.util.HashSet;
import java.util.Set;

public class AuthorCountVisitor implements PostVisitor {
    private Set<String> authors = new HashSet<>();

    @Override
    public void visit(Post post) {
        authors.add(post.getAuthor());
    }

    @Override
    public void visit(Thread thread) {
        authors.add(thread.getAuthor());
    }

    public int getUniqueAuthorCount() {
        return authors.size();
    }
}