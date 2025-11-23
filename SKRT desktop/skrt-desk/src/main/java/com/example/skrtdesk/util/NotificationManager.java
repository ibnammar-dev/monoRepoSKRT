package com.example.skrtdesk.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NotificationManager {
    private static NotificationManager instance;
    private Stage primaryStage;
    private VBox notificationContainer;
    
    private NotificationManager() {
    }
    
    public static NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }
    
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    public void showSuccess(String message) {
        showNotification(message, NotificationType.SUCCESS);
    }
    
    public void showError(String message) {
        showNotification(message, NotificationType.ERROR);
    }
    
    public void showInfo(String message) {
        showNotification(message, NotificationType.INFO);
    }
    
    public void showWarning(String message) {
        showNotification(message, NotificationType.WARNING);
    }
    
    private void showNotification(String message, NotificationType type) {
        Platform.runLater(() -> {
            Label notification = new Label(message);
            notification.setWrapText(true);
            notification.setMaxWidth(400);
            notification.setPrefHeight(60);
            notification.setAlignment(Pos.CENTER);
            notification.setStyle(getStyleForType(type));
            
            // Position at top center
            StackPane notificationPane = new StackPane(notification);
            notificationPane.setAlignment(Pos.TOP_CENTER);
            notificationPane.setStyle("-fx-padding: 20px;");
            
            // Add to scene (assuming root is available)
            if (primaryStage != null && primaryStage.getScene() != null && 
                primaryStage.getScene().getRoot() instanceof StackPane) {
                StackPane root = (StackPane) primaryStage.getScene().getRoot();
                root.getChildren().add(notificationPane);
                
                // Fade in
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notificationPane);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
                
                // Auto hide after 3 seconds
                PauseTransition pause = new PauseTransition(Duration.seconds(3));
                pause.setOnFinished(e -> {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), notificationPane);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(ev -> root.getChildren().remove(notificationPane));
                    fadeOut.play();
                });
                pause.play();
            }
        });
    }
    
    private String getStyleForType(NotificationType type) {
        String baseStyle = "-fx-padding: 15px 20px; -fx-background-radius: 8px; " +
                          "-fx-font-size: 14px; -fx-font-weight: 600; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);";
        
        return switch (type) {
            case SUCCESS -> baseStyle + " -fx-background-color: #d4edda; -fx-text-fill: #155724; " +
                           "-fx-border-color: #c3e6cb; -fx-border-width: 1px;";
            case ERROR -> baseStyle + " -fx-background-color: #f8d7da; -fx-text-fill: #721c24; " +
                         "-fx-border-color: #f5c6cb; -fx-border-width: 1px;";
            case WARNING -> baseStyle + " -fx-background-color: #fff3cd; -fx-text-fill: #856404; " +
                           "-fx-border-color: #ffeaa7; -fx-border-width: 1px;";
            case INFO -> baseStyle + " -fx-background-color: #d1ecf1; -fx-text-fill: #0c5460; " +
                        "-fx-border-color: #bee5eb; -fx-border-width: 1px;";
        };
    }
    
    private enum NotificationType {
        SUCCESS, ERROR, WARNING, INFO
    }
}

