package com.example.skrtdesk.controller;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.ApiResponse;
import com.example.skrtdesk.model.AuthToken;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController extends BaseController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    
    @FXML
    public void initialize() {
        // Add enter key handler for password field
        passwordField.setOnAction(event -> handleLogin());
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showErrorLabel("Please enter both email and password");
            return;
        }
        
        // Disable button during login
        loginButton.setDisable(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        runAsync(() -> {
            try {
                ApiResponse<AuthToken> response = authService.login(email, password);
                
                if (response.isSuccess() && response.getData() != null) {
                    AuthToken authToken = response.getData();
                    sessionManager.setSession(authToken.getToken(), authToken.getUser());
                    
                    javafx.application.Platform.runLater(() -> {
                        viewManager.showMainFeed();
                    });
                } else {
                    javafx.application.Platform.runLater(() -> {
                        showErrorLabel("Invalid credentials");
                        loginButton.setDisable(false);
                    });
                }
            } catch (ApiException e) {
                javafx.application.Platform.runLater(() -> {
                    showErrorLabel(e.getMessage());
                    loginButton.setDisable(false);
                });
            }
        });
    }
    
    @FXML
    private void handleRegisterLink() {
        viewManager.showRegister();
    }
    
    private void showErrorLabel(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}

