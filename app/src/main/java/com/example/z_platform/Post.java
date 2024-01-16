package com.example.z_platform;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Post {

    private String postId, userId, body, name, username, profileImage, createdAt, commentCount;

    public Post(String postId, String userId, String body, String name, String username, String profileImage,
                String createdAt, String commentCount) {
        this.postId = postId;
        this.userId = userId;
        this.body = body;
        this.name = name;
        this.username = username;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
        this.commentCount = commentCount;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getBody() {
        return body;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return '@' + username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getCreatedAt() {
        return dateFormatter(createdAt);
    }

    public String getCommentCount() {
        return commentCount;
    }

    public static String dateFormatter(String date) {
        OffsetDateTime inst = OffsetDateTime.ofInstant(Instant.parse(date), ZoneId.systemDefault());

        return (DateTimeFormatter.ofPattern("MMM dd, yyyy").format(inst));
    }
}
