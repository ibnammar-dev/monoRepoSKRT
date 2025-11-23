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
        
        // Set minimum and preferred window size
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        
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

