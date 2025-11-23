package com.example.skrtdesk.controller;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.Post;
import com.example.skrtdesk.model.User;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Map;

public class AdminController extends BaseController {
    
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Long> userIdColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userFirstNameColumn;
    @FXML private TableColumn<User, String> userLastNameColumn;
    @FXML private TableColumn<User, String> userRolesColumn;
    @FXML private TableColumn<User, Void> userActionsColumn;
    
    @FXML private TableView<Post> postsTable;
    @FXML private TableColumn<Post, Long> postIdColumn;
    @FXML private TableColumn<Post, String> postAuthorColumn;
    @FXML private TableColumn<Post, String> postTextColumn;
    @FXML private TableColumn<Post, Integer> postLikesColumn;
    @FXML private TableColumn<Post, Integer> postCommentsColumn;
    @FXML private TableColumn<Post, Void> postActionsColumn;
    
    @FXML
    public void initialize() {
        // Check if user is admin
        if (!sessionManager.isAdmin()) {
            showError("Access Denied", "You don't have permission to access this page");
            viewManager.showMainFeed();
            return;
        }
        
        setupUsersTable();
        setupPostsTable();
        
        loadUsers();
        loadPosts();
    }
    
    private void setupUsersTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        
        userRolesColumn.setCellValueFactory(cellData -> {
            List<String> roles = cellData.getValue().getRoles();
            String rolesStr = roles != null ? String.join(", ", roles) : "";
            return new javafx.beans.property.SimpleStringProperty(rolesStr);
        });
        
        // Add delete button
        userActionsColumn.setCellFactory(col -> {
            TableCell<User, Void> cell = new TableCell<>() {
                private final Button deleteButton = new Button("Delete");
                
                {
                    deleteButton.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        handleDeleteUser(user);
                    });
                    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        User user = getTableView().getItems().get(getIndex());
                        // Don't allow deleting yourself
                        if (user.getId().equals(sessionManager.getCurrentUser().getId())) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                }
            };
            return cell;
        });
    }
    
    private void setupPostsTable() {
        postIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        postAuthorColumn.setCellValueFactory(cellData -> {
            User author = cellData.getValue().getAuthor();
            String authorName = author != null ? author.getFullName() : "";
            return new javafx.beans.property.SimpleStringProperty(authorName);
        });
        
        postTextColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        postLikesColumn.setCellValueFactory(new PropertyValueFactory<>("likesCount"));
        postCommentsColumn.setCellValueFactory(new PropertyValueFactory<>("commentsCount"));
        
        // Add actions buttons
        postActionsColumn.setCellFactory(col -> {
            TableCell<Post, Void> cell = new TableCell<>() {
                private final Button deleteButton = new Button("Delete");
                
                {
                    deleteButton.setOnAction(event -> {
                        Post post = getTableView().getItems().get(getIndex());
                        handleDeletePost(post);
                    });
                    deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : deleteButton);
                }
            };
            return cell;
        });
    }
    
    private void loadUsers() {
        runAsync(() -> {
            try {
                List<User> users = userService.getUsers();
                
                if (users != null) {
                    Platform.runLater(() -> {
                        ObservableList<User> userList = FXCollections.observableArrayList(users);
                        usersTable.setItems(userList);
                    });
                }
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    showError("Error", "Failed to load users: " + e.getMessage());
                });
            }
        });
    }
    
    private void loadPosts() {
        runAsync(() -> {
            try {
                Map<String, Object> response = postService.getPosts(1, 100);
                
                if (response != null) {
                    List<Map<String, Object>> postsData = (List<Map<String, Object>>) response.get("posts");
                    
                    if (postsData != null) {
                        Gson gson = new Gson();
                        List<Post> posts = postsData.stream()
                            .map(postData -> gson.fromJson(gson.toJson(postData), Post.class))
                            .toList();
                        
                        Platform.runLater(() -> {
                            ObservableList<Post> postList = FXCollections.observableArrayList(posts);
                            postsTable.setItems(postList);
                        });
                    }
                }
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    showError("Error", "Failed to load posts: " + e.getMessage());
                });
            }
        });
    }
    
    private void handleDeleteUser(User user) {
        if (showConfirmation("Delete User", 
            "Are you sure you want to delete user: " + user.getFullName() + "?")) {
            runAsync(() -> {
                try {
                    userService.deleteUser(user.getId());
                    Platform.runLater(() -> {
                        showSuccess("Success", "User deleted successfully");
                        loadUsers();
                    });
                } catch (ApiException e) {
                    Platform.runLater(() -> {
                        showError("Error", "Failed to delete user: " + e.getMessage());
                    });
                }
            });
        }
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
    
    @FXML
    private void handleRefreshUsers() {
        loadUsers();
    }
    
    @FXML
    private void handleRefreshPosts() {
        loadPosts();
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

