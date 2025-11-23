package com.example.skrtdesk.util;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader {
    private static ImageLoader instance;
    
    private ImageLoader() {
    }
    
    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }
    
    /**
     * Load image asynchronously with proper error handling
     */
    public void loadImage(String imageUrl, ImageView imageView, double width) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            setPlaceholder(imageView, width);
            return;
        }
        
        // Check cache first
        ImageCache cache = ImageCache.getInstance();
        if (cache.contains(imageUrl)) {
            Image cachedImage = cache.get(imageUrl);
            if (cachedImage != null) {
                Platform.runLater(() -> imageView.setImage(cachedImage));
                return;
            }
        }
        
        // Set placeholder while loading
        setPlaceholder(imageView, width);
        
        // Load in background thread
        new Thread(() -> {
            try {
                // Construct full URL
                String fullUrl = imageUrl;
                if (!imageUrl.startsWith("http")) {
                    fullUrl = Constants.API_BASE_URL + imageUrl;
                }
                
                System.out.println("Loading image from: " + fullUrl);
                
                // Create connection with timeout
                URL url = new URL(fullUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                // Add auth token if available
                String token = SessionManager.getInstance().getApiToken();
                if (token != null && !token.isEmpty()) {
                    connection.setRequestProperty(Constants.HEADER_API_TOKEN, token);
                }
                
                connection.connect();
                
                int responseCode = connection.getResponseCode();
                System.out.println("Image response code: " + responseCode + " for " + fullUrl);
                
                if (responseCode == 200) {
                    InputStream inputStream = connection.getInputStream();
                    Image image = new Image(inputStream);
                    
                    if (!image.isError()) {
                        // Cache the image
                        cache.put(imageUrl, image);
                        
                        // Update UI on JavaFX thread
                        Platform.runLater(() -> {
                            imageView.setImage(image);
                        });
                    } else {
                        System.err.println("Image error for: " + fullUrl);
                        setPlaceholder(imageView, width);
                    }
                } else {
                    System.err.println("Failed to load image: " + responseCode + " - " + fullUrl);
                    setPlaceholder(imageView, width);
                }
                
            } catch (Exception e) {
                System.err.println("Exception loading image from " + imageUrl + ": " + e.getMessage());
                e.printStackTrace();
                Platform.runLater(() -> setPlaceholder(imageView, width));
            }
        }).start();
    }
    
    /**
     * Load image synchronously (for small images)
     */
    public Image loadImageSync(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        // Check cache first
        ImageCache cache = ImageCache.getInstance();
        if (cache.contains(imageUrl)) {
            return cache.get(imageUrl);
        }
        
        try {
            String fullUrl = imageUrl;
            if (!imageUrl.startsWith("http")) {
                fullUrl = Constants.API_BASE_URL + imageUrl;
            }
            
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            String token = SessionManager.getInstance().getApiToken();
            if (token != null && !token.isEmpty()) {
                connection.setRequestProperty(Constants.HEADER_API_TOKEN, token);
            }
            
            connection.connect();
            
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                Image image = new Image(inputStream);
                
                if (!image.isError()) {
                    cache.put(imageUrl, image);
                    return image;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Set a placeholder image
     */
    private void setPlaceholder(ImageView imageView, double width) {
        Platform.runLater(() -> {
            // Create a simple placeholder
            imageView.setImage(null);
            imageView.setStyle("-fx-background-color: #e4e6eb; -fx-border-color: #dddfe2;");
        });
    }
}

