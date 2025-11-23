package com.example.skrtdesk.util;

public class Constants {
    // API Configuration
    public static final String API_BASE_URL = "http://localhost:8000";
    public static final String API_PREFIX = "/api";
    
    // Auth Endpoints
    public static final String ENDPOINT_REGISTER = API_PREFIX + "/auth/register";
    public static final String ENDPOINT_LOGIN = API_PREFIX + "/auth/login";
    public static final String ENDPOINT_LOGOUT = API_PREFIX + "/auth/logout";
    
    // User Endpoints
    public static final String ENDPOINT_USERS = API_PREFIX + "/users";
    public static final String ENDPOINT_USER = API_PREFIX + "/users/%d";
    
    // Post Endpoints
    public static final String ENDPOINT_POSTS = API_PREFIX + "/posts";
    public static final String ENDPOINT_POST = API_PREFIX + "/posts/%d";
    public static final String ENDPOINT_POST_LIKE = API_PREFIX + "/posts/%d/like";
    public static final String ENDPOINT_POST_LIKES = API_PREFIX + "/posts/%d/likes";
    public static final String ENDPOINT_POST_COMMENTS = API_PREFIX + "/posts/%d/comments";
    
    // Comment Endpoints
    public static final String ENDPOINT_COMMENT = API_PREFIX + "/comments/%d";
    
    // Headers
    public static final String HEADER_API_TOKEN = "X-API-TOKEN";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    
    // Application
    public static final String APP_NAME = "SKRT Social";
    public static final String APP_VERSION = "1.0.0";
    
    private Constants() {
        // Prevent instantiation
    }
}

