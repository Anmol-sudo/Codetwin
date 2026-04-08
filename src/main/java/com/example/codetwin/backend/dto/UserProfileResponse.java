package com.example.codetwin.backend.dto;

public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private long postCount;

    public UserProfileResponse(Long id, String username, String email, long postCount) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.postCount = postCount;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public long getPostCount() { return postCount; }
    public void setPostCount(long postCount) { this.postCount = postCount; }
}