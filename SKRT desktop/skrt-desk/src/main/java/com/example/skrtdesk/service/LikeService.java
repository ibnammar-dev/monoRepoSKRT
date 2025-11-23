package com.example.skrtdesk.service;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.ApiResponse;
import com.example.skrtdesk.model.PostLike;
import com.example.skrtdesk.util.Constants;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class LikeService extends ApiService {
    private static LikeService instance;
    
    private LikeService() {
        super();
    }
    
    public static LikeService getInstance() {
        if (instance == null) {
            instance = new LikeService();
        }
        return instance;
    }
    
    public Map<String, Object> toggleLike(Long postId) throws ApiException {
        String endpoint = String.format(Constants.ENDPOINT_POST_LIKE, postId);
        Type responseType = new TypeToken<ApiResponse<Map<String, Object>>>(){}.getType();
        ApiResponse<Map<String, Object>> response = post(endpoint, null, responseType);
        
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
    
    public Map<String, Object> getLikes(Long postId) throws ApiException {
        String endpoint = String.format(Constants.ENDPOINT_POST_LIKES, postId);
        Type responseType = new TypeToken<ApiResponse<Map<String, Object>>>(){}.getType();
        ApiResponse<Map<String, Object>> response = get(endpoint, responseType);
        
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
}

