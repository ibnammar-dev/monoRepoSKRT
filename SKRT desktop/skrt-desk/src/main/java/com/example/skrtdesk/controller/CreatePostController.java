package com.example.skrtdesk.controller;

import com.example.skrtdesk.exception.ApiException;
import com.example.skrtdesk.model.Post;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class CreatePostController extends BaseController {
    
    @FXML private TextArea textArea;
    @FXML private Label imageLabel;
    @FXML private Label errorLabel;
    @FXML private Button postButton;
    
    private File selectedImage;
    
    @FXML
    public void initialize() {
    }
    
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        Stage stage = (Stage) textArea.getScene().getWindow();
        selectedImage = fileChooser.showOpenDialog(stage);
        
        if (selectedImage != null) {
            imageLabel.setText(selectedImage.getName());
        }
    }
    
    @FXML
    private void handlePost() {
        String text = textArea.getText().trim();
        
        if (text.isEmpty()) {
            showErrorLabel("Please enter some text for your post");
            return;
        }
        
        postButton.setDisable(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        runAsync(() -> {
            try {
                Post post = postService.createPost(text, selectedImage);
                
                if (post != null) {
                    Platform.runLater(() -> {
                        showSuccess("Success", "Post created successfully!");
                        closeDialog();
                    });
                } else {
                    Platform.runLater(() -> {
                        showErrorLabel("Failed to create post");
                        postButton.setDisable(false);
                    });
                }
            } catch (ApiException e) {
                Platform.runLater(() -> {
                    showErrorLabel(e.getMessage());
                    postButton.setDisable(false);
                });
            }
        });
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private void closeDialog() {
        Stage stage = (Stage) textArea.getScene().getWindow();
        stage.close();
    }
    
    private void showErrorLabel(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}

