package com.example.skrtdesk.controller;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class EditProfileController extends BaseController {
    
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    
    @FXML
    public void initialize() {
        loadUserData();
    }
    
    private void loadUserData() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            firstNameField.setText(currentUser.getFirstName());
            lastNameField.setText(currentUser.getLastName());
            emailField.setText(currentUser.getEmail());
        }
    }
    
    @FXML
    private void handleSave() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showErrorLabel("Please fill in all fields");
            return;
        }
        
        saveButton.setDisable(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        runAsync(() -> {
            try {
                User updatedUser = userService.updateUser(
                    sessionManager.getCurrentUser().getId(),
                    email, firstName, lastName
                );
                
                if (updatedUser != null) {
                    // Update session with new user data
                    sessionManager.setCurrentUser(updatedUser);
                    
                    Platform.runLater(() -> {
                        showSuccess("Success", "Profile updated successfully!");
                        viewManager.showProfile(updatedUser);
                    });
                } else {
                    Platform.runLater(() -> {
                        showErrorLabel("Failed to update profile");
                        saveButton.setDisable(false);
                    });
                }
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    showErrorLabel(e.getMessage());
                    saveButton.setDisable(false);
                });
            }
        });
    }
    
    @FXML
    private void handleCancel() {
        viewManager.showProfile(sessionManager.getCurrentUser());
    }
    
    @FXML
    private void handleBack() {
        viewManager.showProfile(sessionManager.getCurrentUser());
    }
    
    private void showErrorLabel(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}

