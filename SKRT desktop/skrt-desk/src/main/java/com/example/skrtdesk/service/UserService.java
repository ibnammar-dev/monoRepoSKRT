package com.example.skrtdesk.service;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.ApiResponse;
import com.example.skrtdesk.model.User;
import com.example.skrtdesk.util.Constants;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService extends ApiService {
    private static UserService instance;
    
    private UserService() {
        super();
    }
    
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    public List<User> getUsers() throws ApiException {
        Type responseType = new TypeToken<ApiResponse<Map<String, List<User>>>>(){}.getType();
        ApiResponse<Map<String, List<User>>> response = get(Constants.ENDPOINT_USERS, responseType);
        
        if (response.isSuccess() && response.getData() != null) {
            return response.getData().get("users");
        }
        return null;
    }
    
    public User getUser(Long userId) throws ApiException {
        String endpoint = String.format(Constants.ENDPOINT_USER, userId);
        Type responseType = new TypeToken<ApiResponse<User>>(){}.getType();
        ApiResponse<User> response = get(endpoint, responseType);
        
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
    
    public User updateUser(Long userId, String email, String firstName, String lastName) throws ApiException {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        
        String endpoint = String.format(Constants.ENDPOINT_USER, userId);
        Type responseType = new TypeToken<ApiResponse<User>>(){}.getType();
        ApiResponse<User> response = put(endpoint, userData, responseType);
        
        if (response.isSuccess()) {
            return response.getData();
        }
        return null;
    }
    
    public void deleteUser(Long userId) throws ApiException {
        String endpoint = String.format(Constants.ENDPOINT_USER, userId);
        delete(endpoint);
    }
}

