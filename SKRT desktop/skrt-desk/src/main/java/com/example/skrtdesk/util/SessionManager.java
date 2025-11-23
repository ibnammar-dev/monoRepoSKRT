package com.example.skrtdesk.util;

import com.example.skrtdesk.model.User;

public class SessionManager {
    private static SessionManager instance;
    
    private String apiToken;
    private User currentUser;
    
    // Impersonation state
    private boolean isImpersonating;
    private String originalToken;
    private User originalUser;
    
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
        this.isImpersonating = false;
        this.originalToken = null;
        this.originalUser = null;
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
    
    // Impersonation methods
    public void startImpersonation(String newToken, User newUser) {
        if (!isImpersonating) {
            this.originalToken = this.apiToken;
            this.originalUser = this.currentUser;
            this.isImpersonating = true;
        }
        this.apiToken = newToken;
        this.currentUser = newUser;
    }
    
    public void stopImpersonation() {
        if (isImpersonating && originalToken != null && originalUser != null) {
            this.apiToken = originalToken;
            this.currentUser = originalUser;
            this.isImpersonating = false;
            this.originalToken = null;
            this.originalUser = null;
        }
    }
    
    public boolean isImpersonating() {
        return isImpersonating;
    }
    
    public User getOriginalUser() {
        return originalUser;
    }
}


