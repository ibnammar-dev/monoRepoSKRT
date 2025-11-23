package com.example.skrtdesk.service;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.ApiResponse;
import com.example.skrtdesk.model.Comment;
import com.example.skrtdesk.util.Constants;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentService extends ApiService {
    private static CommentService instance;
    
    private CommentService() {
        super();
    }
    
    public static CommentService getInstance() {
        if (instance == null) {
            instance = new CommentService();
        }
        return instance;
    }
    
    public Map<String, Object> getComments(Long postId) throws ApiException {
        String endpoint = String.format(Constants.ENDPOINT_POST_COMMENTS, postId);
        Type responseType = new TypeToken<ApiResponse<Map<String, Object>>>(){}.getType();
        ApiResponse<Map<String, Object>> response = get(endpoint, responseType);
        
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
    
    public Comment createComment(Long postId, String text) throws ApiException {
        Map<String, String> commentData = new HashMap<>();
        commentData.put("text", text);
        
        String endpoint = String.format(Constants.ENDPOINT_POST_COMMENTS, postId);
        Type responseType = new TypeToken<ApiResponse<Comment>>(){}.getType();
        ApiResponse<Comment> response = post(endpoint, commentData, responseType);
        
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
    
    public void deleteComment(Long commentId) throws ApiException {
        String endpoint = String.format(Constants.ENDPOINT_COMMENT, commentId);
        delete(endpoint);
    }
}

