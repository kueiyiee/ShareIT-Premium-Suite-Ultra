package com.shareit.shareit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class FileTransferApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Load FXML from resources folder
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/file_transfer_ui.fxml"));
            Parent root = loader.load();

            // Get the controller
            FileTransferController controller = loader.getController();

            Scene scene = new Scene(root, 1400, 900);

            // Pass scene reference to controller for theme management
            controller.setScene(scene);

            // Set application icon
            try {
                Image appIcon = new Image(getClass().getResourceAsStream("/images/shareit_icon.png"));
                primaryStage.getIcons().add(appIcon);
                System.out.println("✅ App icon loaded successfully");
            } catch (Exception e) {
                System.out.println("⚠️ Could not load app icon: " + e.getMessage());
                // Create a fallback colored icon
                System.out.println("🔄 Using fallback app title");
            }

            primaryStage.setTitle("🚀 ShareIT Premium Suite - Advanced File Transfer");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);
            primaryStage.show();

            // Set up shutdown hook
            primaryStage.setOnCloseRequest(e -> {
                controller.shutdown();
            });

            System.out.println("🚀 ShareIT Premium Suite started successfully!");

        } catch (Exception e) {
            System.err.println("❌ ERROR starting application: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        System.out.println("⚡ Initializing ShareIT Premium Suite...");
        launch(args);
    }
}