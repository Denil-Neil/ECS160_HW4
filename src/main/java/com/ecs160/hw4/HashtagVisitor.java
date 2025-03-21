package com.ecs160.hw4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

// Concrete visitor for hashtag generation
public class HashtagVisitor implements PostVisitor {
    private List<PostComponent> postsForHashtagging = new ArrayList<>();
    private int limit;
    private Gson gson = new Gson();

    public HashtagVisitor(int limit) {
        this.limit = limit;
    }

    @Override
    public void visit(Post post) {
        postsForHashtagging.add(post);
    }

    @Override
    public void visit(Thread thread) {
        postsForHashtagging.add(thread);
    }

    public void generateHashtags() {
        // Sort posts by like count
        postsForHashtagging.sort((p1, p2) -> p2.getLikeCount() - p1.getLikeCount());

        // Take top-N posts
        List<PostComponent> topPosts = postsForHashtagging.subList(0,
                Math.min(limit, postsForHashtagging.size()));

        // Generate hashtags using Ollama/LLAMA-3
        for (PostComponent post : topPosts) {
            String hashtag = callLlamaModel(post.getText());

            if (post instanceof Post) {
                ((Post) post).addDecoration("hashtag", hashtag);
            } else if (post instanceof Thread) {
                ((Thread) post).addDecoration("hashtag", hashtag);
            }
        }
    }

    private String callLlamaModel(String text) {
        try {
            // Clean the input text to prevent JSON formatting issues
            String cleanedText = text.replaceAll("[\"\\\\]", " ")
                    .replaceAll("\\r?\\n", " ")
                    .trim();

            // If text is empty or too short, return a default hashtag
            if (cleanedText.length() < 5) {
                return "#general";
            }

            // Limit text length to avoid extremely long prompts
            if (cleanedText.length() > 500) {
                cleanedText = cleanedText.substring(0, 500) + "...";
            }

            // Create a clean JSON object using Gson
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "llama3");
            requestBody.addProperty("prompt", "Generate a single hashtag (without explanation) that best represents this post: " + cleanedText);

            String jsonInput = gson.toJson(requestBody);

            // For debugging
            System.out.println("Making API call for text: " + cleanedText.substring(0, Math.min(30, cleanedText.length())) + "...");

            // Set up the connection
            URL url = new URL("http://localhost:11434/api/generate");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Check for error response
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.err.println("Error response: " + responseCode);
                // Try to read error message if available
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream()))) {
                    String errorLine;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    System.err.println("Error details: " + errorResponse.toString());
                } catch (Exception e) {
                    // Ignore errors reading the error stream
                }
                return "#error";
            }

            // Process the streaming response
            StringBuilder hashtagBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    try {
                        // Parse each JSON response line
                        JsonObject responseJson = gson.fromJson(responseLine, JsonObject.class);

                        if (responseJson.has("response")) {
                            String responsePart = responseJson.get("response").getAsString();
                            hashtagBuilder.append(responsePart);
                        }

                        // Check if this is the end of the stream
                        if (responseJson.has("done") && responseJson.get("done").getAsBoolean()) {
                            break;
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing response line: " + responseLine);
                    }
                }
            }

            // Process the generated hashtag
            String generatedText = hashtagBuilder.toString().trim();

            // Try to extract a single hashtag
            String hashtag;
            if (generatedText.startsWith("#")) {
                // Extract just the first hashtag if multiple are returned
                int endIndex = generatedText.indexOf(" ");
                if (endIndex > 0) {
                    hashtag = generatedText.substring(0, endIndex);
                } else {
                    hashtag = generatedText;
                }
            } else {
                // If no hashtag format, create one from the first word
                int endIndex = generatedText.indexOf(" ");
                if (endIndex > 0) {
                    hashtag = "#" + generatedText.substring(0, endIndex).replaceAll("[^a-zA-Z0-9]", "");
                } else {
                    hashtag = "#" + generatedText.replaceAll("[^a-zA-Z0-9]", "");
                }
            }

            // If hashtag is empty or just #, use a fallback
            if (hashtag.equals("#") || hashtag.length() <= 1) {
                // Use keywords from text to create a simple hashtag
                if (cleanedText.toLowerCase().contains("bluesky")) {
                    return "#bskypost";
                } else if (cleanedText.toLowerCase().contains("atproto")) {
                    return "#bskypost";
                } else {
                    return "#bskypost";
                }
            }

            System.out.println("Generated hashtag: " + hashtag);
            return hashtag;

        } catch (Exception e) {
            System.err.println("Error calling LLAMA model: " + e.getMessage());
            e.printStackTrace();

            // Fallback to a simple hashtag based on keywords
            if (text.toLowerCase().contains("bluesky")) {
                return "#bskypost";
            } else if (text.toLowerCase().contains("atproto")) {
                return "#bskypost";
            } else {
                return "#bskypost";
            }
        }
    }
}