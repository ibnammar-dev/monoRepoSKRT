package com.example.skrtdesk.controller;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.Post;
import com.example.skrtdesk.model.User;
import com.example.skrtdesk.util.Constants;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;

public class ProfileController extends BaseController {
    
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label joinedLabel;
    @FXML private Button editButton;
    @FXML private Button adminButton;
    @FXML private VBox postsContainer;
    
    private User displayUser;
    
    @FXML
    public void initialize() {
        if (sessionManager.isAdmin()) {
            adminButton.setVisible(true);
            adminButton.setManaged(true);
        }
    }
    
    public void setUser(User user) {
        this.displayUser = user;
        displayProfile();
        loadUserPosts();
    }
    
    private void displayProfile() {
        if (displayUser == null) return;
        
        nameLabel.setText(displayUser.getFullName());
        emailLabel.setText(displayUser.getEmail());
        
        if (displayUser.getCreatedAt() != null) {
            joinedLabel.setText("Joined: " + displayUser.getCreatedAt().substring(0, 10));
        }
        
        // Show edit button only for own profile
        if (sessionManager.getCurrentUser().getId().equals(displayUser.getId())) {
            editButton.setVisible(true);
            editButton.setManaged(true);
        }
    }
    
    private void loadUserPosts() {
        postsContainer.getChildren().clear();
        
        Label loadingLabel = new Label("Loading posts...");
        loadingLabel.setStyle("-fx-text-fill: #666;");
        postsContainer.getChildren().add(loadingLabel);
        
        runAsync(() -> {
            try {
                // Get all posts and filter by user
                Map<String, Object> response = postService.getPosts(1, 100);
                
                if (response != null) {
                    Platform.runLater(() -> {
                        postsContainer.getChildren().clear();
                        
                        List<Map<String, Object>> postsData = (List<Map<String, Object>>) response.get("posts");
                        
                        if (postsData != null) {
                            Gson gson = new Gson();
                            List<Post> userPosts = postsData.stream()
                                .map(postData -> gson.fromJson(gson.toJson(postData), Post.class))
                                .filter(post -> post.getAuthor().getId().equals(displayUser.getId()))
                                .toList();
                            
                            if (userPosts.isEmpty()) {
                                Label emptyLabel = new Label("No posts yet");
                                emptyLabel.setStyle("-fx-text-fill: #666;");
                                postsContainer.getChildren().add(emptyLabel);
                            } else {
                                for (Post post : userPosts) {
                                    postsContainer.getChildren().add(createPostCard(post));
                                }
                            }
                        }
                    });
                }
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    showError("Error", "Failed to load posts: " + e.getMessage());
                });
            }
        });
    }
    
    private VBox createPostCard(Post post) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #ddd; " +
                     "-fx-border-radius: 5; -fx-background-radius: 5;");
        
        Label timeLabel = new Label(formatTime(post.getCreatedAt()));
        timeLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        
        Text textContent = new Text(post.getText());
        textContent.setWrappingWidth(800);
        textContent.setStyle("-fx-font-size: 14px;");
        
        card.getChildren().addAll(timeLabel, textContent);
        
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(600);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            
            // Use ImageLoader utility
            com.example.skrtdesk.util.ImageLoader.getInstance().loadImage(
                post.getImageUrl(), imageView, 600
            );
            
            card.getChildren().add(imageView);
        }
        
        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER_LEFT);
        
        Label likesLabel = new Label(post.getLikesCount() + " likes");
        Label commentsLabel = new Label(post.getCommentsCount() + " comments");
        
        Button viewButton = new Button("View Details");
        viewButton.setOnAction(e -> viewManager.showPostDetail(post));
        
        actionsBox.getChildren().addAll(likesLabel, new Label("|"), commentsLabel, new Label("|"), viewButton);
        
        card.getChildren().add(actionsBox);
        
        return card;
    }
    
    private String formatTime(String createdAt) {
        if (createdAt == null) return "";
        return createdAt.substring(0, Math.min(10, createdAt.length()));
    }
    
    @FXML
    private void handleEditProfile() {
        viewManager.showEditProfile();
    }
    
    @FXML
    private void handleFeed() {
        viewManager.showMainFeed();
    }
    
    @FXML
    private void handleProfile() {
        viewManager.showProfile(sessionManager.getCurrentUser());
    }
    
    @FXML
    private void handleAdmin() {
        viewManager.showAdminDashboard();
    }
    
    @FXML
    private void handleLogout() {
        if (showConfirmation("Logout", "Are you sure you want to logout?")) {
            sessionManager.clearSession();
            viewManager.showLogin();
        }
    }
}

