package com.ecs160.hw4;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// JSON Parser class using Gson instead of json-simple
public class BlueskyJsonParser {
    public static List<PostComponent> parseJson(String jsonFilePath) {
        List<PostComponent> components = new ArrayList<>();

        try (FileReader reader = new FileReader(jsonFilePath)) {
            // Parse the JSON file using Gson
            JsonElement rootElement = JsonParser.parseReader(reader);
            JsonObject rootObject = rootElement.getAsJsonObject();
            JsonArray feed = rootObject.getAsJsonArray("feed");

            for (JsonElement item : feed) {
                JsonObject threadObj = item.getAsJsonObject().getAsJsonObject("thread");
                if (threadObj != null) {
                    String type = threadObj.get("$type").getAsString();

                    if ("app.bsky.feed.defs#threadViewPost".equals(type)) {
                        JsonObject postObj = threadObj.getAsJsonObject("post");
                        Post mainPost = createPostFromJson(postObj);

                        if (threadObj.has("replies")) {
                            JsonArray replies = threadObj.getAsJsonArray("replies");
                            if (replies != null && replies.size() > 0) {
                                Thread thread = new Thread(mainPost);

                                for (JsonElement replyElem : replies) {
                                    JsonObject replyObj = replyElem.getAsJsonObject();
                                    JsonObject replyPostObj = replyObj.getAsJsonObject("post");
                                    Post reply = createPostFromJson(replyPostObj);
                                    thread.addReply(reply);
                                }

                                components.add(thread);
                            } else {
                                // Single post with no replies
                                components.add(mainPost);
                            }
                        } else {
                            // No replies field
                            components.add(mainPost);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            e.printStackTrace();
        }

        return components;
    }

    private static Post createPostFromJson(JsonObject postObj) {
        String uri = postObj.get("uri").getAsString();

        JsonObject author = postObj.getAsJsonObject("author");
        String authorName;
        if (author.has("displayName")) {
            authorName = author.get("displayName").getAsString();
        } else {
            authorName = author.get("handle").getAsString();
        }

        JsonObject record = postObj.getAsJsonObject("record");
        String text = record.get("text").getAsString();

        int likeCount = 0;
        if (postObj.has("likeCount")) {
            likeCount = postObj.get("likeCount").getAsInt();
        }

        return new Post(uri, authorName, text, likeCount);
    }
}