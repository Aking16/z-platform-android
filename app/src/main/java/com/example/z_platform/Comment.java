package com.example.z_platform;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Comment {
    private String body, name, username, profileImage, createdAt;

    public Comment(String body, String name, String username, String profileImage, String createdAt) {
        this.body = body;
        this.name = name;
        this.username = username;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
    }

    public String getBody() {
        return body;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getCreatedAt() {
        return dateFormatter(createdAt);
    }

    public static String dateFormatter(String date) {
        OffsetDateTime inst = OffsetDateTime.ofInstant(Instant.parse(date), ZoneId.systemDefault());

        return (DateTimeFormatter.ofPattern("MMM dd, yyyy").format(inst));
    }
}
