package com.example.skrtdesk.util;

import com.example.skrtdesk.model.User;

public class SessionManager {
    private static SessionManager instance;
    
    private String apiToken;
    private User currentUser;
    
    private SessionManager() {
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void setSession(String apiToken, User currentUser) {
        this.apiToken = apiToken;
        this.currentUser = currentUser;
    }
    
    public void clearSession() {
        this.apiToken = null;
        this.currentUser = null;
    }
    
    public boolean isAuthenticated() {
        return apiToken != null && currentUser != null;
    }
    
    public String getApiToken() {
        return apiToken;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    
    public boolean isAdmin() {
        return currentUser != null && 
               currentUser.getRoles() != null && 
               currentUser.getRoles().contains("ROLE_ADMIN");
    }
}

