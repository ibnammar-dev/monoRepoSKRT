package com.example.skrtdesk.controller;

import com.example.skrtdesk.service.*;
import com.example.skrtdesk.util.SessionManager;
import com.example.skrtdesk.view.ViewManager;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public abstract class BaseController {
    protected ViewManager viewManager;
    protected SessionManager sessionManager;
    protected AuthService authService;
    protected UserService userService;
    protected PostService postService;
    protected LikeService likeService;
    protected CommentService commentService;
    protected AdminService adminService;
    
    public BaseController() {
        this.viewManager = ViewManager.getInstance();
        this.sessionManager = SessionManager.getInstance();
        this.authService = AuthService.getInstance();
        this.userService = UserService.getInstance();
        this.postService = PostService.getInstance();
        this.likeService = LikeService.getInstance();
        this.commentService = CommentService.getInstance();
        this.adminService = AdminService.getInstance();
    }
    
    protected void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    protected void showSuccess(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    protected void showWarning(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    protected boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    protected void runAsync(Runnable task) {
        new Thread(task).start();
    }
}

