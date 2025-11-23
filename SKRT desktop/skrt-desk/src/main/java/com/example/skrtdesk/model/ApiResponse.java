package com.example.skrtdesk.model;

import com.google.gson.JsonElement;
import java.util.Map;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Map<String, Object> errors;
    
    public ApiResponse() {
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public Map<String, Object> getErrors() {
        return errors;
    }
    
    public void setErrors(Map<String, Object> errors) {
        this.errors = errors;
    }
    
    public String getErrorMessage() {
        if (message != null && !message.isEmpty()) {
            return message;
        }
        if (errors != null && !errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : errors.entrySet()) {
                sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            return sb.toString();
        }
        return "Unknown error";
    }
}

