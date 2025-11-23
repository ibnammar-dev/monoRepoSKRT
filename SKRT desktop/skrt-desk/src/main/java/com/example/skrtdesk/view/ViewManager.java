package com.example.skrtdesk.view;

import com.example.skrtdesk.model.Post;
import com.example.skrtdesk.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {
    private static ViewManager instance;
    private Stage primaryStage;
    
    private ViewManager() {
    }
    
    public static ViewManager getInstance() {
        if (instance == null) {
            instance = new ViewManager();
        }
        return instance;
    }
    
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public void showLogin() {
        loadView("/fxml/login.fxml", "SKRT Social - Login");
    }
    
    public void showRegister() {
        loadView("/fxml/register.fxml", "SKRT Social - Register");
    }
    
    public void showMainFeed() {
        loadView("/fxml/main-feed.fxml", "SKRT Social");
    }
    
    public void showProfile(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent root = loader.load();
            
            // Pass user data to controller
            Object controller = loader.getController();
            if (controller != null && controller.getClass().getName().contains("ProfileController")) {
                try {
                    controller.getClass().getMethod("setUser", User.class).invoke(controller, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            Scene scene = new Scene(root);
            
            // Load CSS stylesheet
            try {
                String css = getClass().getResource("/css/main.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.err.println("Warning: Could not load CSS: " + e.getMessage());
            }
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("SKRT Social - Profile");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showEditProfile() {
        loadView("/fxml/edit-profile.fxml", "SKRT Social - Edit Profile");
    }
    
    public void showPostDetail(Post post) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/post-detail.fxml"));
            Parent root = loader.load();
            
            // Pass post data to controller
            Object controller = loader.getController();
            if (controller != null && controller.getClass().getName().contains("PostDetailController")) {
                try {
                    controller.getClass().getMethod("setPost", Post.class).invoke(controller, post);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            Scene scene = new Scene(root);
            
            // Load CSS stylesheet
            try {
                String css = getClass().getResource("/css/main.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.err.println("Warning: Could not load CSS: " + e.getMessage());
            }
            
            primaryStage.setScene(scene);
            primaryStage.setTitle("SKRT Social - Post");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showAdminDashboard() {
        loadView("/fxml/admin-dashboard.fxml", "SKRT Social - Admin Dashboard");
    }
    
    public void showCreatePostDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create-post-dialog.fxml"));
            Parent root = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create Post");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(primaryStage);
            
            Scene scene = new Scene(root);
            
            // Load CSS stylesheet
            try {
                String css = getClass().getResource("/css/main.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.err.println("Warning: Could not load CSS: " + e.getMessage());
            }
            
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadView(String fxmlPath, String title) {
        try {
            // Store current window dimensions
            double currentWidth = primaryStage.getWidth();
            double currentHeight = primaryStage.getHeight();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            // Load CSS stylesheet
            try {
                String css = getClass().getResource("/css/main.css").toExternalForm();
                scene.getStylesheets().add(css);
            } catch (Exception e) {
                System.err.println("Warning: Could not load CSS: " + e.getMessage());
            }
            
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            
            // Restore window dimensions
            if (currentWidth > 0 && currentHeight > 0) {
                primaryStage.setWidth(currentWidth);
                primaryStage.setHeight(currentHeight);
            }
            
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load view: " + fxmlPath);
        }
    }
}

