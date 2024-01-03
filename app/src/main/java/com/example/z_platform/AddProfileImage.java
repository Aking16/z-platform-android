package com.example.z_platform;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AddProfileImage {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("bio")
    @Expose
    private Object bio;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("hashedPassword")
    @Expose
    private String hashedPassword;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("coverImage")
    @Expose
    private Object coverImage;
    @SerializedName("profileImage")
    @Expose
    private String profileImage;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("followingIds")
    @Expose
    private List<Object> followingIds;
    @SerializedName("hasNotficiation")
    @Expose
    private Object hasNotficiation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getBio() {
        return bio;
    }

    public void setBio(Object bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Object getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Object coverImage) {
        this.coverImage = coverImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Object> getFollowingIds() {
        return followingIds;
    }

    public void setFollowingIds(List<Object> followingIds) {
        this.followingIds = followingIds;
    }

    public Object getHasNotficiation() {
        return hasNotficiation;
    }

    public void setHasNotficiation(Object hasNotficiation) {
        this.hasNotficiation = hasNotficiation;
    }

}
