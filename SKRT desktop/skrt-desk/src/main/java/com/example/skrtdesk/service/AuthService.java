package com.example.skrtdesk.service;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.ApiResponse;
import com.example.skrtdesk.model.AuthToken;
import com.example.skrtdesk.util.Constants;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AuthService extends ApiService {
    private static AuthService instance;
    
    private AuthService() {
        super();
    }
    
    public static AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }
    
    public ApiResponse<AuthToken> login(String email, String password) throws ApiException {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);
        
        Type responseType = new TypeToken<ApiResponse<AuthToken>>(){}.getType();
        return post(Constants.ENDPOINT_LOGIN, credentials, responseType);
    }
    
    public ApiResponse<Map<String, Object>> register(String email, String password, 
                                                       String firstName, String lastName) throws ApiException {
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        
        Type responseType = new TypeToken<ApiResponse<Map<String, Object>>>(){}.getType();
        return post(Constants.ENDPOINT_REGISTER, userData, responseType);
    }
    
    public ApiResponse<Map<String, Object>> logout() throws ApiException {
        Type responseType = new TypeToken<ApiResponse<Map<String, Object>>>(){}.getType();
        return post(Constants.ENDPOINT_LOGOUT, null, responseType);
    }
}

