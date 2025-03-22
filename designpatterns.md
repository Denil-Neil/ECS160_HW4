# ECS160-HW4 Design Patterns Documentation

## Design Patterns Implemented

### 1. Singleton Pattern
**Where**: `Configuration` class
**Why**: The Singleton pattern was chosen for the Configuration class because:
- We need a single, globally accessible configuration object that stores analysis type and JSON filename
- Only one instance of configuration should exist during the application's lifetime
- It provides a convenient way to access configuration parameters from any part of the application without passing the object around

### 2. Composite Pattern
**Where**: `PostComponent` interface with `Post` (leaf) and `Thread` (composite) implementations
**Why**: The Composite pattern was chosen for handling posts and threads because:
- It allows treating individual posts and thread hierarchies uniformly through a common interface
- Clients can interact with both single posts and threads through the same API
- It simplifies operations like recursive statistics calculation by hiding the differences between simple and composite objects
- This pattern naturally represents the hierarchical relationship between posts and their replies

### 3. Visitor Pattern
**Where**: `PostVisitor` interface with various concrete visitor implementations
**Why**: The Visitor pattern was chosen for computing statistics because:
- It separates algorithms from the object structure they operate on
- New operations can be added without modifying the post/thread classes
- Each statistic calculation is encapsulated in its own visitor class
- It enables traversing the composite structure while applying different operations

Visitors created:
- `AuthorCountVisitor`: Counts unique authors
- `AverageLikesVisitor`: Calculates average likes per post
- `TopLikedPostsVisitor`: Identifies most-liked posts
- `HashtagVisitor`: Generates hashtags for top posts

### 4. Decorator Pattern
**Where**: `Post` class with the `decorations` Map and related methods
**Why**: The Decorator pattern (implemented as a Map-based property bag) was chosen to add hashtag functionality because:
- It allows dynamically adding new properties (hashtags) to objects without subclassing
- It satisfies the requirement of not adding a dedicated `hashtag` field or subclassing `Post` or `Thread`
- It's flexible enough to add any kind of metadata to posts in the future
- The Thread class delegates decoration operations to its main post, maintaining consistency

## Implementation Details

1. **Configuration Management**:
   - The `Configuration` class implements the Singleton pattern with a private constructor and static getInstance() method
   - It stores analysis type (weighted/non-weighted) and JSON filename
   - It provides a method to check if analysis is weighted

2. **Post Structure**:
   - `PostComponent` interface defines common operations like getLikeCount(), getAuthor(), getText(), and accept()
   - `Post` implements leaf functionality, representing a single post
   - `Thread` manages a main post and its replies, delegating some operations to the main post
   - Both accept visitors for statistics calculation

3. **Statistics Calculation**:
   - Each statistic is calculated by a dedicated visitor
   - Visitors traverse the post structure to gather data
   - Main application coordinates the process by passing visitors to components

4. **Hashtag Generation**:
   - `HashtagVisitor` identifies top posts by like count
   - It calls LLAMA-3 model via Ollama API to generate hashtags
   - Hashtags are stored as decorations on posts/threads
   - The `printHashtags()` method displays hashtags for the top 10 most-liked posts and their replies

## Benefits of the Design

1. **Extensibility**:
   - New statistics can be added by creating new visitors without modifying existing code
   - New post metadata can be added without modifying class structure

2. **Maintainability**:
   - Separation of concerns between data structure and algorithms
   - Each design pattern solves a specific problem
   - Single responsibility principle is maintained

3. **Flexibility**:
   - Uniform treatment of posts and threads through the common interface
   - Configuration accessible throughout the application
   - Data and behavior separated appropriately

4. **Adherence to Requirements**:
   - Single configuration object (Singleton)
   - Uniform API for posts and threads (Composite)
   - Flexible statistics computation (Visitor)
   - Hashtags without subclassing or adding fields (Decorator)