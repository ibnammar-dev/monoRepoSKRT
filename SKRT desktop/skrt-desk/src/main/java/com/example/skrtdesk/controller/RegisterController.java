package com.example.skrtdesk.controller;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.ApiResponse;
import com.example.skrtdesk.model.AuthToken;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Map;

public class RegisterController extends BaseController {
    
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button registerButton;
    @FXML private Label errorLabel;
    
    @FXML
    public void initialize() {
        passwordField.setOnAction(event -> handleRegister());
    }
    
    @FXML
    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showErrorLabel("Please fill in all fields");
            return;
        }
        
        if (password.length() < 6) {
            showErrorLabel("Password must be at least 6 characters");
            return;
        }
        
        registerButton.setDisable(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        runAsync(() -> {
            try {
                ApiResponse<Map<String, Object>> response = 
                    authService.register(email, password, firstName, lastName);
                
                if (response.isSuccess()) {
                    // Auto-login after successful registration
                    try {
                        ApiResponse<AuthToken> loginResponse = authService.login(email, password);
                        if (loginResponse.isSuccess() && loginResponse.getData() != null) {
                            AuthToken authToken = loginResponse.getData();
                            sessionManager.setSession(authToken.getToken(), authToken.getUser());
                            
                            javafx.application.Platform.runLater(() -> {
                                showSuccess("Success", "Registration successful!");
                                viewManager.showMainFeed();
                            });
                        } else {
                            javafx.application.Platform.runLater(() -> {
                                showSuccess("Success", "Registration successful! Please login.");
                                viewManager.showLogin();
                            });
                        }
                    } catch (ApiException e) {
                        javafx.application.Platform.runLater(() -> {
                            showSuccess("Success", "Registration successful! Please login.");
                            viewManager.showLogin();
                        });
                    }
                } else {
                    javafx.application.Platform.runLater(() -> {
                        showErrorLabel(response.getErrorMessage());
                        registerButton.setDisable(false);
                    });
                }
            } catch (ApiException e) {
                javafx.application.Platform.runLater(() -> {
                    showErrorLabel(e.getMessage());
                    registerButton.setDisable(false);
                });
            }
        });
    }
    
    @FXML
    private void handleLoginLink() {
        viewManager.showLogin();
    }
    
    private void showErrorLabel(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}

