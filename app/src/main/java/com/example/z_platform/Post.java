package com.example.z_platform;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Post {

    private String body, name, username, profileImage, createdAt;

    public Post(String body, String name, String username, String profileImage, String createdAt) {
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
        return '@' + username;
    }
    public String getProfileImage() {return profileImage;}

    public String getCreatedAt() {
        return dateFormatter(createdAt);
    }

    public static String dateFormatter(String date) {
        OffsetDateTime inst = OffsetDateTime.ofInstant(Instant.parse(date),
                ZoneId.systemDefault());

        return (DateTimeFormatter.ofPattern("MMM dd, yyyy").format(inst));
    }
}
