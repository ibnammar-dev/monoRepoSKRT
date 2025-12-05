package com.example.skrtdesk;

import com.example.skrtdesk.util.Constants;
import com.example.skrtdesk.util.HttpClientUtil;
import com.example.skrtdesk.util.SessionManager;
import com.example.skrtdesk.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class SkrtApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        ViewManager viewManager = ViewManager.getInstance();
        viewManager.setPrimaryStage(primaryStage);
        
        // Set fixed window dimensions
        primaryStage.setWidth(1280);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
        primaryStage.setMaxWidth(1920);
        primaryStage.setMaxHeight(1080);
        
        // Check if user is already logged in
        if (SessionManager.getInstance().isAuthenticated()) {
            viewManager.showMainFeed();
        } else {
            viewManager.showLogin();
        }
    }
    
    @Override
    public void stop() {
        HttpClientUtil.close();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

