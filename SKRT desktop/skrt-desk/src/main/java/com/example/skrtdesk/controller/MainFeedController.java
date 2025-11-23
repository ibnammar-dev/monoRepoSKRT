package com.example.skrtdesk.controller;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.Post;
import com.example.skrtdesk.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class MainFeedController extends BaseController {
    
    @FXML private VBox postsContainer;
    @FXML private Button adminButton;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    @FXML private HBox impersonationBanner;
    @FXML private Label impersonationLabel;
    
    private int currentPage = 1;
    private int totalPages = 1;
    private static final int POSTS_PER_PAGE = 10;
    
    @FXML
    public void initialize() {
        // Show admin button if original user is admin (not current impersonated user)
        if (sessionManager.isImpersonating()) {
            if (sessionManager.getOriginalUser() != null && 
                sessionManager.getOriginalUser().getRoles() != null &&
                sessionManager.getOriginalUser().getRoles().contains("ROLE_ADMIN")) {
                adminButton.setVisible(true);
                adminButton.setManaged(true);
            }
            
            // Show impersonation banner
            impersonationBanner.setVisible(true);
            impersonationBanner.setManaged(true);
            impersonationLabel.setText("⚠ Viewing as " + sessionManager.getCurrentUser().getFullName());
        } else if (sessionManager.isAdmin()) {
            adminButton.setVisible(true);
            adminButton.setManaged(true);
        }
        
        loadPosts();
    }
    
    private void loadPosts() {
        postsContainer.getChildren().clear();
        
        // Show loading indicator
        Label loadingLabel = new Label("Loading posts...");
        loadingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        postsContainer.getChildren().add(loadingLabel);
        
        runAsync(() -> {
            try {
                Map<String, Object> response = postService.getPosts(currentPage, POSTS_PER_PAGE);
                
                if (response != null) {
                    Platform.runLater(() -> {
                        postsContainer.getChildren().clear();
                        
                        List<Map<String, Object>> postsData = (List<Map<String, Object>>) response.get("posts");
                        
                        if (postsData == null || postsData.isEmpty()) {
                            Label emptyLabel = new Label("No posts yet. Be the first to post!");
                            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
                            VBox emptyBox = new VBox(emptyLabel);
                            emptyBox.setAlignment(Pos.CENTER);
                            emptyBox.setPadding(new Insets(50));
                            postsContainer.getChildren().add(emptyBox);
                        } else {
                            Gson gson = new Gson();
                            for (Map<String, Object> postData : postsData) {
                                Post post = gson.fromJson(gson.toJson(postData), Post.class);
                                postsContainer.getChildren().add(createPostCard(post));
                            }
                        }
                        
                        // Update pagination
                        Map<String, Object> pagination = (Map<String, Object>) response.get("pagination");
                        if (pagination != null) {
                            Double totalPagesDouble = (Double) pagination.get("pages");
                            totalPages = totalPagesDouble != null ? totalPagesDouble.intValue() : 1;
                            updatePaginationControls();
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
        
        // Author info
        HBox authorBox = new HBox(10);
        authorBox.setAlignment(Pos.CENTER_LEFT);
        
        Label authorLabel = new Label(post.getAuthor().getFullName());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label timeLabel = new Label(formatTime(post.getCreatedAt()));
        timeLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        
        authorBox.getChildren().addAll(authorLabel, new Label("•"), timeLabel);
        
        // Post text
        Text textContent = new Text(post.getText());
        textContent.setWrappingWidth(800);
        textContent.setStyle("-fx-font-size: 14px;");
        
        card.getChildren().addAll(authorBox, textContent);
        
        // Image if exists
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
        
        // Actions
        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER_LEFT);
        
        Button likeButton = new Button(post.isLiked() ? "❤ Unlike" : "♡ Like");
        likeButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        Label likesLabel = new Label(post.getLikesCount() + " likes");
        likesLabel.setStyle("-fx-text-fill: #666;");
        
        likeButton.setOnAction(e -> handleLike(post, likeButton, likesLabel));
        
        Button commentsButton = new Button("💬 Comments (" + post.getCommentsCount() + ")");
        commentsButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        commentsButton.setOnAction(e -> viewManager.showPostDetail(post));
        
        actionsBox.getChildren().addAll(likeButton, likesLabel, new Label("|"), commentsButton);
        
        // Delete button for own posts or admin
        if (sessionManager.getCurrentUser().getId().equals(post.getAuthor().getId()) || 
            sessionManager.isAdmin()) {
            Button deleteButton = new Button("🗑 Delete");
            deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-cursor: hand;");
            deleteButton.setOnAction(e -> handleDeletePost(post));
            actionsBox.getChildren().addAll(new Label("|"), deleteButton);
        }
        
        card.getChildren().add(actionsBox);
        
        return card;
    }
    
    private void handleLike(Post post, Button likeButton, Label likesLabel) {
        runAsync(() -> {
            try {
                Map<String, Object> result = likeService.toggleLike(post.getId());
                if (result != null) {
                    Platform.runLater(() -> {
                        boolean liked = (boolean) result.get("liked");
                        Double likesCountDouble = (Double) result.get("likesCount");
                        int likesCount = likesCountDouble != null ? likesCountDouble.intValue() : 0;
                        
                        likeButton.setText(liked ? "❤ Unlike" : "♡ Like");
                        likesLabel.setText(likesCount + " likes");
                    });
                }
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    showError("Error", "Failed to toggle like: " + e.getMessage());
                });
            }
        });
    }
    
    private void handleDeletePost(Post post) {
        if (showConfirmation("Delete Post", "Are you sure you want to delete this post?")) {
            runAsync(() -> {
                try {
                    postService.deletePost(post.getId());
                    Platform.runLater(() -> {
                        showSuccess("Success", "Post deleted successfully");
                        loadPosts();
                    });
                } catch (ApiException e) {
                    Platform.runLater(() -> {
                        showError("Error", "Failed to delete post: " + e.getMessage());
                    });
                }
            });
        }
    }
    
    private String formatTime(String createdAt) {
        if (createdAt == null) return "";
        // Simple formatting - just return the date part for now
        return createdAt.substring(0, Math.min(10, createdAt.length()));
    }
    
    private void updatePaginationControls() {
        pageLabel.setText("Page " + currentPage + " of " + totalPages);
        prevButton.setDisable(currentPage <= 1);
        nextButton.setDisable(currentPage >= totalPages);
    }
    
    @FXML
    private void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            loadPosts();
        }
    }
    
    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadPosts();
        }
    }
    
    @FXML
    private void handleCreatePost() {
        viewManager.showCreatePostDialog();
        // Refresh feed after dialog closes
        loadPosts();
    }
    
    @FXML
    private void handleFeed() {
        // Already on feed
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
    private void handleExitImpersonation() {
        sessionManager.stopImpersonation();
        viewManager.showMainFeed();
    }
    
    @FXML
    private void handleLogout() {
        if (showConfirmation("Logout", "Are you sure you want to logout?")) {
            runAsync(() -> {
                try {
                    authService.logout();
                } catch (ApiException e) {
                    // Ignore logout errors
                }
                
                Platform.runLater(() -> {
                    sessionManager.clearSession();
                    viewManager.showLogin();
                });
            });
        }
    }
}

