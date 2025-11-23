package com.example.skrtdesk.util;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    private static ImageCache instance;
    private final Map<String, Image> cache;
    
    private ImageCache() {
        this.cache = new HashMap<>();
    }
    
    public static ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;
    }
    
    public Image get(String url) {
        return cache.get(url);
    }
    
    public void put(String url, Image image) {
        cache.put(url, image);
    }
    
    public boolean contains(String url) {
        return cache.containsKey(url);
    }
    
    public void clear() {
        cache.clear();
    }
}

