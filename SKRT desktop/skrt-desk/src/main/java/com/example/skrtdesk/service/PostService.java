package com.example.skrtdesk.service;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.ApiResponse;
import com.example.skrtdesk.model.Post;
import com.example.skrtdesk.util.Constants;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostService extends ApiService {
    private static PostService instance;
    
    private PostService() {
        super();
    }
    
    public static PostService getInstance() {
        if (instance == null) {
            instance = new PostService();
        }
        return instance;
    }
    
    public Map<String, Object> getPosts(int page, int limit) throws ApiException {
        String endpoint = Constants.ENDPOINT_POSTS + "?page=" + page + "&limit=" + limit;
        Type responseType = new TypeToken<ApiResponse<Map<String, Object>>>(){}.getType();
        ApiResponse<Map<String, Object>> response = get(endpoint, responseType);
        
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
    
    public Post getPost(Long postId) throws ApiException {
        String endpoint = String.format(Constants.ENDPOINT_POST, postId);
        Type responseType = new TypeToken<ApiResponse<Post>>(){}.getType();
        ApiResponse<Post> response = get(endpoint, responseType);
        
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
    
    public Post createPost(String text, File imageFile) throws ApiException {
        Type responseType = new TypeToken<ApiResponse<Post>>(){}.getType();
        
        if (imageFile != null) {
            ApiResponse<Post> response = postMultipart(Constants.ENDPOINT_POSTS, "text", text, 
                                                        "image", imageFile, responseType);
            if (response.isSuccess()) {
                return response.getData();
            }
        } else {
            Map<String, String> postData = new HashMap<>();
            postData.put("text", text);
            ApiResponse<Post> response = post(Constants.ENDPOINT_POSTS, postData, responseType);
            if (response.isSuccess()) {
                return response.getData();
            }
        }
        return null;
    }
    
    public Post updatePost(Long postId, String text, File imageFile) throws ApiException {
        String endpoint = String.format(Constants.ENDPOINT_POST, postId);
        Type responseType = new TypeToken<ApiResponse<Post>>(){}.getType();
        
        if (imageFile != null) {
            ApiResponse<Post> response = postMultipart(endpoint, "text", text, 
                                                        "image", imageFile, responseType);
            if (response.isSuccess()) {
                return response.getData();
            }
        } else {
            Map<String, String> postData = new HashMap<>();
            postData.put("text", text);
            ApiResponse<Post> response = put(endpoint, postData, responseType);
            if (response.isSuccess()) {
                return response.getData();
            }
        }
        return null;
    }
    
    public void deletePost(Long postId) throws ApiException {
        String endpoint = String.format(Constants.ENDPOINT_POST, postId);
        delete(endpoint);
    }
}

