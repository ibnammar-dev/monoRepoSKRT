package com.example.skrtdesk.model;

public class Comment {
    private Long id;
    private String text;
    private User user;
    private String createdAt;
    private boolean canDelete;
    
    public Comment() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isCanDelete() {
        return canDelete;
    }
    
    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }
    
    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", user=" + user +
                '}';
    }
}

