package com.example.skrtdesk.controller;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.Comment;
import com.example.skrtdesk.model.Post;
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

public class PostDetailController extends BaseController {
    
    @FXML private VBox postContainer;
    @FXML private VBox commentsContainer;
    @FXML private TextField commentField;
    
    private Post currentPost;
    
    @FXML
    public void initialize() {
    }
    
    public void setPost(Post post) {
        this.currentPost = post;
        displayPost();
        loadComments();
    }
    
    private void displayPost() {
        postContainer.getChildren().clear();
        
        if (currentPost == null) return;
        
        // Author info
        HBox authorBox = new HBox(10);
        authorBox.setAlignment(Pos.CENTER_LEFT);
        
        Label authorLabel = new Label(currentPost.getAuthor().getFullName());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label timeLabel = new Label(formatTime(currentPost.getCreatedAt()));
        timeLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        
        authorBox.getChildren().addAll(authorLabel, new Label("•"), timeLabel);
        
        // Post text
        Text textContent = new Text(currentPost.getText());
        textContent.setWrappingWidth(800);
        textContent.setStyle("-fx-font-size: 14px;");
        
        postContainer.getChildren().addAll(authorBox, textContent);
        
        // Image if exists
        if (currentPost.getImageUrl() != null && !currentPost.getImageUrl().isEmpty()) {
            ImageView imageView = new ImageView();
            imageView.setFitWidth(700);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            
            // Use ImageLoader utility
            com.example.skrtdesk.util.ImageLoader.getInstance().loadImage(
                currentPost.getImageUrl(), imageView, 700
            );
            
            postContainer.getChildren().add(imageView);
        }
        
        // Like info
        HBox likeBox = new HBox(15);
        likeBox.setAlignment(Pos.CENTER_LEFT);
        likeBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button likeButton = new Button(currentPost.isLiked() ? "❤ Unlike" : "♡ Like");
        likeButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px;");
        Label likesLabel = new Label(currentPost.getLikesCount() + " likes");
        likesLabel.setStyle("-fx-text-fill: #666;");
        
        likeButton.setOnAction(e -> handleLike(likeButton, likesLabel));
        
        likeBox.getChildren().addAll(likeButton, likesLabel);
        postContainer.getChildren().add(likeBox);
    }
    
    private void loadComments() {
        commentsContainer.getChildren().clear();
        
        Label loadingLabel = new Label("Loading comments...");
        loadingLabel.setStyle("-fx-text-fill: #666;");
        commentsContainer.getChildren().add(loadingLabel);
        
        runAsync(() -> {
            try {
                Map<String, Object> response = commentService.getComments(currentPost.getId());
                
                if (response != null) {
                    Platform.runLater(() -> {
                        commentsContainer.getChildren().clear();
                        
                        List<Map<String, Object>> commentsData = (List<Map<String, Object>>) response.get("comments");
                        
                        if (commentsData == null || commentsData.isEmpty()) {
                            Label emptyLabel = new Label("No comments yet. Be the first to comment!");
                            emptyLabel.setStyle("-fx-text-fill: #666;");
                            commentsContainer.getChildren().add(emptyLabel);
                        } else {
                            Gson gson = new Gson();
                            for (Map<String, Object> commentData : commentsData) {
                                Comment comment = gson.fromJson(gson.toJson(commentData), Comment.class);
                                commentsContainer.getChildren().add(createCommentCard(comment));
                            }
                        }
                    });
                }
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    showError("Error", "Failed to load comments: " + e.getMessage());
                });
            }
        });
    }
    
    private VBox createCommentCard(Comment comment) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label authorLabel = new Label(comment.getUser().getFullName());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        
        Label timeLabel = new Label(formatTime(comment.getCreatedAt()));
        timeLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
        
        headerBox.getChildren().addAll(authorLabel, new Label("•"), timeLabel);
        
        // Delete button if can delete
        if (comment.isCanDelete()) {
            Button deleteButton = new Button("🗑");
            deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-cursor: hand;");
            deleteButton.setOnAction(e -> handleDeleteComment(comment));
            headerBox.getChildren().add(deleteButton);
        }
        
        Text commentText = new Text(comment.getText());
        commentText.setWrappingWidth(750);
        commentText.setStyle("-fx-font-size: 13px;");
        
        card.getChildren().addAll(headerBox, commentText);
        
        return card;
    }
    
    @FXML
    private void handlePostComment() {
        String text = commentField.getText().trim();
        
        if (text.isEmpty()) {
            showWarning("Warning", "Please enter a comment");
            return;
        }
        
        commentField.setDisable(true);
        
        runAsync(() -> {
            try {
                Comment comment = commentService.createComment(currentPost.getId(), text);
                
                if (comment != null) {
                    Platform.runLater(() -> {
                        commentField.clear();
                        commentField.setDisable(false);
                        loadComments();
                        // Update comment count
                        currentPost.setCommentsCount(currentPost.getCommentsCount() + 1);
                    });
                }
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    showError("Error", "Failed to post comment: " + e.getMessage());
                    commentField.setDisable(false);
                });
            }
        });
    }
    
    private void handleLike(Button likeButton, Label likesLabel) {
        runAsync(() -> {
            try {
                Map<String, Object> result = likeService.toggleLike(currentPost.getId());
                
                if (result != null) {
                    Platform.runLater(() -> {
                        boolean liked = (boolean) result.get("liked");
                        Double likesCountDouble = (Double) result.get("likesCount");
                        int likesCount = likesCountDouble != null ? likesCountDouble.intValue() : 0;
                        
                        currentPost.setLiked(liked);
                        currentPost.setLikesCount(likesCount);
                        
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
    
    private void handleDeleteComment(Comment comment) {
        if (showConfirmation("Delete Comment", "Are you sure you want to delete this comment?")) {
            runAsync(() -> {
                try {
                    commentService.deleteComment(comment.getId());
                    Platform.runLater(() -> {
                        showSuccess("Success", "Comment deleted");
                        loadComments();
                        currentPost.setCommentsCount(currentPost.getCommentsCount() - 1);
                    });
                } catch (ApiException e) {
                    Platform.runLater(() -> {
                        showError("Error", "Failed to delete comment: " + e.getMessage());
                    });
                }
            });
        }
    }
    
    private String formatTime(String createdAt) {
        if (createdAt == null) return "";
        return createdAt.substring(0, Math.min(10, createdAt.length()));
    }
    
    @FXML
    private void handleBack() {
        viewManager.showMainFeed();
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
    private void handleLogout() {
        if (showConfirmation("Logout", "Are you sure you want to logout?")) {
            sessionManager.clearSession();
            viewManager.showLogin();
        }
    }
}

