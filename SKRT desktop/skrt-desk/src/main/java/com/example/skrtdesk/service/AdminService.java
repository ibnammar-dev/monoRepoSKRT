package com.example.skrtdesk.service;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.ApiResponse;
import com.example.skrtdesk.model.AuthToken;
import com.example.skrtdesk.util.Constants;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class AdminService extends ApiService {
    private static AdminService instance;
    
    private AdminService() {
        super();
    }
    
    public static AdminService getInstance() {
        if (instance == null) {
            instance = new AdminService();
        }
        return instance;
    }
    
    public ApiResponse<AuthToken> impersonateUser(Long userId) throws ApiException {
        String endpoint = String.format("/api/admin/impersonate/%d", userId);
        Type responseType = new TypeToken<ApiResponse<AuthToken>>(){}.getType();
        return post(endpoint, null, responseType);
    }
}

