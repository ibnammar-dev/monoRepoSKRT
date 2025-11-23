package com.example.skrtdesk.service;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.exception.AuthenticationException;
import com.example.skrtdesk.model.ApiResponse;
import com.example.skrtdesk.util.Constants;
import com.example.skrtdesk.util.HttpClientUtil;
import com.example.skrtdesk.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

public abstract class ApiService {
    protected final Gson gson;
    protected final CloseableHttpClient httpClient;
    
    public ApiService() {
        this.gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            .create();
        this.httpClient = HttpClientUtil.getHttpClient();
    }
    
    protected <T> ApiResponse<T> get(String endpoint, Type responseType) throws ApiException {
        HttpGet request = new HttpGet(Constants.API_BASE_URL + endpoint);
        addAuthHeader(request);
        return executeRequest(request, responseType);
    }
    
    protected <T> ApiResponse<T> post(String endpoint, Object body, Type responseType) throws ApiException {
        HttpPost request = new HttpPost(Constants.API_BASE_URL + endpoint);
        addAuthHeader(request);
        
        if (body != null) {
            String jsonBody = gson.toJson(body);
            request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        }
        
        return executeRequest(request, responseType);
    }
    
    protected <T> ApiResponse<T> postMultipart(String endpoint, String textFieldName, String textValue, 
                                                 String fileFieldName, File file, Type responseType) throws ApiException {
        HttpPost request = new HttpPost(Constants.API_BASE_URL + endpoint);
        addAuthHeader(request);
        
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        if (textValue != null) {
            builder.addTextBody(textFieldName, textValue);
        }
        if (file != null) {
            builder.addBinaryBody(fileFieldName, file, ContentType.DEFAULT_BINARY, file.getName());
        }
        
        request.setEntity(builder.build());
        return executeRequest(request, responseType);
    }
    
    protected <T> ApiResponse<T> put(String endpoint, Object body, Type responseType) throws ApiException {
        HttpPut request = new HttpPut(Constants.API_BASE_URL + endpoint);
        addAuthHeader(request);
        
        if (body != null) {
            String jsonBody = gson.toJson(body);
            request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        }
        
        return executeRequest(request, responseType);
    }
    
    protected void delete(String endpoint) throws ApiException {
        HttpDelete request = new HttpDelete(Constants.API_BASE_URL + endpoint);
        addAuthHeader(request);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            if (statusCode >= 400) {
                String responseBody = EntityUtils.toString(response.getEntity());
                throw new ApiException("Delete failed: " + responseBody, statusCode);
            }
        } catch (IOException e) {
            throw new ApiException("Network error: " + e.getMessage(), 0, e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void addAuthHeader(HttpUriRequestBase request) {
        String token = SessionManager.getInstance().getApiToken();
        if (token != null && !token.isEmpty()) {
            request.setHeader(Constants.HEADER_API_TOKEN, token);
        }
        request.setHeader("Accept", "application/json");
    }
    
    private <T> ApiResponse<T> executeRequest(HttpUriRequestBase request, Type responseType) throws ApiException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            HttpEntity entity = response.getEntity();
            String responseBody = entity != null ? EntityUtils.toString(entity) : "";
            
            if (statusCode == 401) {
                throw new AuthenticationException("Authentication failed. Please login again.");
            }
            
            if (statusCode >= 400) {
                ApiResponse<T> errorResponse = gson.fromJson(responseBody, responseType);
                if (errorResponse != null) {
                    throw new ApiException(errorResponse.getErrorMessage(), statusCode);
                }
                throw new ApiException("Request failed with status: " + statusCode, statusCode);
            }
            
            return gson.fromJson(responseBody, responseType);
            
        } catch (IOException e) {
            throw new ApiException("Network error: " + e.getMessage(), 0, e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

