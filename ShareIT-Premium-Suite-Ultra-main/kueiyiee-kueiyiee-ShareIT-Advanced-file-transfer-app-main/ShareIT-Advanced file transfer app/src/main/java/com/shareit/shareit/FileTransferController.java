package com.shareit.shareit;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.HashMap;
import java.util.Map;

public class FileTransferController {

    // === FXML INJECTIONS ===
    @FXML private RadioButton senderMode;
    @FXML private RadioButton receiverMode;
    @FXML private TextField ipField;
    @FXML private HBox ipBox;
    @FXML private Button selectFilesBtn;
    @FXML private Button selectFolderBtn;
    @FXML private Button clearFilesBtn;
    @FXML private Button actionBtn;
    @FXML private Button pauseBtn;
    @FXML private Button cancelBtn;
    @FXML private Label fileCountLabel;
    @FXML private TableView<FileTransfer> filesTableView;
    @FXML private TableColumn<FileTransfer, String> fileNameColumn;
    @FXML private TableColumn<FileTransfer, String> fileSizeColumn;
    @FXML private TableColumn<FileTransfer, String> fileStatusColumn;
    @FXML private TableColumn<FileTransfer, Double> fileProgressColumn;
    @FXML private TableColumn<FileTransfer, String> fileSpeedColumn;
    @FXML private ProgressBar overallProgressBar;
    @FXML private Label overallProgressLabel;
    @FXML private Label transferSpeedLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea logArea;
    @FXML private Label timeElapsedLabel;
    @FXML private Label dataTransferredLabel;
    @FXML private Label activeTransfersLabel;
    @FXML private Label completedFilesLabel;
    @FXML private Label remainingTimeLabel;
    @FXML private Button menuButton;
    @FXML private GridPane mainGridPane;
    @FXML private VBox fileTransferPanel;
    @FXML private VBox analyticsPanel;
    @FXML private VBox logPanel;
    @FXML private VBox headerSection;
    @FXML private VBox operationModeSection;
    @FXML private VBox fileManagementSection;
    @FXML private HBox footerSection;
    @FXML private Label operationModeLabel;
    @FXML private RadioButton senderModeRadio;
    @FXML private Label senderFeature1;
    @FXML private Label senderFeature2;
    @FXML private Label receiverFeature1;
    @FXML private Label receiverFeature2;
    @FXML private Label ipLabel;
    @FXML private Button getIpButton;
    @FXML private Label copyrightLabel;

    // === APPLICATION VARIABLES ===
    private ObservableList<FileTransfer> fileTransfers = FXCollections.observableArrayList();
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private List<Task<Void>> activeTasks = new ArrayList<>();
    private boolean isTransferring = false;
    private AtomicBoolean isPaused = new AtomicBoolean(false);
    private AtomicInteger totalFilesCompleted = new AtomicInteger(0);
    private AtomicLong totalBytesTransferred = new AtomicLong(0);
    private AtomicLong totalTransferStartTime = new AtomicLong(0);
    private AtomicInteger activeTransferCount = new AtomicInteger(0);
    private AtomicLong lastSpeedUpdateTime = new AtomicLong(0);
    private AtomicLong lastSpeedBytes = new AtomicLong(0);
    private AtomicLong totalFilesSize = new AtomicLong(0);
    private Timeline progressTimer;
    private FileTransferServer fileTransferServer;
    private FileTransferClient fileTransferClient;
    private int serverPort = 12345;
    private String currentTheme = "dark";
    private double currentFontScale = 1.0;
    private Scene mainScene;
    private Parent root;

    // === ULTRA HIGH SPEED TRANSFER VARIABLES ===
    private double currentSpeedMBps = 0.0;
    private double averageSpeedMBps = 0.0;
    private long speedCalculationInterval = 500;
    private int MAX_CONCURRENT_TRANSFERS = 10;
    private int BUFFER_SIZE = 8192 * 8;

    // === NETWORK CONFIGURATION ===
    private boolean isCrossNetworkEnabled = true;
    private String publicIP = "";
    private String localIP = "";

    // === INITIALIZATION ===
    @FXML
    public void initialize() {
        setupModeToggle();
        setupFilesTable();
        updateUIForMode();
        updateFileCount();
        initializeProgressDisplay();
        logSystemStart();
        updateFooterCopyright();
        setupThreePanelLayout();
        updateButtonStates();
        detectNetworkConfiguration();

        Platform.runLater(() -> {
            Scene scene = menuButton.getScene();
            if (scene != null) {
                setScene(scene);
            }
        });
    }

    // === NETWORK DETECTION ===
    private void detectNetworkConfiguration() {
        try {
            localIP = InetAddress.getLocalHost().getHostAddress();
            log("🌐 NETWORK: Local IP: " + localIP);
            log("🚀 SYSTEM: Ultra-high speed transfer engine ready");

            new Thread(() -> {
                try {
                    URL whatismyip = new URL("http://checkip.amazonaws.com");
                    BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
                    publicIP = in.readLine();
                    in.close();
                    log("🌐 NETWORK: Public IP detected: " + publicIP);
                    log("🌐 NETWORK: Cross-network transfers ENABLED");
                } catch (Exception e) {
                    log("⚠️ NETWORK: Public IP detection failed - Local network only");
                    isCrossNetworkEnabled = false;
                }
            }).start();

        } catch (Exception e) {
            log("⚠️ NETWORK: Could not detect network configuration");
        }
    }

    private void setupThreePanelLayout() {
        Platform.runLater(() -> {
            if (mainGridPane != null) {
                VBox.setVgrow(filesTableView, Priority.ALWAYS);
                VBox.setVgrow(logArea, Priority.ALWAYS);

                if (filesTableView != null) {
                    filesTableView.setPrefHeight(300);
                }

                if (logArea != null) {
                    logArea.setPrefHeight(300);
                }
            }
        });
    }

    public void setScene(Scene scene) {
        this.mainScene = scene;
        this.root = scene.getRoot();
        applyTheme(currentTheme);
        applyFontSize("medium");
    }

    // === UPDATED: Get My IP Address ===
    @FXML
    private void showMyIP() {
        try {
            String localIP = InetAddress.getLocalHost().getHostAddress();
            ipField.setText(localIP);
            statusLabel.setText("✅ IP Address set: " + localIP);
            log("🌐 NETWORK: Local IP address retrieved: " + localIP);

            ipField.setStyle("-fx-border-color: #00e676; -fx-border-width: 2;");
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(1), e ->
                            ipField.setStyle("-fx-border-color: #666; -fx-border-width: 1;")
                    )
            );
            timeline.play();
            updateButtonStates();

        } catch (Exception e) {
            log("❌ ERROR: Could not determine local IP - " + e.getMessage());
            statusLabel.setText("❌ Could not retrieve IP address");
        }
    }

    // === DEVELOPER INFO METHOD ===
    @FXML
    private void showDeveloperInfo() {
        Platform.runLater(() -> {
            Alert alert = createStyledAlert("👨‍💻 Developer Information", "ShareIT Premium Suite v3.0");
            VBox content = new VBox(15);
            content.setAlignment(Pos.CENTER_LEFT);
            content.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 20;");

            TextFlow infoFlow = new TextFlow();
            infoFlow.getChildren().addAll(
                    createStyledText("👤 Developer: Kuei Poch Kuei\n\n", "#00e676", 14, true),
                    createStyledText("🎓 Education:\n", "#ffffff", 13, true),
                    createStyledText("   • Dilla University\n   • College of Engineering & Technology\n   • 3rd Year Computer Science\n\n", "#bdbdbd", 13, false),
                    createStyledText("💻 Skills:\n", "#ffffff", 13, true),
                    createStyledText("   • Java & JavaFX Development\n   • Network Programming\n   • Multi-threaded Applications\n   • UI/UX Design\n\n", "#bdbdbd", 13, false),
                    createStyledText("📞 Contact:\n", "#ffffff", 13, true),
                    createStyledText("   • Email: kueiyiee@gmail.com\n   • Phone: +251 937 910 246\n   • GitHub: github.com/kueiyiee\n", "#bdbdbd", 13, false),
                    createStyledText("\n💡 Motto: \"My Logic is your Limit\"", "#ff6d00", 13, true)
            );

            content.getChildren().add(infoFlow);
            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setPrefSize(450, 400);
            alert.showAndWait();
        });

        log("👨‍💻 DEVELOPER: Developer information displayed");
    }

    private Text createStyledText(String content, String color, double size, boolean bold) {
        Text text = new Text(content);
        String style = String.format("-fx-fill: %s; -fx-font-size: %.0f;", color, size);
        if (bold) {
            style += " -fx-font-weight: bold;";
        }
        text.setStyle(style);
        return text;
    }

    private Alert createStyledAlert(String title, String header) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.getDialogPane().setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: white;");
        return alert;
    }

    private void updateButtonStates() {
        boolean isSender = senderMode.isSelected();
        boolean hasIP = !ipField.getText().trim().isEmpty();
        boolean hasFiles = !fileTransfers.isEmpty();

        if (isSender) {
            actionBtn.setDisable(!hasIP || !hasFiles);
        } else {
            actionBtn.setDisable(false);
        }

        pauseBtn.setDisable(!isTransferring);
        cancelBtn.setDisable(!isTransferring);
    }

    // === MODE TOGGLE ===
    private void setupModeToggle() {
        ToggleGroup modeGroup = new ToggleGroup();
        senderMode.setToggleGroup(modeGroup);
        receiverMode.setToggleGroup(modeGroup);
        senderMode.setSelected(true);

        modeGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            updateUIForMode();
            updateButtonStates();
        });

        ipField.textProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
    }

    private void updateUIForMode() {
        boolean isSender = senderMode.isSelected();

        if (isSender) {
            ipLabel.setText("🎯 RECEIVER IP ADDRESS:");
            ipField.setPromptText("Enter receiver's IP address");
            getIpButton.setVisible(false);
            getIpButton.setManaged(false);
            actionBtn.setText("🚀 LAUNCH TRANSFER");
            statusLabel.setText("🟢 READY TO SEND FILES");
            log("🚀 MODE: Sender mode activated");
        } else {
            ipLabel.setText("🎯 YOUR IP ADDRESS:");
            ipField.setPromptText("Click 'Get My IP' to display");
            getIpButton.setVisible(true);
            getIpButton.setManaged(true);
            actionBtn.setText("📥 START LISTENING");
            statusLabel.setText("🔵 READY TO RECEIVE FILES");
            log("📥 MODE: Receiver mode activated");
        }

        if (!isSender) {
            startReceiverServer();
        } else {
            stopReceiverServer();
        }

        updateButtonStates();
    }

    // === FILE SELECTION METHODS ===
    @FXML
    private void handleSelectFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files for Transfer");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx", "*.txt", "*.xlsx"),
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.gif", "*.bmp", "*.webp"),
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.avi", "*.mov", "*.mkv", "*.webm"),
                new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.wav", "*.flac", "*.aac"),
                new FileChooser.ExtensionFilter("Archives", "*.zip", "*.rar", "*.7z", "*.tar")
        );

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(selectFilesBtn.getScene().getWindow());

        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            int newFiles = 0;
            for (File file : selectedFiles) {
                if (!isFileAlreadyAdded(file)) {
                    FileTransfer fileTransfer = new FileTransfer(file);
                    fileTransfer.setStatus("Queued");
                    fileTransfers.add(fileTransfer);
                    newFiles++;
                }
            }
            updateFileCount();
            log("📁 FILES: " + newFiles + " files added to transfer queue");
            updateOverallProgressDisplay();
            updateButtonStates();
            statusLabel.setText("🟢 READY TO LAUNCH TRANSFER");
        }
    }

    @FXML
    private void handleSelectFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder for Transfer");
        File selectedFolder = directoryChooser.showDialog(selectFolderBtn.getScene().getWindow());

        if (selectedFolder != null) {
            int newFiles = addFolderFiles(selectedFolder);
            updateFileCount();
            log("📁 FILES: Added " + newFiles + " files from folder: " + selectedFolder.getName());
            updateOverallProgressDisplay();
            updateButtonStates();
            statusLabel.setText("🟢 READY TO LAUNCH TRANSFER");
        }
    }

    @FXML
    private void handleClearFiles() {
        if (!fileTransfers.isEmpty()) {
            int fileCount = fileTransfers.size();
            fileTransfers.clear();
            updateFileCount();
            log("🗑️ FILES: Transfer queue cleared - " + fileCount + " files removed");
            resetProgressTracking();
            updateButtonStates();
        }
    }

    // === TRANSFER CONTROL METHODS ===
    @FXML
    private void handleAction() {
        if (senderMode.isSelected()) {
            launchUltraSpeedTransfer();
        } else {
            startUltraSpeedReceiver();
            actionBtn.setDisable(true);
            statusLabel.setText("🔵 LISTENING FOR INCOMING FILES...");
            log("🔵 RECEIVER: Ultra-speed server activated on port " + serverPort);
        }
    }

    @FXML
    private void handlePauseResume() {
        if (isPaused.get()) {
            isPaused.set(false);
            pauseBtn.setText("⏸️ PAUSE ALL");
            statusLabel.setText("🟢 TRANSFER RESUMED");
            log("⏯️ TRANSFER: All transfers resumed");
        } else {
            isPaused.set(true);
            pauseBtn.setText("▶️ RESUME ALL");
            statusLabel.setText("🟡 TRANSFER PAUSED");
            log("⏸️ TRANSFER: All transfers paused");
        }
    }

    @FXML
    private void handleCancelTransfers() {
        abortMission();
    }

    // === LOG MANAGEMENT ===
    @FXML
    private void clearLog() {
        logArea.clear();
        log("📋 LOG: Log cleared by user");
    }

    @FXML
    private void saveLog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Log File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        String gregorianDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String ethiopianDate = getEthiopianDateFormatted();
        String safeEthiopianDate = ethiopianDate.replace("/", "").replace(" ", "_");

        fileChooser.setInitialFileName("shareit_log_" + gregorianDate + "_" + safeEthiopianDate + ".txt");

        File file = fileChooser.showSaveDialog(logArea.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.write(logArea.getText());
                log("💾 LOG: Log saved to: " + file.getAbsolutePath());
                showSuccessAlert("Log Saved", "Log file successfully saved to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                log("❌ ERROR: Failed to save log - " + e.getMessage());
                showAlert("Save Error", "Failed to save log file: " + e.getMessage());
            }
        }
    }

    // === DATE/TIME METHODS ===
    private String getEthiopianDate() {
        try {
            LocalDate gregorianDate = LocalDate.now();
            int gregorianYear = gregorianDate.getYear();
            int ethiopianYear = gregorianYear - 7;

            if (gregorianDate.getMonthValue() < 9) {
                ethiopianYear = gregorianYear - 8;
            }

            int dayOfYear = gregorianDate.getDayOfYear();
            int ethiopianMonth, ethiopianDay;

            if (dayOfYear >= 265) {
                int daysSinceNewYear = dayOfYear - 264;
                ethiopianMonth = (daysSinceNewYear / 30) + 1;
                ethiopianDay = (daysSinceNewYear % 30);
                if (ethiopianDay == 0) {
                    ethiopianDay = 30;
                    ethiopianMonth--;
                }
            } else {
                int daysFromPreviousYear = dayOfYear + 100;
                ethiopianMonth = (daysFromPreviousYear / 30) + 1;
                ethiopianDay = (daysFromPreviousYear % 30);
                if (ethiopianDay == 0) {
                    ethiopianDay = 30;
                    ethiopianMonth--;
                }
            }

            if (ethiopianMonth == 13) {
                boolean isGregorianLeapYear = gregorianDate.isLeapYear();
                int pagumeDays = isGregorianLeapYear ? 6 : 5;
                if (ethiopianDay > pagumeDays) {
                    ethiopianDay = 1;
                    ethiopianMonth = 1;
                    ethiopianYear++;
                }
            }

            return String.format("%d-%02d-%02d", ethiopianYear, ethiopianMonth, ethiopianDay);

        } catch (Exception e) {
            int currentYear = LocalDate.now().getYear();
            return String.format("%d-01-01", currentYear - 7);
        }
    }

    private String getEthiopianDateFormatted() {
        String ethiopianDate = getEthiopianDate();
        String[] parts = ethiopianDate.split("-");
        if (parts.length == 3) {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            String[] ethiopianMonths = {
                    "Meskerem", "Tikimt", "Hidar", "Tahsas", "Tir", "Yekatit",
                    "Megabit", "Miazia", "Genbot", "Sene", "Hamle", "Nehase", "Pagume"
            };

            String monthName = (month <= 13 && month >= 1) ? ethiopianMonths[month - 1] : "Unknown";
            return String.format("%d %s %d", day, monthName, year);
        }
        return ethiopianDate;
    }

    private String getCurrentDateInfo() {
        try {
            ZoneId systemZone = ZoneId.systemDefault();
            ZonedDateTime now = ZonedDateTime.now(systemZone);

            DateTimeFormatter gregorianFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
            String gregorianDateTime = now.format(gregorianFormatter);

            String ethiopianDate = getEthiopianDateFormatted();

            return String.format("Gregorian: %s\nEthiopian: %s", gregorianDateTime, ethiopianDate);
        } catch (Exception e) {
            return "Date information unavailable";
        }
    }

    // === COPYRIGHT ===
    private String getCurrentCopyrightYear() {
        try {
            int gregorianYear = LocalDate.now().getYear();
            int ethiopianYear = gregorianYear - 7;

            int currentMonth = LocalDate.now().getMonthValue();
            if (currentMonth < 9) {
                ethiopianYear = gregorianYear - 8;
            }

            return String.format("%d GC / %d EC", gregorianYear, ethiopianYear);
        } catch (Exception e) {
            int currentYear = LocalDate.now().getYear();
            return String.format("%d GC / %d EC", currentYear, currentYear - 7);
        }
    }

    private void updateFooterCopyright() {
        if (copyrightLabel != null) {
            String copyrightText = "© " + getCurrentCopyrightYear() + " | ShareIT Premium Suite v3.0 Ultra";
            copyrightLabel.setText(copyrightText);
            copyrightLabel.setStyle("-fx-text-fill: #00e676; -fx-font-size: 12; -fx-font-weight: bold;");
        }
    }

    // === CONTEXT MENU ===
    @FXML
    private void showContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setStyle("-fx-background-color: #2a2a2a; -fx-border-color: #00c853; -fx-border-width: 2;");

        // Transfer Actions
        MenuItem newTransferItem = createStyledMenuItem("🔄 New Transfer Session", 13);
        MenuItem clearFilesItem = createStyledMenuItem("🗑️ Clear File Queue", 13);
        MenuItem saveLogItem = createStyledMenuItem("💾 Save System Log", 13);

        newTransferItem.setOnAction(e -> handleNewTransfer());
        clearFilesItem.setOnAction(e -> handleClearFiles());
        saveLogItem.setOnAction(e -> saveLog());

        // Developer Section
        Menu developerMenu = createStyledMenu("👨‍💻 Developer Profile", 14);
        MenuItem profileItem = createStyledMenuItem("📄 Developer Information", 13);
        MenuItem githubItem = createStyledMenuItem("💻 GitHub Portfolio", 13);
        MenuItem linkedinItem = createStyledMenuItem("💼 LinkedIn Profile", 13);
        MenuItem contactItem = createStyledMenuItem("📞 Contact Developer", 13);

        profileItem.setOnAction(e -> showDeveloperProfileDialog());
        githubItem.setOnAction(e -> openGitHub());
        linkedinItem.setOnAction(e -> openLinkedIn());
        contactItem.setOnAction(e -> contactDeveloper());

        developerMenu.getItems().addAll(profileItem, githubItem, linkedinItem, contactItem);

        // Help & Guides
        Menu helpMenu = createStyledMenu("📚 Help & Guides", 14);
        MenuItem userManualItem = createStyledMenuItem("📖 Complete User Manual", 13);
        MenuItem quickGuideItem = createStyledMenuItem("🚀 Quick Start Guide", 13);
        MenuItem aboutItem = createStyledMenuItem("ℹ️ About Application", 13);
        MenuItem dateInfoItem = createStyledMenuItem("📅 Current Date Info", 13);

        userManualItem.setOnAction(e -> showCompleteUserManual());
        quickGuideItem.setOnAction(e -> showUserGuideDialog());
        aboutItem.setOnAction(e -> showAboutDialog());
        dateInfoItem.setOnAction(e -> showDateInfoDialog());

        helpMenu.getItems().addAll(userManualItem, quickGuideItem, aboutItem, dateInfoItem);

        // Appearance
        Menu appearanceMenu = createStyledMenu("🎨 Appearance Settings", 14);
        Menu themeSubMenu = createStyledMenu("🌈 Interface Themes", 13);
        MenuItem darkThemeItem = createStyledMenuItem("🌙 Dark Theme (Default)", 12);
        MenuItem lightThemeItem = createStyledMenuItem("☀️ Light Theme", 12);
        MenuItem blueThemeItem = createStyledMenuItem("🔵 Blue Theme", 12);
        MenuItem purpleThemeItem = createStyledMenuItem("🟣 Purple Theme", 12);

        darkThemeItem.setOnAction(e -> setDarkTheme());
        lightThemeItem.setOnAction(e -> setLightTheme());
        blueThemeItem.setOnAction(e -> setBlueTheme());
        purpleThemeItem.setOnAction(e -> setPurpleTheme());

        themeSubMenu.getItems().addAll(darkThemeItem, lightThemeItem, blueThemeItem, purpleThemeItem);

        // Font Size
        Menu fontSubMenu = createStyledMenu("🔤 Text Size Settings", 13);
        MenuItem smallFontItem = createStyledMenuItem("Small Text Size", 12);
        MenuItem mediumFontItem = createStyledMenuItem("Medium Text Size", 12);
        MenuItem largeFontItem = createStyledMenuItem("Large Text Size", 12);
        MenuItem xlargeFontItem = createStyledMenuItem("Extra Large Text Size", 12);

        smallFontItem.setOnAction(e -> setSmallFont());
        mediumFontItem.setOnAction(e -> setMediumFont());
        largeFontItem.setOnAction(e -> setLargeFont());
        xlargeFontItem.setOnAction(e -> setXLargeFont());

        fontSubMenu.getItems().addAll(smallFontItem, mediumFontItem, largeFontItem, xlargeFontItem);

        // Network Configuration
        Menu networkMenu = createStyledMenu("🌐 Network Settings", 14);
        MenuItem optimizeLocalItem = createStyledMenuItem("⚡ Optimize for Local Network", 12);
        MenuItem optimizeCrossItem = createStyledMenuItem("🌍 Optimize for Cross-Network", 12);
        MenuItem testSpeedItem = createStyledMenuItem("📊 Test Network Speed", 12);
        MenuItem portConfigItem = createStyledMenuItem("🔧 Configure Port", 12);

        optimizeLocalItem.setOnAction(e -> optimizeForLocalNetwork());
        optimizeCrossItem.setOnAction(e -> optimizeForCrossNetwork());
        testSpeedItem.setOnAction(e -> testNetworkSpeed());
        portConfigItem.setOnAction(e -> configurePort());

        networkMenu.getItems().addAll(optimizeLocalItem, optimizeCrossItem, testSpeedItem, portConfigItem);

        appearanceMenu.getItems().addAll(themeSubMenu, fontSubMenu, networkMenu);

        // System Actions
        MenuItem exitItem = createStyledMenuItem("🚪 Exit Application", 13);
        exitItem.setOnAction(e -> handleExit());

        // Build Menu
        contextMenu.getItems().addAll(
                newTransferItem,
                clearFilesItem,
                new SeparatorMenuItem(),
                developerMenu,
                helpMenu,
                appearanceMenu,
                new SeparatorMenuItem(),
                saveLogItem,
                exitItem
        );

        applyFontScalingToContextMenu(contextMenu, currentFontScale);
        contextMenu.show(menuButton, javafx.geometry.Side.BOTTOM, 0, 5);
    }

    // === NETWORK OPTIMIZATION ===
    private void optimizeForLocalNetwork() {
        BUFFER_SIZE = 8192 * 16;
        MAX_CONCURRENT_TRANSFERS = 15;
        log("⚡ NETWORK: Optimized for local network (128KB buffer, 15 concurrent transfers)");
        showSuccessAlert("Network Optimized", "Optimized for local network transfers");
    }

    private void optimizeForCrossNetwork() {
        BUFFER_SIZE = 8192 * 4;
        MAX_CONCURRENT_TRANSFERS = 5;
        log("🌍 NETWORK: Optimized for cross-network (32KB buffer, 5 concurrent transfers)");
        showSuccessAlert("Network Optimized", "Optimized for cross-network transfers");
    }

    private void testNetworkSpeed() {
        log("📊 NETWORK: Starting network speed test...");
        showAlert("Speed Test", "Network speed test feature coming soon!");
    }

    private void configurePort() {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(serverPort));
        dialog.setTitle("Configure Port");
        dialog.setHeaderText("Change Server Port");
        dialog.setContentText("Enter new port number (1024-65535):");

        dialog.showAndWait().ifPresent(portStr -> {
            try {
                int newPort = Integer.parseInt(portStr);
                if (newPort >= 1024 && newPort <= 65535) {
                    serverPort = newPort;
                    log("🔧 NETWORK: Port changed to: " + serverPort);
                    showSuccessAlert("Port Changed", "Server port updated to: " + serverPort);
                } else {
                    showAlert("Invalid Port", "Port must be between 1024 and 65535");
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Please enter a valid port number");
            }
        });
    }

    private MenuItem createStyledMenuItem(String text, double fontSize) {
        MenuItem item = new MenuItem(text);
        double newSize = fontSize * currentFontScale;
        item.setStyle(String.format(
                "-fx-text-fill: #ffffff; -fx-font-size: %.1f; " +
                        "-fx-background-color: #2a2a2a; -fx-padding: 8 12 8 12; " +
                        "-fx-border-color: transparent;",
                newSize
        ));

        item.addEventHandler(MouseEvent.MOUSE_ENTERED, event ->
                item.setStyle(String.format(
                        "-fx-text-fill: #00e676; -fx-font-size: %.1f; " +
                                "-fx-background-color: #1a1a1a; -fx-padding: 8 12 8 12; " +
                                "-fx-border-color: #00c853; -fx-border-width: 1;",
                        newSize
                ))
        );

        item.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
                item.setStyle(String.format(
                        "-fx-text-fill: #ffffff; -fx-font-size: %.1f; " +
                                "-fx-background-color: #2a2a2a; -fx-padding: 8 12 8 12; " +
                                "-fx-border-color: transparent;",
                        newSize
                ))
        );

        return item;
    }

    private Menu createStyledMenu(String text, double fontSize) {
        Menu menu = new Menu(text);
        double newSize = fontSize * currentFontScale;
        menu.setStyle(String.format(
                "-fx-text-fill: #00e676; -fx-font-size: %.1f; -fx-font-weight: bold; " +
                        "-fx-background-color: #2a2a2a; -fx-padding: 8 12 8 12;",
                newSize
        ));
        return menu;
    }

    // === DATE INFO DIALOG ===
    private void showDateInfoDialog() {
        Platform.runLater(() -> {
            Alert alert = createStyledAlert("📅 Date & Time Information", "Current Date in Both Calendars");
            VBox content = new VBox(10);
            content.setAlignment(Pos.CENTER_LEFT);
            content.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 20;");

            TextFlow dateFlow = new TextFlow();
            String dateInfo = getCurrentDateInfo();
            String[] lines = dateInfo.split("\n");

            dateFlow.getChildren().addAll(
                    createStyledText("🌍 Gregorian Calendar:\n", "#00e676", 14, true),
                    createStyledText(lines[0].replace("Gregorian: ", "") + "\n\n", "#ffffff", 13, false),
                    createStyledText("🇪🇹 Ethiopian Calendar:\n", "#00e676", 14, true),
                    createStyledText((lines.length > 1 ? lines[1].replace("Ethiopian: ", "") : getEthiopianDateFormatted()) + "\n\n", "#ffffff", 13, false),
                    createStyledText("⏰ System Time Zone:\n", "#00e676", 14, true),
                    createStyledText(ZoneId.systemDefault().getId() + "\n\n", "#bdbdbd", 13, false),
                    createStyledText("Note: Dates are automatically calculated based on your system's time zone.", "#ff6d00", 12, false)
            );

            content.getChildren().add(dateFlow);
            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setPrefSize(500, 350);
            alert.showAndWait();
        });

        log("📅 SYSTEM: Date information displayed");
    }

    // === MENU HANDLERS ===
    @FXML
    private void showDeveloperProfile() {
        showDeveloperProfileDialog();
    }

    @FXML
    private void showUserGuide() {
        showUserGuideDialog();
    }

    @FXML
    private void showAbout() {
        showAboutDialog();
    }

    @FXML
    private void handleNewTransfer() {
        handleClearFiles();
        log("🔄 SYSTEM: New transfer session started");
    }

    @FXML
    private void handleExit() {
        shutdown();
        Platform.exit();
    }

    // === FILE TABLE SETUP ===
    private void setupFilesTable() {
        filesTableView.setItems(fileTransfers);
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSizeFormatted"));
        fileStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        fileProgressColumn.setCellValueFactory(new PropertyValueFactory<>("progress"));
        fileSpeedColumn.setCellValueFactory(new PropertyValueFactory<>("transferSpeed"));

        fileProgressColumn.setCellFactory(column -> new TableCell<FileTransfer, Double>() {
            private final ProgressBar progressBar = new ProgressBar();
            private final Label percentageLabel = new Label();
            private final HBox progressContainer = new HBox(5, progressBar, percentageLabel);

            {
                progressBar.setMaxWidth(Double.MAX_VALUE);
                progressBar.setPrefWidth(150);
                progressBar.setStyle("-fx-accent: #00e676; -fx-background-color: #0a0a0a;");
                percentageLabel.setStyle("-fx-text-fill: #00e676; -fx-font-size: 10; -fx-font-weight: bold;");
                progressContainer.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(progressBar, Priority.ALWAYS);
            }

            @Override
            protected void updateItem(Double progress, boolean empty) {
                super.updateItem(progress, empty);
                if (empty || progress == null) {
                    setGraphic(null);
                } else {
                    progressBar.setProgress(progress);
                    percentageLabel.setText(String.format("%.0f%%", progress * 100));
                    setGraphic(progressContainer);
                }
            }
        });
    }

    // === PROGRESS AND ANALYTICS ===
    private void initializeProgressDisplay() {
        timeElapsedLabel.setText("00:00:00");
        dataTransferredLabel.setText("0 B");
        activeTransfersLabel.setText("0");
        completedFilesLabel.setText("0");
        remainingTimeLabel.setText("--:--");
        transferSpeedLabel.setText("0.00 KB/s");
        overallProgressLabel.setText("0/0 FILES COMPLETED");
    }

    private void startProgressTimer() {
        if (progressTimer != null) {
            progressTimer.stop();
        }

        progressTimer = new Timeline(
                new KeyFrame(Duration.millis(250), e -> updateProgressDisplay())
        );
        progressTimer.setCycleCount(Timeline.INDEFINITE);
        progressTimer.play();

        totalTransferStartTime.set(System.currentTimeMillis());
        lastSpeedUpdateTime.set(System.currentTimeMillis());
        lastSpeedBytes.set(0);
    }

    private void updateProgressDisplay() {
        Platform.runLater(() -> {
            long elapsedMillis = System.currentTimeMillis() - totalTransferStartTime.get();

            // Update time with proper formatting
            timeElapsedLabel.setText(formatElapsedTime(elapsedMillis));

            // Update data transferred with proper formatting
            dataTransferredLabel.setText(formatDataSize(totalBytesTransferred.get()));

            // Update other labels
            activeTransfersLabel.setText(String.valueOf(activeTransferCount.get()));
            completedFilesLabel.setText(String.valueOf(totalFilesCompleted.get()));

            // Update speed calculations
            updateCurrentSpeed();
            updateRemainingTime();
            updateFileCount();
        });
    }

    // === FIXED: IMPROVED ELAPSED TIME FORMATTING ===
    private String formatElapsedTime(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02d:%02d", minutes, seconds);
        } else {
            return String.format("00:%02d", seconds);
        }
    }

    // === FIXED: IMPROVED DATA SIZE FORMATTING ===
    private String formatDataSize(long bytes) {
        if (bytes < 1024) {
            return String.format("%d B", bytes);
        } else if (bytes < 1024 * 1024) {
            double kb = bytes / 1024.0;
            if (kb < 10) return String.format("%.2f KB", kb);
            else if (kb < 100) return String.format("%.1f KB", kb);
            else return String.format("%.0f KB", kb);
        } else if (bytes < 1024 * 1024 * 1024) {
            double mb = bytes / (1024.0 * 1024.0);
            if (mb < 10) return String.format("%.2f MB", mb);
            else if (mb < 100) return String.format("%.1f MB", mb);
            else return String.format("%.0f MB", mb);
        } else {
            double gb = bytes / (1024.0 * 1024.0 * 1024.0);
            if (gb < 10) return String.format("%.2f GB", gb);
            else if (gb < 100) return String.format("%.1f GB", gb);
            else return String.format("%.0f GB", gb);
        }
    }

    // === FIXED: IMPROVED CURRENT SPEED CALCULATION ===
    private void updateCurrentSpeed() {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastSpeedUpdateTime.get();

        if (timeDiff >= speedCalculationInterval) {
            long bytesDiff = totalBytesTransferred.get() - lastSpeedBytes.get();

            if (timeDiff > 0 && bytesDiff > 0) {
                double bytesPerSecond = (bytesDiff * 1000.0) / timeDiff;

                // Store current speed for average calculation
                currentSpeedMBps = bytesPerSecond / (1024 * 1024); // Convert to MB/s

                // Update display with appropriate formatting
                if (bytesPerSecond >= 1024 * 1024 * 1024) { // GB/s range
                    double gbps = bytesPerSecond / (1024 * 1024 * 1024);
                    transferSpeedLabel.setText(String.format("⚡ %.2f GB/s", gbps));
                    transferSpeedLabel.setStyle("-fx-text-fill: #ff6d00; -fx-font-weight: bold;");
                } else if (bytesPerSecond >= 1024 * 1024 * 100) { // High MB/s range
                    if (currentSpeedMBps >= 1000) {
                        transferSpeedLabel.setText(String.format("⚡ %.1f GB/s", currentSpeedMBps / 1024));
                    } else if (currentSpeedMBps >= 100) {
                        transferSpeedLabel.setText(String.format("🚀 %.0f MB/s", currentSpeedMBps));
                    } else {
                        transferSpeedLabel.setText(String.format("⚡ %.1f MB/s", currentSpeedMBps));
                    }
                    transferSpeedLabel.setStyle("-fx-text-fill: #00e676; -fx-font-weight: bold;");
                } else if (bytesPerSecond >= 1024 * 1024) { // MB/s range
                    if (currentSpeedMBps >= 10) {
                        transferSpeedLabel.setText(String.format("🚀 %.1f MB/s", currentSpeedMBps));
                    } else if (currentSpeedMBps >= 1) {
                        transferSpeedLabel.setText(String.format("%.2f MB/s", currentSpeedMBps));
                    } else {
                        double kbps = bytesPerSecond / 1024;
                        transferSpeedLabel.setText(String.format("%.1f KB/s", kbps));
                    }
                    transferSpeedLabel.setStyle("-fx-text-fill: #00e676;");
                } else if (bytesPerSecond >= 1024) { // KB/s range
                    double kbps = bytesPerSecond / 1024;
                    if (kbps >= 100) {
                        transferSpeedLabel.setText(String.format("%.0f KB/s", kbps));
                    } else if (kbps >= 10) {
                        transferSpeedLabel.setText(String.format("%.1f KB/s", kbps));
                    } else {
                        transferSpeedLabel.setText(String.format("%.2f KB/s", kbps));
                    }
                    transferSpeedLabel.setStyle("-fx-text-fill: #bdbdbd;");
                } else { // B/s range
                    transferSpeedLabel.setText(String.format("%.0f B/s", bytesPerSecond));
                    transferSpeedLabel.setStyle("-fx-text-fill: #bdbdbd;");
                }

                // Update average speed - FIXED CALCULATION
                if (totalBytesTransferred.get() > 0 && totalTransferStartTime.get() > 0) {
                    long totalTime = currentTime - totalTransferStartTime.get();
                    if (totalTime > 0) {
                        double totalMB = totalBytesTransferred.get() / (1024.0 * 1024.0);
                        double totalSeconds = totalTime / 1000.0;
                        averageSpeedMBps = totalMB / totalSeconds;
                    }
                }

                lastSpeedUpdateTime.set(currentTime);
                lastSpeedBytes.set(totalBytesTransferred.get());
            } else if (timeDiff >= 2000) {
                transferSpeedLabel.setText("0.00 KB/s");
                transferSpeedLabel.setStyle("-fx-text-fill: #bdbdbd;");
                currentSpeedMBps = 0.0;
            }
        }
    }

    private void updateRemainingTime() {
        if (totalBytesTransferred.get() > 0 && activeTransferCount.get() > 0 && currentSpeedMBps > 0.001) {
            long remainingBytes = totalFilesSize.get() - totalBytesTransferred.get();
            double remainingSeconds = remainingBytes / (currentSpeedMBps * 1024 * 1024);

            if (remainingSeconds > 0) {
                long hours = (long) (remainingSeconds / 3600);
                long minutes = (long) ((remainingSeconds % 3600) / 60);
                long seconds = (long) (remainingSeconds % 60);

                if (hours > 0) {
                    remainingTimeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                } else if (minutes > 0) {
                    remainingTimeLabel.setText(String.format("%02d:%02d", minutes, seconds));
                } else if (remainingSeconds >= 1) {
                    remainingTimeLabel.setText(String.format("00:%02d", seconds));
                } else {
                    remainingTimeLabel.setText("< 1 sec");
                }

                if (currentSpeedMBps > 10) {
                    remainingTimeLabel.setStyle("-fx-text-fill: #00e676; -fx-font-weight: bold;");
                } else if (currentSpeedMBps > 1) {
                    remainingTimeLabel.setStyle("-fx-text-fill: #ff6d00;");
                } else {
                    remainingTimeLabel.setStyle("-fx-text-fill: #bdbdbd;");
                }
            } else {
                remainingTimeLabel.setText("Finishing...");
                remainingTimeLabel.setStyle("-fx-text-fill: #00e676;");
            }
        } else {
            remainingTimeLabel.setText("--:--");
            remainingTimeLabel.setStyle("-fx-text-fill: #bdbdbd;");
        }
    }

    private void stopProgressTimer() {
        if (progressTimer != null) {
            progressTimer.stop();
        }
    }

    private void updateFileCount() {
        int totalFiles = fileTransfers.size();
        long totalSize = fileTransfers.stream().mapToLong(ft -> ft.getFile().length()).sum();
        totalFilesSize.set(totalSize);

        int queuedFiles = (int) fileTransfers.stream().filter(ft -> ft.getStatus().equals("Queued") || ft.getStatus().equals("Pending")).count();
        int activeFiles = (int) fileTransfers.stream().filter(ft -> ft.getStatus().equals("Sending...") || ft.getStatus().equals("Receiving...")).count();
        int completedFiles = (int) fileTransfers.stream().filter(ft -> ft.getStatus().equals("Completed") || ft.getStatus().equals("Received")).count();

        fileCountLabel.setText(String.format("%d files | %d queued | %d active | %d completed | Total: %s",
                totalFiles, queuedFiles, activeFiles, completedFiles, formatDataSize(totalSize)));
    }

    private void updateOverallProgressDisplay() {
        Platform.runLater(() -> {
            int totalFiles = fileTransfers.size();
            int completedFiles = totalFilesCompleted.get();
            double overallProgress = totalFiles > 0 ? (double) completedFiles / totalFiles : 0;

            overallProgressBar.setProgress(overallProgress);
            overallProgressLabel.setText(completedFiles + "/" + totalFiles + " FILES COMPLETED");

            updateFileCount();

            if (completedFiles >= totalFiles && totalFiles > 0) {
                missionComplete();
            }
        });
    }

    // === METHODS NEEDED BY CLIENT AND SERVER ===
    public void markFileAsCompleted(String fileName) {
        Platform.runLater(() -> {
            fileTransfers.stream()
                    .filter(ft -> ft.getFileName().equals(fileName))
                    .findFirst()
                    .ifPresent(ft -> {
                        ft.setStatus("Completed");
                        ft.setProgress(1.0);
                        ft.setTransferSpeed("Completed");
                        totalFilesCompleted.incrementAndGet();
                        updateOverallProgressDisplay();
                        playNotificationSound();
                        log("🎉 COMPLETED: " + fileName + " - Ultra-speed transfer successful!");
                    });
        });
    }

    public void updateFileStatus(String fileName, String status) {
        Platform.runLater(() -> {
            fileTransfers.stream()
                    .filter(ft -> ft.getFileName().equals(fileName))
                    .findFirst()
                    .ifPresent(ft -> {
                        ft.setStatus(status);
                        if (status.contains("Failed") || status.contains("error")) {
                            ft.setProgress(0.0);
                            ft.setTransferSpeed("Failed");
                        }
                    });
        });
    }

    // === FIXED: UPDATED METHOD WITH 5 PARAMETERS ===
    public void updateFileProgress(String fileName, double progress, double additionalValue, long bytesSent, long totalSize) {
        Platform.runLater(() -> {
            long currentTime = System.currentTimeMillis();

            fileTransfers.stream()
                    .filter(ft -> ft.getFileName().equals(fileName))
                    .findFirst()
                    .ifPresent(ft -> {
                        double fileSpeed = calculateFileSpeed(ft, bytesSent, currentTime);
                        ft.setProgress(progress);

                        // Use additionalValue if needed (e.g., for displaying additional metrics)
                        if (additionalValue > 0) {
                            // You can use additionalValue here for extra calculations or display
                        }

                        // Format speed display
                        if (fileSpeed >= 1024 * 1024 * 100) {
                            double mbps = fileSpeed / (1024 * 1024);
                            if (mbps >= 1000) {
                                ft.setTransferSpeed(String.format("⚡ %.1f GB/s", mbps / 1024));
                            } else if (mbps >= 100) {
                                ft.setTransferSpeed(String.format("🚀 %.0f MB/s", mbps));
                            } else {
                                ft.setTransferSpeed(String.format("⚡ %.1f MB/s", mbps));
                            }
                        } else if (fileSpeed >= 1024 * 1024) {
                            double mbps = fileSpeed / (1024 * 1024);
                            if (mbps >= 10) {
                                ft.setTransferSpeed(String.format("🚀 %.1f MB/s", mbps));
                            } else {
                                ft.setTransferSpeed(String.format("%.2f MB/s", mbps));
                            }
                        } else if (fileSpeed >= 1024) {
                            double kbps = fileSpeed / 1024;
                            if (kbps >= 100) {
                                ft.setTransferSpeed(String.format("%.0f KB/s", kbps));
                            } else if (kbps >= 10) {
                                ft.setTransferSpeed(String.format("%.1f KB/s", kbps));
                            } else {
                                ft.setTransferSpeed(String.format("%.2f KB/s", kbps));
                            }
                        } else {
                            ft.setTransferSpeed(String.format("%.0f B/s", fileSpeed));
                        }

                        if (progress >= 1.0) {
                            ft.setStatus("Completed");
                            ft.setTransferSpeed("Completed");
                            totalFilesCompleted.incrementAndGet();
                            updateOverallProgressDisplay();
                            playNotificationSound();
                        }
                    });

            totalBytesTransferred.addAndGet(bytesSent);
            activeTransferCount.set((int) fileTransfers.stream()
                    .filter(ft -> ft.getStatus().equals("Sending...") || ft.getStatus().equals("Receiving..."))
                    .count());
        });
    }

    private double calculateFileSpeed(FileTransfer fileTransfer, long currentBytes, long currentTime) {
        if (fileTransfer.getLastUpdateTime() == 0) {
            fileTransfer.setLastUpdateTime(currentTime);
            fileTransfer.setLastBytes(currentBytes);
            return 0.0;
        }

        long timeDiff = currentTime - fileTransfer.getLastUpdateTime();
        long bytesDiff = currentBytes - fileTransfer.getLastBytes();

        if (timeDiff > 0 && bytesDiff > 0) {
            double speed = (bytesDiff * 1000.0) / timeDiff;
            fileTransfer.setLastUpdateTime(currentTime);
            fileTransfer.setLastBytes(currentBytes);
            return speed;
        }

        return 0.0;
    }

    public void addReceivedFile(File file, long fileSize) {
        Platform.runLater(() -> {
            FileTransfer incomingTransfer = new FileTransfer(file, fileSize);
            incomingTransfer.setStatus("Received");
            incomingTransfer.setProgress(1.0);
            incomingTransfer.setTransferSpeed("Completed");
            fileTransfers.add(incomingTransfer);
            totalFilesCompleted.incrementAndGet();
            updateFileCount();
            updateOverallProgressDisplay();
            playNotificationSound();
            log("🎉 RECEIVED: " + file.getName() + " (" + formatDataSize(fileSize) + ") - Ultra-speed transfer completed!");
        });
    }

    private void playNotificationSound() {
        try {
            java.awt.Toolkit.getDefaultToolkit().beep();
        } catch (Exception e) {
            // Silent fail - audio is optional
        }
    }

    public void updateServerStatus(String status) {
        Platform.runLater(() -> {
            statusLabel.setText(status);
        });
    }

    // === ULTRA SPEED TRANSFER LAUNCH ===
    private void launchUltraSpeedTransfer() {
        if (fileTransfers.isEmpty()) {
            showAlert("MISSION ABORT", "No files selected for transfer. Please add files to the queue.");
            return;
        }

        if (ipField.getText().trim().isEmpty()) {
            showAlert("CONFIGURATION ERROR", "Please enter receiver IP address for transfer.");
            return;
        }

        initializeFileClient();
        String targetHost = ipField.getText().trim();

        // Test connection
        if (!testConnection(targetHost)) {
            showAlert("CONNECTION ERROR", "Cannot connect to " + targetHost + ":" + serverPort +
                    "\nPlease check:\n• IP address is correct\n• Receiver is running\n• Firewall settings\n• Network connectivity");
            return;
        }

        isTransferring = true;
        isPaused.set(false);
        actionBtn.setDisable(true);
        pauseBtn.setDisable(false);
        cancelBtn.setDisable(false);

        resetProgressTracking();
        totalTransferStartTime.set(System.currentTimeMillis());
        lastSpeedUpdateTime.set(System.currentTimeMillis());
        lastSpeedBytes.set(0);

        startProgressTimer();

        log("🚀 ULTRA-SPEED: Launching transfer to " + targetHost + ":" + serverPort);
        log("📊 STATS: " + fileTransfers.size() + " files, " + formatDataSize(totalFilesSize.get()));
        statusLabel.setText("⚡ ULTRA-SPEED TRANSFER IN PROGRESS");

        // Start file transfers
        int concurrentCount = Math.min(MAX_CONCURRENT_TRANSFERS, fileTransfers.size());
        log("⚡ CONCURRENT: Launching " + concurrentCount + " parallel transfers");

        for (int i = 0; i < concurrentCount; i++) {
            if (i < fileTransfers.size()) {
                FileTransfer fileTransfer = fileTransfers.get(i);
                if (fileTransfer.getStatus().equals("Queued") || fileTransfer.getStatus().equals("Pending")) {
                    fileTransfer.setStatus("Sending...");
                    startFileTransferThread(fileTransfer, targetHost);
                }
            }
        }
    }

    private void startFileTransferThread(FileTransfer fileTransfer, String targetHost) {
        // Create final copies of the variables needed in the lambda
        final String targetHostFinal = targetHost;
        final File file = fileTransfer.getFile();
        final String fileName = fileTransfer.getFileName();

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    long fileSize = file.length();  // LINE 1421 - This is CORRECT
                    long bytesRead = 0;

                    updateFileStatus(fileName, "Connecting...");  // LINE 1427 - This is CORRECT

                    // Simulate file transfer (replace with actual socket code)
                    byte[] buffer = new byte[BUFFER_SIZE];

                    try (FileInputStream fis = new FileInputStream(file);
                         Socket socket = new Socket(targetHostFinal, serverPort);
                         DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                        // Send file info
                        dos.writeUTF(file.getName());
                        dos.writeLong(fileSize);
                        dos.flush();

                        updateFileStatus(fileName, "Transferring...");

                        // Send file data
                        int bytes;
                        while ((bytes = fis.read(buffer)) != -1) {
                            if (isCancelled()) {
                                updateFileStatus(fileName, "Cancelled");
                                return null;
                            }

                            while (isPaused.get()) {
                                Thread.sleep(100);
                                if (isCancelled()) {
                                    updateFileStatus(fileName, "Cancelled");
                                    return null;
                                }
                            }

                            dos.write(buffer, 0, bytes);
                            bytesRead += bytes;

                            // Update progress
                            double progress = (double) bytesRead / fileSize;
                            updateFileProgress(fileName, progress, 0, bytesRead, fileSize);

                            // Small delay to show progress
                            Thread.sleep(10);
                        }

                        dos.flush();
                        markFileAsCompleted(fileName);

                    } catch (Exception e) {
                        log("❌ ERROR: Failed to send " + file.getName() + " - " + e.getMessage());
                        updateFileStatus(fileName, "Failed: " + e.getMessage());
                    }

                } catch (Exception e) {
                    log("❌ ERROR: Transfer thread error - " + e.getMessage());
                }
                return null;
            }
        };

        activeTasks.add(task);
        executorService.submit(task);
    }

    private boolean testConnection(String host) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, serverPort), 3000);
            log("✅ CONNECTION: Successfully connected to " + host + ":" + serverPort);
            return true;
        } catch (Exception e) {
            log("❌ CONNECTION: Failed to connect to " + host + ":" + serverPort + " - " + e.getMessage());
            return false;
        }
    }

    private void startUltraSpeedReceiver() {
        startFileServer();
        log("🔵 ULTRA-SPEED: Receiver activated on port " + serverPort);
        log("⚡ BUFFER: " + (BUFFER_SIZE / 1024) + "KB buffer size configured");
        statusLabel.setText("🔵 ULTRA-SPEED RECEIVER ACTIVE");
    }

    private void missionComplete() {
        stopProgressTimer();

        long totalTime = System.currentTimeMillis() - totalTransferStartTime.get();
        double totalTimeSeconds = totalTime / 1000.0;
        double totalDataMB = totalBytesTransferred.get() / (1024.0 * 1024.0);

        // Calculate accurate average speed
        double averageSpeed = 0.0;
        if (totalTimeSeconds > 0) {
            averageSpeed = totalDataMB / totalTimeSeconds;
        }

        // Create final copies for lambda
        final double finalTotalTimeSeconds = totalTimeSeconds;
        final double finalTotalDataMB = totalDataMB;
        final double finalAverageSpeed = averageSpeed;
        final String formattedTime = formatElapsedTime(totalTime);
        final String formattedData = formatDataSize(totalBytesTransferred.get());
        final int completedFiles = totalFilesCompleted.get();

        Platform.runLater(() -> {
            isTransferring = false;
            isPaused.set(false);
            actionBtn.setDisable(false);
            pauseBtn.setDisable(true);
            cancelBtn.setDisable(true);
            pauseBtn.setText("⏸️ PAUSE ALL");
            transferSpeedLabel.setText("0.00 KB/s");
            statusLabel.setText("🎉 ULTRA-SPEED MISSION ACCOMPLISHED");

            log("🎉 MISSION: Ultra-speed transfer completed successfully!");
            log("📊 STATS: " + completedFiles + " files transferred");
            log("⏱️  TIME: Total time: " + formattedTime + " (" + String.format("%.1f", finalTotalTimeSeconds) + " seconds)");
            log("💾 DATA: Total data: " + formattedData + " (" + String.format("%.2f", finalTotalDataMB) + " MB)");
            log("⚡ SPEED: Average speed: " + String.format("%.2f", finalAverageSpeed) + " MB/s");

            showSuccessAlert("🎉 Ultra-Speed Mission Accomplished",
                    "Successfully transferred " + completedFiles + " files at ultra-high speed!\n\n" +
                            "⏱️  Total time: " + formattedTime + "\n" +
                            "💾 Total data: " + formattedData + "\n" +
                            "⚡ Average speed: " + String.format("%.2f", finalAverageSpeed) + " MB/s");
        });
    }

    private void abortMission() {
        Platform.runLater(() -> {
            stopProgressTimer();

            for (Task<Void> task : activeTasks) {
                task.cancel();
            }
            activeTasks.clear();

            for (FileTransfer fileTransfer : fileTransfers) {
                if (fileTransfer.getStatus().equals("Sending...") || fileTransfer.getStatus().equals("Receiving...") || fileTransfer.getStatus().equals("Paused")) {
                    fileTransfer.setStatus("Cancelled");
                    fileTransfer.setProgress(0.0);
                    fileTransfer.setTransferSpeed("Cancelled");
                }
            }

            isTransferring = false;
            isPaused.set(false);
            actionBtn.setDisable(false);
            pauseBtn.setDisable(true);
            cancelBtn.setDisable(true);
            pauseBtn.setText("⏸️ PAUSE ALL");
            statusLabel.setText("❌ MISSION ABORTED");
            transferSpeedLabel.setText("0.00 KB/s");

            int cancelledCount = (int) fileTransfers.stream()
                    .filter(ft -> ft.getStatus().equals("Cancelled"))
                    .count();

            log("⚠️ MISSION: Ultra-speed transfer aborted by user");
            log("❌ FILES: " + cancelledCount + " transfers cancelled");

            resetProgressTracking();
            updateButtonStates();
        });
    }

    private void resetProgressTracking() {
        totalFilesCompleted.set(0);
        totalBytesTransferred.set(0);
        totalFilesSize.set(0);
        activeTransferCount.set(0);
        lastSpeedUpdateTime.set(0);
        lastSpeedBytes.set(0);
        currentSpeedMBps = 0.0;
        averageSpeedMBps = 0.0;
        overallProgressBar.setProgress(0);
        overallProgressLabel.setText("0/0 FILES COMPLETED");
        transferSpeedLabel.setText("0.00 KB/s");

        timeElapsedLabel.setText("00:00:00");
        dataTransferredLabel.setText("0 B");
        activeTransfersLabel.setText("0");
        completedFilesLabel.setText("0");
        remainingTimeLabel.setText("--:--");
    }

    // === CLIENT-SERVER METHODS ===
    public void startFileServer() {
        if (fileTransferServer == null) {
            fileTransferServer = new FileTransferServer(this, serverPort, BUFFER_SIZE);
        }
        fileTransferServer.startUltraSpeedServer();
    }

    public void stopFileServer() {
        if (fileTransferServer != null) {
            fileTransferServer.stopServer();
            fileTransferServer = null;
        }
    }

    private void startReceiverServer() {
        startFileServer();
        log("🔵 RECEIVER: Server activated on port " + serverPort);
        statusLabel.setText("🔵 LISTENING FOR INCOMING FILES");
    }

    private void stopReceiverServer() {
        stopFileServer();
        log("🔴 RECEIVER: Server deactivated");
    }

    private void initializeFileClient() {
        if (fileTransferClient == null) {
            fileTransferClient = new FileTransferClient(this, BUFFER_SIZE);
        }
    }

    // === HELPER METHODS ===
    @FXML
    private void openGitHub() {
        openWebPage("https://github.com/kueiyiee");
    }

    @FXML
    private void openLinkedIn() {
        openWebPage("https://www.linkedin.com/in/kueiyieeyt");
    }

    @FXML
    private void contactDeveloper() {
        Platform.runLater(() -> {
            Alert alert = createStyledAlert("📧 Contact Developer", "Kuei Poch Kuei - Contact Information");
            VBox content = new VBox(15);
            content.setAlignment(Pos.CENTER_LEFT);
            content.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 20;");

            VBox contactBox = new VBox(10);
            contactBox.setAlignment(Pos.CENTER_LEFT);

            addContactInfo(contactBox, "📧 Personal Email:", "kueiyiee@gmail.com", "#00e676");
            addContactInfo(contactBox, "🎓 University Email:", "kuei.poch@du.edu.et", "#2196f3");
            addContactInfo(contactBox, "📱 Phone Number:", "+251 937 910 246", "#ff6d00");

            content.getChildren().add(contactBox);
            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setPrefSize(500, 550);
            alert.setResizable(true);
            alert.showAndWait();
        });

        log("📞 DEVELOPER: Contact information displayed");
    }

    private void addContactInfo(VBox parent, String labelText, String value, String color) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #bdbdbd; -fx-font-weight: bold;");
        Hyperlink link = new Hyperlink(value);
        link.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-border-color: transparent;");

        // Create final copies for lambda
        final String finalValue = value;
        final String finalLabelText = labelText;

        link.setOnAction(e -> {
            if (finalLabelText.contains("Email")) {
                openEmailClient(finalValue, "ShareIT Premium Suite Inquiry",
                        "Hello Kuei,\n\nI'm interested in your ShareIT Premium Suite application.\n\nBest regards,\n[Your Name]");
            } else {
                openPhoneDialer(finalValue.replaceAll("[^0-9+]", ""));
            }
        });

        box.getChildren().addAll(label, link);
        parent.getChildren().add(box);
    }

    private void openEmailClient(String email, String subject, String body) {
        try {
            String encodedSubject = URLEncoder.encode(subject, "UTF-8").replace("+", "%20");
            String encodedBody = URLEncoder.encode(body, "UTF-8").replace("+", "%20");
            String mailto = String.format("mailto:%s?subject=%s&body=%s", email, encodedSubject, encodedBody);
            java.awt.Desktop.getDesktop().browse(new java.net.URI(mailto));
            log("📧 EMAIL: Opening email client for: " + email);
        } catch (Exception e) {
            log("❌ ERROR: Failed to open email client - " + e.getMessage());
            copyToClipboard(email);
            showSuccessAlert("Email Ready", "Email address copied to clipboard: " + email);
        }
    }

    private void openPhoneDialer(String phoneNumber) {
        try {
            String cleanNumber = phoneNumber.replaceAll("[^0-9+]", "");
            String telUri = "tel:" + cleanNumber;
            java.awt.Desktop.getDesktop().browse(new java.net.URI(telUri));
            log("📱 PHONE: Opening dialer for: " + phoneNumber);
        } catch (Exception e) {
            log("❌ ERROR: Failed to open phone dialer - " + e.getMessage());
            copyToClipboard(phoneNumber);
            showSuccessAlert("Phone Ready", "Phone number copied to clipboard: " + phoneNumber);
        }
    }

    private void copyToClipboard(String text) {
        try {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(text);
            clipboard.setContent(content);
            log("📋 CLIPBOARD: Copied to clipboard: " + text);
        } catch (Exception e) {
            log("❌ ERROR: Failed to copy to clipboard - " + e.getMessage());
        }
    }

    private void openWebPage(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
            log("🌐 DEVELOPER: Opening " + url);
        } catch (Exception e) {
            log("❌ ERROR: Failed to open web page - " + e.getMessage());
            showAlert("Browser Error", "Cannot open web browser. Please visit manually:\n" + url);
        }
    }

    private void logSystemStart() {
        String gregorianDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String ethiopianDate = getEthiopianDateFormatted();

        log("🚀 === SHAREIT PREMIUM SUITE ULTRA v3.0 ===");
        log("📅 SYSTEM: Gregorian Date: " + gregorianDate);
        log("📅 SYSTEM: Ethiopian Date: " + ethiopianDate);
        log("🌍 SYSTEM: Time Zone: " + ZoneId.systemDefault().getId());
        log("⚡ SYSTEM: Ultra-high speed transfer engine ready");
        log("👨‍💻 DEVELOPER: Kuei Poch Kuei - 3rd Year CS Student");
        log("🎓 DEVELOPER: Dilla University - College of Engineering");
        log("🌟 SYSTEM: Ultra-speed features activated - Ready for mission!");
    }

    public void log(String message) {
        Platform.runLater(() -> {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            logArea.appendText("[" + timestamp + "] " + message + "\n");
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: white; -fx-border-color: #00c853;");
            alert.showAndWait();
        });
    }

    private void showSuccessAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: white; -fx-border-color: #00e676;");
            alert.showAndWait();
        });
    }

    public void shutdown() {
        abortMission();
        stopFileServer();
        stopReceiverServer();
        executorService.shutdownNow();
        log("🔴 SYSTEM: ShareIT Premium Ultra shutting down...");
    }

    private int addFolderFiles(File folder) {
        int newFiles = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    newFiles += addFolderFiles(file);
                } else {
                    if (!isFileAlreadyAdded(file)) {
                        FileTransfer fileTransfer = new FileTransfer(file);
                        fileTransfer.setStatus("Queued");
                        fileTransfers.add(fileTransfer);
                        newFiles++;
                    }
                }
            }
        }
        return newFiles;
    }

    private boolean isFileAlreadyAdded(File file) {
        return fileTransfers.stream().anyMatch(ft -> ft.getFile().getAbsolutePath().equals(file.getAbsolutePath()));
    }

    // === THEME METHODS ===
    @FXML
    private void setDarkTheme() {
        applyTheme("dark");
        log("🎨 THEME: Dark theme activated");
    }

    @FXML
    private void setLightTheme() {
        applyTheme("light");
        log("🎨 THEME: Light theme activated");
    }

    @FXML
    private void setBlueTheme() {
        applyTheme("blue");
        log("🎨 THEME: Blue theme activated");
    }

    @FXML
    private void setPurpleTheme() {
        applyTheme("purple");
        log("🎨 THEME: Purple theme activated");
    }

    private void applyTheme(String theme) {
        currentTheme = theme;
        Platform.runLater(() -> {
            switch (theme) {
                case "light": applyLightTheme(); break;
                case "blue": applyBlueTheme(); break;
                case "purple": applyPurpleTheme(); break;
                default: applyDarkTheme(); break;
            }
        });
    }

    private void applyDarkTheme() {
        headerSection.setStyle("-fx-background-color: linear-gradient(to right, #0a0a0a, #00c853, #0a0a0a); -fx-border-color: #00c853;");
        operationModeSection.setStyle("-fx-background-color: #151515;");
        fileManagementSection.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #00c853;");
        fileTransferPanel.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #00c853;");
        analyticsPanel.setStyle("-fx-background-color: #151515; -fx-border-color: #00c853;");
        logPanel.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #00c853;");
        footerSection.setStyle("-fx-background-color: #151515; -fx-border-color: #00c853;");
        updateMenuButtonForTheme("#00e676");
        updateTextColors("#00e676", "#bdbdbd");
        if (logArea != null) logArea.setStyle("-fx-background-color: #0a0a0a; -fx-text-fill: #00e676;");
    }

    private void applyLightTheme() {
        headerSection.setStyle("-fx-background-color: linear-gradient(to right, #ffffff, #4caf50, #ffffff); -fx-border-color: #4caf50;");
        operationModeSection.setStyle("-fx-background-color: #fafafa;");
        fileManagementSection.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #4caf50;");
        fileTransferPanel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #4caf50;");
        analyticsPanel.setStyle("-fx-background-color: #fafafa; -fx-border-color: #4caf50;");
        logPanel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #4caf50;");
        footerSection.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #4caf50;");
        updateMenuButtonForTheme("#2e7d32");
        updateTextColors("#2e7d32", "#666666");
        if (logArea != null) logArea.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #2e7d32;");
    }

    private void applyBlueTheme() {
        headerSection.setStyle("-fx-background-color: linear-gradient(to right, #0a1a2a, #2196f3, #0a1a2a); -fx-border-color: #2196f3;");
        operationModeSection.setStyle("-fx-background-color: #152535;");
        fileManagementSection.setStyle("-fx-background-color: #1a2a3a; -fx-border-color: #2196f3;");
        fileTransferPanel.setStyle("-fx-background-color: #1a2a3a; -fx-border-color: #2196f3;");
        analyticsPanel.setStyle("-fx-background-color: #152535; -fx-border-color: #2196f3;");
        logPanel.setStyle("-fx-background-color: #1a2a3a; -fx-border-color: #2196f3;");
        footerSection.setStyle("-fx-background-color: #152535; -fx-border-color: #2196f3;");
        updateMenuButtonForTheme("#2196f3");
        updateTextColors("#2196f3", "#90caf9");
        if (logArea != null) logArea.setStyle("-fx-background-color: #0a1a2a; -fx-text-fill: #2196f3;");
    }

    private void applyPurpleTheme() {
        headerSection.setStyle("-fx-background-color: linear-gradient(to right, #1a0a2a, #9c27b0, #1a0a2a); -fx-border-color: #9c27b0;");
        operationModeSection.setStyle("-fx-background-color: #251535;");
        fileManagementSection.setStyle("-fx-background-color: #2a1a3a; -fx-border-color: #9c27b0;");
        fileTransferPanel.setStyle("-fx-background-color: #2a1a3a; -fx-border-color: #9c27b0;");
        analyticsPanel.setStyle("-fx-background-color: #251535; -fx-border-color: #9c27b0;");
        logPanel.setStyle("-fx-background-color: #2a1a3a; -fx-border-color: #9c27b0;");
        footerSection.setStyle("-fx-background-color: #251535; -fx-border-color: #9c27b0;");
        updateMenuButtonForTheme("#9c27b0");
        updateTextColors("#9c27b0", "#ce93d8");
        if (logArea != null) logArea.setStyle("-fx-background-color: #1a0a2a; -fx-text-fill: #9c27b0;");
    }

    private void updateMenuButtonForTheme(String primaryColor) {
        if (menuButton != null) {
            String style = String.format(
                    "-fx-background-color: linear-gradient(to bottom, %s, %s); " +
                            "-fx-text-fill: #000000; -fx-font-size: 24; -fx-font-weight: bold; " +
                            "-fx-pref-width: 45; -fx-pref-height: 45; -fx-background-radius: 10; " +
                            "-fx-effect: dropshadow(gaussian, %s, 10, 0.5, 0, 0);",
                    darkenColor(primaryColor), primaryColor, primaryColor
            );
            menuButton.setStyle(style);
        }
    }

    private String darkenColor(String color) {
        switch (color) {
            case "#00e676": return "#00c853";
            case "#2e7d32": return "#1b5e20";
            case "#2196f3": return "#1976d2";
            case "#9c27b0": return "#7b1fa2";
            default: return color;
        }
    }

    private void updateTextColors(String primaryColor, String secondaryColor) {
        if (statusLabel != null) statusLabel.setStyle("-fx-text-fill: " + primaryColor + ";");
        if (fileCountLabel != null) fileCountLabel.setStyle("-fx-text-fill: " + primaryColor + ";");
        if (transferSpeedLabel != null) transferSpeedLabel.setStyle("-fx-text-fill: " + primaryColor + ";");
        if (timeElapsedLabel != null) timeElapsedLabel.setStyle("-fx-text-fill: " + primaryColor + ";");
        if (dataTransferredLabel != null) dataTransferredLabel.setStyle("-fx-text-fill: " + primaryColor + ";");
        if (activeTransfersLabel != null) activeTransfersLabel.setStyle("-fx-text-fill: " + primaryColor + ";");
        if (completedFilesLabel != null) completedFilesLabel.setStyle("-fx-text-fill: " + primaryColor + ";");
        if (remainingTimeLabel != null) remainingTimeLabel.setStyle("-fx-text-fill: " + primaryColor + ";");
        if (overallProgressLabel != null) overallProgressLabel.setStyle("-fx-text-fill: " + primaryColor + ";");
        if (copyrightLabel != null) copyrightLabel.setStyle("-fx-text-fill: " + primaryColor + ";");
    }

    // === FONT SCALING ===
    @FXML
    private void setSmallFont() {
        applyFontSize("small");
        log("🔤 FONT: Small font size applied");
    }

    @FXML
    private void setMediumFont() {
        applyFontSize("medium");
        log("🔤 FONT: Medium font size applied");
    }

    @FXML
    private void setLargeFont() {
        applyFontSize("large");
        log("🔤 FONT: Large font size applied");
    }

    @FXML
    private void setXLargeFont() {
        applyFontSize("xlarge");
        log("🔤 FONT: Extra large font size applied");
    }

    private void applyFontSize(String size) {
        double scaleFactor;
        switch (size) {
            case "small": scaleFactor = 0.85; break;
            case "medium": scaleFactor = 1.0; break;
            case "large": scaleFactor = 1.25; break;
            case "xlarge": scaleFactor = 1.5; break;
            default: scaleFactor = 1.0;
        }

        currentFontScale = scaleFactor;
        Platform.runLater(() -> {
            applyFontScalingToAllElements(scaleFactor);
        });
    }

    private void applyFontScalingToAllElements(double scaleFactor) {
        scaleLabelFont(operationModeLabel, 18, scaleFactor);
        scaleRadioButtonFont(senderModeRadio, 14, scaleFactor);
        scaleLabelFont(senderFeature1, 12, scaleFactor);
        scaleLabelFont(senderFeature2, 12, scaleFactor);
        scaleRadioButtonFont(receiverMode, 14, scaleFactor);
        scaleLabelFont(receiverFeature1, 12, scaleFactor);
        scaleLabelFont(receiverFeature2, 12, scaleFactor);
        scaleLabelFont(ipLabel, 14, scaleFactor);
        scaleButtonFont(getIpButton, 13, scaleFactor);
        scaleButtonFont(selectFilesBtn, 13, scaleFactor);
        scaleButtonFont(selectFolderBtn, 13, scaleFactor);
        scaleButtonFont(clearFilesBtn, 13, scaleFactor);
        scaleLabelFont(fileCountLabel, 14, scaleFactor);
        scaleLabelFont(timeElapsedLabel, 16, scaleFactor);
        scaleLabelFont(dataTransferredLabel, 16, scaleFactor);
        scaleLabelFont(activeTransfersLabel, 16, scaleFactor);
        scaleLabelFont(transferSpeedLabel, 16, scaleFactor);
        scaleLabelFont(completedFilesLabel, 16, scaleFactor);
        scaleLabelFont(remainingTimeLabel, 16, scaleFactor);
        scaleLabelFont(overallProgressLabel, 12, scaleFactor);
        scaleButtonFont(actionBtn, 16, scaleFactor);
        scaleButtonFont(pauseBtn, 13, scaleFactor);
        scaleButtonFont(cancelBtn, 13, scaleFactor);
        scaleLabelFont(statusLabel, 14, scaleFactor);

        if (logArea != null) {
            double newSize = 12 * scaleFactor;
            logArea.setStyle(logArea.getStyle().replaceAll("-fx-font-size:\\s*\\d+(\\.\\d+)?", "")
                    .replaceAll(";\\s*;", ";") + String.format(" -fx-font-size: %.1f;", newSize));
        }

        if (copyrightLabel != null) {
            double newSize = 12 * scaleFactor;
            copyrightLabel.setStyle(copyrightLabel.getStyle().replaceAll("-fx-font-size:\\s*\\d+(\\.\\d+)?", "")
                    .replaceAll(";\\s*;", ";") + String.format(" -fx-font-size: %.1f;", newSize));
        }
    }

    private void scaleLabelFont(Label label, double baseSize, double scaleFactor) {
        if (label != null) {
            double newSize = baseSize * scaleFactor;
            label.setStyle(label.getStyle().replaceAll("-fx-font-size:\\s*\\d+(\\.\\d+)?", "")
                    .replaceAll(";\\s*;", ";") + String.format(" -fx-font-size: %.1f;", newSize));
        }
    }

    private void scaleButtonFont(Button button, double baseSize, double scaleFactor) {
        if (button != null) {
            double newSize = baseSize * scaleFactor;
            button.setStyle(button.getStyle().replaceAll("-fx-font-size:\\s*\\d+(\\.\\d+)?", "")
                    .replaceAll(";\\s*;", ";") + String.format(" -fx-font-size: %.1f;", newSize));
        }
    }

    private void scaleRadioButtonFont(RadioButton radio, double baseSize, double scaleFactor) {
        if (radio != null) {
            double newSize = baseSize * scaleFactor;
            radio.setStyle(radio.getStyle().replaceAll("-fx-font-size:\\s*\\d+(\\.\\d+)?", "")
                    .replaceAll(";\\s*;", ";") + String.format(" -fx-font-size: %.1f;", newSize));
        }
    }

    private void applyFontScalingToContextMenu(ContextMenu menu, double scaleFactor) {
        for (MenuItem item : menu.getItems()) {
            if (item instanceof Menu) {
                applyFontScalingToMenu((Menu) item, scaleFactor);
            }
        }
    }

    private void applyFontScalingToMenu(Menu menu, double scaleFactor) {
        double newSize = 14 * scaleFactor;
        menu.setStyle(menu.getStyle().replaceAll("-fx-font-size:\\s*\\d+(\\.\\d+)?", "")
                + String.format(" -fx-font-size: %.1f;", newSize));
    }

    // === DIALOG METHODS ===
    private void showDeveloperProfileDialog() {
        Platform.runLater(() -> {
            Alert alert = createStyledAlert("👨‍💻 Developer Profile", "Kuei Poch Kuei - Software Engineer");
            VBox content = new VBox(15);
            content.setAlignment(Pos.CENTER_LEFT);
            content.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 20;");

            TextFlow profileFlow = new TextFlow();
            profileFlow.getChildren().addAll(
                    createStyledText("👤 Name: Kuei Poch Kuei\n\n", "#00e676", 14, true),
                    createStyledText("🎓 Education:\n", "#ffffff", 13, true),
                    createStyledText("   • DILLA UNIVERSITY\n", "#80cbc4", 13, true),
                    createStyledText("   • College of Engineering and Technology\n", "#bdbdbd", 13, false),
                    createStyledText("   • Department of Computer Science\n", "#bdbdbd", 13, false),
                    createStyledText("   • 3rd Year Computer Science Student\n\n", "#ff6d00", 13, true),
                    createStyledText("💻 Technical Skills:\n", "#ffffff", 13, true),
                    createStyledText("   • Java & JavaFX Development\n", "#bdbdbd", 13, false),
                    createStyledText("   • Network Programming\n", "#bdbdbd", 13, false),
                    createStyledText("   • Multi-threaded Applications\n\n", "#bdbdbd", 13, false),
                    createStyledText("💡 Motto: \"My Logic is your Limit\"", "#ff6d00", 13, true)
            );

            content.getChildren().add(profileFlow);
            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setPrefSize(400, 400);
            alert.showAndWait();
        });
    }

    private void showCompleteUserManual() {
        Platform.runLater(() -> {
            Alert alert = createStyledAlert("📚 ShareIT Premium - Complete User Manual", "Complete Step-by-Step User Guide");
            VBox content = new VBox(20);
            content.setAlignment(Pos.CENTER_LEFT);
            content.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 20;");

            TextFlow manual = new TextFlow();
            manual.getChildren().addAll(
                    createStyledText("🚀 SHAREIT PREMIUM ULTRA USER MANUAL\n\n", "#00e676", 18, true),
                    createStyledText("1. GETTING STARTED\n", "#bdbdbd", 14, true),
                    createStyledText("Welcome to ShareIT Premium Ultra! Select your operation mode and configure network settings.\n\n", "#ffffff", 13, false),
                    createStyledText("2. SENDER MODE\n", "#bdbdbd", 14, true),
                    createStyledText("• Select Sender Mode\n• Enter receiver IP address\n• Add files to transfer queue\n• Click Launch Transfer\n\n", "#ffffff", 13, false),
                    createStyledText("3. RECEIVER MODE\n", "#bdbdbd", 14, true),
                    createStyledText("• Select Receiver Mode\n• Click 'Get My IP'\n• Share IP with sender\n• Click Start Listening\n", "#ffffff", 13, false)
            );

            content.getChildren().add(manual);
            alert.getDialogPane().setContent(content);
            alert.setResizable(true);
            alert.showAndWait();
        });
    }

    private void showUserGuideDialog() {
        Platform.runLater(() -> {
            Alert alert = createStyledAlert("📖 Quick Start Guide", "ShareIT Premium Ultra - Quick Start");
            VBox content = new VBox(15);
            content.setAlignment(Pos.CENTER_LEFT);
            content.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 20;");

            TextFlow guide = new TextFlow();
            guide.getChildren().addAll(
                    createStyledText("1. Choose Operation Mode:\n", "#bdbdbd", 14, true),
                    createStyledText("   • 🚀 SENDER: To send files\n   • 📥 RECEIVER: To receive files\n\n", "#ffffff", 13, false),
                    createStyledText("2. Configure Network:\n", "#bdbdbd", 14, true),
                    createStyledText("   • SENDER: Enter receiver IP\n   • RECEIVER: Click 'Get My IP'\n\n", "#ffffff", 13, false),
                    createStyledText("3. Transfer Files:\n", "#bdbdbd", 14, true),
                    createStyledText("   • Select files or folders\n   • Monitor progress in real-time\n   • Use pause/resume as needed\n", "#ffffff", 13, false)
            );

            content.getChildren().add(guide);
            alert.getDialogPane().setContent(content);
            alert.showAndWait();
        });
    }

    private void showAboutDialog() {
        Platform.runLater(() -> {
            Alert alert = createStyledAlert("ℹ️ About ShareIT Premium Suite Ultra", null);
            VBox content = new VBox(15);
            content.setAlignment(Pos.CENTER);
            content.setStyle("-fx-background-color: #1a1a1a; -fx-padding: 20;");

            ImageView logo = new ImageView();
            logo.setFitWidth(80);
            logo.setFitHeight(80);
            logo.setStyle("-fx-background-color: #00e676; -fx-background-radius: 40;");

            Label titleLabel = new Label("🚀 ShareIT Premium Suite Ultra v3.0");
            titleLabel.setStyle("-fx-text-fill: #00e676; -fx-font-weight: bold; -fx-font-size: 18;");

            TextFlow about = new TextFlow();
            about.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            about.getChildren().addAll(
                    createStyledText("Ultra-High Speed Multi-Threaded Secure File Transfer System\n\n", "#bdbdbd", 14, false),
                    createStyledText("Developed with ❤️ by Kuei Poch Kuei\n", "#ff6d00", 13, true),
                    createStyledText("Dilla University - College of Engineering & Technology\n", "#bdbdbd", 13, false),
                    createStyledText("3rd Year Computer Science Student\n\n", "#bdbdbd", 13, false),
                    createStyledText("Current Date: " + getCurrentDateInfo() + "\n", "#00e676", 12, false)
            );

            content.getChildren().addAll(logo, titleLabel, about);
            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setPrefSize(500, 400);

            ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            alert.getDialogPane().getButtonTypes().setAll(okButton);

            Button okBtn = (Button) alert.getDialogPane().lookupButton(okButton);
            if (okBtn != null) {
                okBtn.setStyle("-fx-background-color: #00e676; -fx-text-fill: black; -fx-font-weight: bold;");
            }

            alert.showAndWait();
        });
    }

    // === FILE TRANSFER DATA MODEL ===
    public static class FileTransfer {
        private final File file;
        private final long fileSize;
        private final StringProperty fileName;
        private final StringProperty fileSizeFormatted;
        private final StringProperty status;
        private final DoubleProperty progress;
        private final StringProperty transferSpeed;
        private long lastUpdateTime = 0;
        private long lastBytes = 0;

        public FileTransfer(File file) {
            this.file = file;
            this.fileSize = file.length();
            this.fileName = new SimpleStringProperty(file.getName());
            this.fileSizeFormatted = new SimpleStringProperty(formatFileSize(fileSize));
            this.status = new SimpleStringProperty("Pending");
            this.progress = new SimpleDoubleProperty(0.0);
            this.transferSpeed = new SimpleStringProperty("0.00 KB/s");
        }

        public FileTransfer(File file, long fileSize) {
            this.file = file;
            this.fileSize = fileSize;
            this.fileName = new SimpleStringProperty(file.getName());
            this.fileSizeFormatted = new SimpleStringProperty(formatFileSize(fileSize));
            this.status = new SimpleStringProperty("Pending");
            this.progress = new SimpleDoubleProperty(0.0);
            this.transferSpeed = new SimpleStringProperty("0.00 KB/s");
        }

        // === FIXED: IMPROVED FILE SIZE FORMATTING ===
        private static String formatFileSize(long size) {
            if (size < 1024) return size + " B";
            else if (size < 1024 * 1024) {
                double kb = size / 1024.0;
                if (kb < 10) return String.format("%.2f KB", kb);
                else if (kb < 100) return String.format("%.1f KB", kb);
                else return String.format("%.0f KB", kb);
            } else if (size < 1024 * 1024 * 1024) {
                double mb = size / (1024.0 * 1024.0);
                if (mb < 10) return String.format("%.2f MB", mb);
                else if (mb < 100) return String.format("%.1f MB", mb);
                else return String.format("%.0f MB", mb);
            } else {
                double gb = size / (1024.0 * 1024.0 * 1024.0);
                if (gb < 10) return String.format("%.2f GB", gb);
                else if (gb < 100) return String.format("%.1f GB", gb);
                else return String.format("%.0f GB", gb);
            }
        }

        public File getFile() { return file; }
        public long getFileSizeBytes() { return fileSize; }
        public String getFileName() { return fileName.get(); }
        public StringProperty fileNameProperty() { return fileName; }
        public String getFileSizeFormatted() { return fileSizeFormatted.get(); }
        public StringProperty fileSizeProperty() { return fileSizeFormatted; }
        public String getStatus() { return status.get(); }
        public void setStatus(String status) { this.status.set(status); }
        public StringProperty statusProperty() { return status; }
        public double getProgress() { return progress.get(); }
        public void setProgress(double progress) { this.progress.set(progress); }
        public DoubleProperty progressProperty() { return progress; }
        public String getTransferSpeed() { return transferSpeed.get(); }
        public void setTransferSpeed(String speed) { this.transferSpeed.set(speed); }
        public StringProperty transferSpeedProperty() { return transferSpeed; }
        public long getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(long time) { this.lastUpdateTime = time; }
        public long getLastBytes() { return lastBytes; }
        public void setLastBytes(long bytes) { this.lastBytes = bytes; }
    }

    // === INNER NETWORK CLASSES ===

    // FileTransferServer class
    public class FileTransferServer {
        private ServerSocket serverSocket;
        private FileTransferController controller;
        private int port;
        private int bufferSize;
        private boolean running;

        public FileTransferServer(FileTransferController controller, int port, int bufferSize) {
            this.controller = controller;
            this.port = port;
            this.bufferSize = bufferSize;
        }

        public void startUltraSpeedServer() {
            try {
                serverSocket = new ServerSocket(port);
                running = true;
                controller.log("✅ SERVER: Listening on port " + port);

                new Thread(() -> {
                    while (running) {
                        try {
                            Socket clientSocket = serverSocket.accept();
                            controller.log("🔗 SERVER: Client connected from " +
                                    clientSocket.getInetAddress().getHostAddress());

                            // Handle client in separate thread
                            new ClientHandler(clientSocket, controller, bufferSize).start();
                        } catch (IOException e) {
                            if (running) {
                                controller.log("❌ SERVER: Connection error - " + e.getMessage());
                            }
                        }
                    }
                }).start();

            } catch (IOException e) {
                controller.log("❌ SERVER: Failed to start - " + e.getMessage());
            }
        }

        public void stopServer() {
            running = false;
            try {
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                // Ignore
            }
        }

        private class ClientHandler extends Thread {
            private Socket socket;
            private FileTransferController controller;
            private int bufferSize;

            public ClientHandler(Socket socket, FileTransferController controller, int bufferSize) {
                this.socket = socket;
                this.controller = controller;
                this.bufferSize = bufferSize;
            }

            @Override
            public void run() {
                try (DataInputStream dis = new DataInputStream(socket.getInputStream())) {
                    // Read file info
                    String fileName = dis.readUTF();
                    long fileSize = dis.readLong();

                    controller.log("📥 RECEIVING: " + fileName + " (" + fileSize + " bytes)");

                    // Create file in downloads folder
                    String downloadsPath = System.getProperty("user.home") + "/Downloads/ShareIT_Received/";
                    File downloadsDir = new File(downloadsPath);
                    if (!downloadsDir.exists()) {
                        downloadsDir.mkdirs();
                    }

                    File outputFile = new File(downloadsPath + fileName);
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[bufferSize];
                        long totalRead = 0;
                        int bytesRead;

                        controller.updateFileStatus(fileName, "Receiving...");

                        while (totalRead < fileSize && (bytesRead = dis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            totalRead += bytesRead;

                            // Update progress
                            double progress = (double) totalRead / fileSize;
                            controller.updateFileProgress(fileName, progress, 0, totalRead, fileSize);

                            if (controller.isPaused.get()) {
                                while (controller.isPaused.get()) {
                                    Thread.sleep(100);
                                }
                            }
                        }

                        fos.flush();
                        controller.markFileAsCompleted(fileName);
                        controller.addReceivedFile(outputFile, fileSize);

                        controller.log("✅ RECEIVED: " + fileName + " successfully saved to " + outputFile.getAbsolutePath());

                    } catch (Exception e) {
                        controller.log("❌ ERROR: Failed to save file " + fileName + " - " + e.getMessage());
                        controller.updateFileStatus(fileName, "Failed: " + e.getMessage());
                    }

                } catch (Exception e) {
                    controller.log("❌ ERROR: File transfer failed - " + e.getMessage());
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // Ignore
                    }
                }
            }
        }
    }

    // FileTransferClient class
    public class FileTransferClient {
        private FileTransferController controller;
        private int bufferSize;

        public FileTransferClient(FileTransferController controller, int bufferSize) {
            this.controller = controller;
            this.bufferSize = bufferSize;
        }

        public boolean testUltraSpeedConnection(String host, int port) {
            return controller.testConnection(host);
        }

        public void sendFileUltraSpeed(File file, String host, int port, int bufferSize) {
            // This is handled by startFileTransferThread method in controller
            controller.startFileTransferThread(new FileTransfer(file), host);
        }
    }
}