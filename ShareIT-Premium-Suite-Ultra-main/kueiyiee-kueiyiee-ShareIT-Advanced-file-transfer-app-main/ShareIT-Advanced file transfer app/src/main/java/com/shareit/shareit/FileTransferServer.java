package com.shareit.shareit;

import javafx.application.Platform;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FileTransferServer {
    private ServerSocket serverSocket;
    private ExecutorService clientThreadPool;
    private FileTransferController controller;
    private boolean isRunning = false;
    private int port;
    private int bufferSize = 8192 * 8; // Default 64KB buffer
    private AtomicInteger activeConnections = new AtomicInteger(0);

    // Constructor with default buffer
    public FileTransferServer(FileTransferController controller, int port) {
        this.controller = controller;
        this.port = port;
        this.clientThreadPool = Executors.newFixedThreadPool(15); // Increased pool size
    }

    // Constructor with custom buffer size
    public FileTransferServer(FileTransferController controller, int port, int bufferSize) {
        this.controller = controller;
        this.port = port;
        this.bufferSize = bufferSize;
        this.clientThreadPool = Executors.newFixedThreadPool(20); // Ultra-speed pool size
        log("⚡ SERVER: Ultra-speed server initialized with " + (bufferSize/1024) + "KB buffer");
    }

    // Standard server start
    public void startServer() {
        if (isRunning) {
            log("SERVER: Server is already running");
            return;
        }

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true); // Allow port reuse
            isRunning = true;
            log("🚀 SERVER: Started on port " + port);
            log("⚡ SERVER: Using " + (bufferSize/1024) + "KB buffer size");
            log("👂 SERVER: Waiting for incoming connections...");

            // Update UI
            Platform.runLater(() -> {
                if (controller != null) {
                    controller.updateServerStatus("🟢 SERVER RUNNING - Port " + port);
                }
            });

            // Start accepting connections in a new thread
            Thread acceptThread = new Thread(this::acceptConnections);
            acceptThread.setDaemon(true);
            acceptThread.start();

        } catch (IOException e) {
            log("💥 SERVER ERROR: Failed to start server - " + e.getMessage());
            isRunning = false;
        }
    }

    // === NEW: Ultra-Speed Server Start ===
    public void startUltraSpeedServer() {
        if (isRunning) {
            log("SERVER: Server is already running");
            return;
        }

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true); // Allow port reuse
            serverSocket.setReceiveBufferSize(bufferSize * 2); // Double buffer for better performance

            isRunning = true;
            log("⚡ SERVER: ULTRA-SPEED server started on port " + port);
            log("🚀 SERVER: Maximum concurrent connections: " + clientThreadPool);
            log("📊 SERVER: Buffer size optimized to " + (bufferSize/1024) + "KB");
            log("🌐 SERVER: Ready for cross-network ultra-speed transfers");
            log("👂 SERVER: Listening for incoming connections...");

            // Update UI
            Platform.runLater(() -> {
                if (controller != null) {
                    controller.updateServerStatus("⚡ ULTRA-SPEED SERVER - Port " + port);
                }
            });

            // Start accepting connections in a high-priority thread
            Thread acceptThread = new Thread(this::acceptConnections);
            acceptThread.setDaemon(true);
            acceptThread.setPriority(Thread.MAX_PRIORITY);
            acceptThread.start();

        } catch (IOException e) {
            log("💥 SERVER ERROR: Failed to start ultra-speed server - " + e.getMessage());
            isRunning = false;
        }
    }

    private void acceptConnections() {
        while (isRunning && serverSocket != null && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                int currentConnections = activeConnections.incrementAndGet();

                log("🌐 SERVER: New connection from " + clientAddress +
                        " (Active connections: " + currentConnections + ")");

                // Optimize socket for high-speed transfers
                optimizeSocket(clientSocket);

                // Handle client in a separate thread
                clientThreadPool.execute(() -> {
                    try {
                        handleClient(clientSocket);
                    } finally {
                        activeConnections.decrementAndGet();
                    }
                });

            } catch (IOException e) {
                if (isRunning) {
                    log("💥 SERVER ERROR: Connection accept failed - " + e.getMessage());
                }
            }
        }
    }

    // === NEW: Socket Optimization ===
    private void optimizeSocket(Socket socket) throws SocketException {
        // Critical optimizations for ultra-speed transfers
        socket.setTcpNoDelay(true); // Disable Nagle's algorithm for low latency
        socket.setSoTimeout(60000); // 60 second timeout for large files
        socket.setSendBufferSize(bufferSize * 2); // Optimized send buffer
        socket.setReceiveBufferSize(bufferSize * 2); // Optimized receive buffer
        socket.setKeepAlive(true); // Enable keep-alive
        socket.setPerformancePreferences(0, 2, 1); // Prioritize bandwidth over latency
    }

    private void handleClient(Socket clientSocket) {
        String clientAddress = clientSocket.getInetAddress().getHostAddress();

        try (InputStream inputStream = clientSocket.getInputStream();
             DataInputStream dataInputStream = new DataInputStream(inputStream);
             OutputStream outputStream = clientSocket.getOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {

            // First, read protocol identifier
            String protocolHeader = dataInputStream.readUTF();

            // === UPDATED: Ultra-Speed Protocol Detection ===
            boolean isUltraSpeed = "SHAREIT_ULTRA_START".equals(protocolHeader);

            if (isUltraSpeed) {
                handleUltraSpeedTransfer(clientSocket, dataInputStream, dataOutputStream, clientAddress);
            } else if ("SHAREIT_TEST".equals(protocolHeader) || "SHAREIT_ULTRA_TEST".equals(protocolHeader)) {
                // Handle connection test
                handleConnectionTest(protocolHeader, dataOutputStream);
            } else {
                // Standard transfer (backward compatibility)
                handleStandardTransfer(clientSocket, dataInputStream, protocolHeader, clientAddress);
            }

        } catch (IOException e) {
            log("💥 SERVER ERROR: Connection handling failed from " + clientAddress + " - " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // Ignore close errors
            }
        }
    }

    // === NEW: Ultra-Speed Transfer Handler ===
    private void handleUltraSpeedTransfer(Socket clientSocket, DataInputStream dataInputStream,
                                          DataOutputStream dataOutputStream, String clientAddress) throws IOException {
        // Acknowledge ultra-speed protocol
        dataOutputStream.writeUTF("SHAREIT_ULTRA_READY");
        dataOutputStream.flush();

        // Read ultra-speed metadata
        String fileName = dataInputStream.readUTF();
        long fileSize = dataInputStream.readLong();
        int clientBufferSize = dataInputStream.readInt();
        String ultraProtocol = dataInputStream.readUTF();

        if (!"SHAREIT_ULTRA_v3".equals(ultraProtocol)) {
            log("⚠️ SERVER: Incompatible ultra-speed protocol from " + clientAddress);
            return;
        }

        log("⚡ SERVER: ULTRA-SPEED transfer initiated: " + fileName +
                " (" + formatFileSize(fileSize) + ") from " + clientAddress);
        log("📊 SERVER: Client buffer size: " + (clientBufferSize/1024) + "KB");

        // Dynamically adjust buffer if client has different size
        int transferBufferSize = Math.max(bufferSize, clientBufferSize);
        log("⚙️ SERVER: Using " + (transferBufferSize/1024) + "KB buffer for optimal performance");

        // Create received files directory with ultra-speed tag
        File receivedDir = new File("ultra_received_files");
        if (!receivedDir.exists()) {
            receivedDir.mkdirs();
        }

        // Handle duplicate file names
        File outputFile = getUniqueFile(receivedDir, fileName);

        // Notify controller about incoming ultra-speed file
        Platform.runLater(() -> {
            if (controller != null) {
                controller.log("⚡ ULTRA INCOMING: " + fileName +
                        " (" + formatFileSize(fileSize) + ") from " + clientAddress);
            }
        });

        // Receive file with ultra-speed optimization
        receiveFileUltraSpeed(dataInputStream, outputFile, fileSize, fileName, clientAddress, transferBufferSize);

        // Wait for completion signal
        String completionSignal = dataInputStream.readUTF();
        if ("SHAREIT_ULTRA_COMPLETE".equals(completionSignal)) {
            log("🎉 SERVER: Ultra-speed transfer COMPLETED: " + fileName);
        }
    }

    // === NEW: Standard Transfer Handler ===
    private void handleStandardTransfer(Socket clientSocket, DataInputStream dataInputStream,
                                        String fileName, String clientAddress) throws IOException {
        // First string is actually the filename for standard protocol
        long fileSize = dataInputStream.readLong();
        String protocol = dataInputStream.readUTF();

        if (!"SHAREIT_PREMIUM_v3".equals(protocol)) {
            log("⚠️ SERVER: Incompatible protocol from " + clientAddress);
            return;
        }

        log("📥 SERVER: Receiving file: " + fileName + " (" + formatFileSize(fileSize) + ") from " + clientAddress);

        // Create received files directory
        File receivedDir = new File("received_files");
        if (!receivedDir.exists()) {
            receivedDir.mkdirs();
        }

        // Handle duplicate file names
        File outputFile = getUniqueFile(receivedDir, fileName);

        // Notify controller about incoming file
        Platform.runLater(() -> {
            if (controller != null) {
                controller.log("📥 INCOMING: " + fileName + " (" + formatFileSize(fileSize) + ") from " + clientAddress);
            }
        });

        // Receive file data
        receiveFile(dataInputStream, outputFile, fileSize, fileName, clientAddress, bufferSize);
    }

    // === NEW: Connection Test Handler ===
    private void handleConnectionTest(String protocolHeader, DataOutputStream dataOutputStream) throws IOException {
        boolean isUltraTest = "SHAREIT_ULTRA_TEST".equals(protocolHeader);

        if (isUltraTest) {
            dataOutputStream.writeUTF("SHAREIT_ULTRA_READY");
            log("✅ SERVER: Ultra-speed connection test passed");
        } else {
            dataOutputStream.writeUTF("SHAREIT_READY");
            log("✅ SERVER: Standard connection test passed");
        }
        dataOutputStream.flush();
    }

    // === UPDATED: Standard File Reception ===
    private void receiveFile(DataInputStream dataInputStream, File outputFile, long fileSize,
                             String fileName, String clientAddress, int receiveBufferSize) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOutputStream, receiveBufferSize)) {

            byte[] buffer = new byte[receiveBufferSize];
            int bytesRead;
            long totalBytesReceived = 0;
            long startTime = System.currentTimeMillis();
            long lastUpdateTime = System.currentTimeMillis();
            long lastBytesReceived = 0;
            long lastSpeedTime = startTime;
            long lastSpeedBytes = 0;
            double maxSpeed = 0.0;

            while (totalBytesReceived < fileSize && (bytesRead = dataInputStream.read(buffer)) != -1) {
                bufferedOut.write(buffer, 0, bytesRead);
                totalBytesReceived += bytesRead;

                // Update progress every 250ms for smoother display
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastUpdateTime > 250) {
                    double progress = (double) totalBytesReceived / fileSize;
                    double speed = calculateSpeed(totalBytesReceived, lastBytesReceived, lastUpdateTime, currentTime);
                    maxSpeed = Math.max(maxSpeed, speed);

                    updateFileProgress(fileName, progress, speed, totalBytesReceived, fileSize);

                    lastUpdateTime = currentTime;
                    lastBytesReceived = totalBytesReceived;
                }
            }

            // File transfer complete
            long transferTime = (System.currentTimeMillis() - startTime) / 1000;
            double averageSpeed = fileSize / (1024.0 * 1024.0) / (transferTime > 0 ? transferTime : 1);

            log("✅ SERVER: File received successfully: " + fileName +
                    " (" + formatFileSize(fileSize) + ") in " + transferTime + "s (" +
                    String.format("%.2f", averageSpeed) + " MB/s)");

            // Log performance info
            if (averageSpeed > 10) {
                log("🚀 SERVER: HIGH SPEED transfer achieved!");
            }

            // Update UI
            Platform.runLater(() -> {
                if (controller != null) {
                    controller.addReceivedFile(outputFile, fileSize);
                }
            });

        } catch (IOException e) {
            log("💥 SERVER ERROR: Failed to save file " + fileName + " - " + e.getMessage());
        }
    }

    // === NEW: Ultra-Speed File Reception ===
    private void receiveFileUltraSpeed(DataInputStream dataInputStream, File outputFile, long fileSize,
                                       String fileName, String clientAddress, int ultraBufferSize) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             BufferedOutputStream bufferedOut = new BufferedOutputStream(fileOutputStream, ultraBufferSize * 2)) {

            byte[] buffer = new byte[ultraBufferSize];
            int bytesRead;
            long totalBytesReceived = 0;
            long startTime = System.currentTimeMillis();
            long lastUpdateTime = System.currentTimeMillis();
            long lastBytesReceived = 0;
            long lastSpeedTime = startTime;
            long lastSpeedBytes = 0;
            double maxSpeed = 0.0;
            double totalSpeed = 0.0;
            int speedSamples = 0;

            while (totalBytesReceived < fileSize && (bytesRead = dataInputStream.read(buffer)) != -1) {
                bufferedOut.write(buffer, 0, bytesRead);
                totalBytesReceived += bytesRead;

                // Ultra-fast progress updates every 100ms
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastUpdateTime > 100) {
                    double progress = (double) totalBytesReceived / fileSize;
                    double currentSpeed = calculateSpeed(totalBytesReceived, lastBytesReceived, lastUpdateTime, currentTime);

                    maxSpeed = Math.max(maxSpeed, currentSpeed);
                    totalSpeed += currentSpeed;
                    speedSamples++;

                    updateFileProgress(fileName, progress, currentSpeed, totalBytesReceived, fileSize);

                    // Log ultra-speed milestones
                    if (currentSpeed > 50) { // Over 50 MB/s
                        log("⚡ SERVER: ULTRA-SPEED: " + String.format("%.1f", currentSpeed) + " MB/s for " + fileName);
                    }

                    lastUpdateTime = currentTime;
                    lastBytesReceived = totalBytesReceived;
                }
            }

            // Ultra-speed transfer complete
            long transferTime = (System.currentTimeMillis() - startTime) / 1000;
            double averageSpeed = fileSize / (1024.0 * 1024.0) / (transferTime > 0 ? transferTime : 1);
            double calculatedAverageSpeed = speedSamples > 0 ? totalSpeed / speedSamples : 0;

            log("🎉 SERVER: ULTRA-SPEED transfer COMPLETE: " + fileName);
            log("📊 SERVER: File size: " + formatFileSize(fileSize));
            log("⏱️  SERVER: Total time: " + transferTime + " seconds");
            log("⚡ SERVER: Average speed: " + String.format("%.2f", averageSpeed) + " MB/s");
            log("🚀 SERVER: Peak speed: " + String.format("%.2f", maxSpeed) + " MB/s");
            log("📈 SERVER: Sampled average: " + String.format("%.2f", calculatedAverageSpeed) + " MB/s");

            // Performance rating
            if (averageSpeed > 50) {
                log("🏆 SERVER: ULTRA-HIGH SPEED TRANSFER ACHIEVED!");
            } else if (averageSpeed > 10) {
                log("⭐ SERVER: EXCELLENT SPEED PERFORMANCE");
            }

            // Update UI
            Platform.runLater(() -> {
                if (controller != null) {
                    controller.addReceivedFile(outputFile, fileSize);
                }
            });

        } catch (IOException e) {
            log("💥 SERVER ERROR: Failed to save ultra-speed file " + fileName + " - " + e.getMessage());
        }
    }

    private File getUniqueFile(File directory, String fileName) {
        File file = new File(directory, fileName);
        if (!file.exists()) {
            return file;
        }

        // Handle duplicates
        int dotIndex = fileName.lastIndexOf('.');
        String nameWithoutExt, extension;

        if (dotIndex > 0) {
            nameWithoutExt = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        } else {
            nameWithoutExt = fileName;
            extension = "";
        }

        int counter = 1;

        while (true) {
            String newFileName = nameWithoutExt + "_" + counter + extension;
            File newFile = new File(directory, newFileName);
            if (!newFile.exists()) {
                return newFile;
            }
            counter++;
        }
    }

    private double calculateSpeed(long totalBytes, long lastBytes, long lastTime, long currentTime) {
        long bytesDiff = totalBytes - lastBytes;
        long timeDiff = currentTime - lastTime;
        return timeDiff > 0 ? (bytesDiff / (1024.0 * 1024.0)) / (timeDiff / 1000.0) : 0;
    }

    private void updateFileProgress(String fileName, double progress, double speed, long bytesReceived, long totalSize) {
        Platform.runLater(() -> {
            if (controller != null) {
                controller.updateFileProgress(fileName, progress, speed, bytesReceived, totalSize);
            }
        });
    }

    public void stopServer() {
        isRunning = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log("💥 SERVER ERROR: Failed to stop server properly - " + e.getMessage());
        }

        clientThreadPool.shutdownNow(); // Force shutdown for ultra-speed
        log("🔴 SERVER: Stopped - " + activeConnections.get() + " active connections terminated");

        Platform.runLater(() -> {
            if (controller != null) {
                controller.updateServerStatus("🔴 SERVER STOPPED");
            }
        });
    }

    public boolean isRunning() {
        return isRunning;
    }

    // === NEW: Get current buffer size ===
    public int getBufferSize() {
        return bufferSize;
    }

    // === NEW: Set buffer size dynamically ===
    public void setBufferSize(int newBufferSize) {
        this.bufferSize = newBufferSize;
        log("⚡ SERVER: Buffer size updated to " + (newBufferSize/1024) + "KB");
    }

    // === NEW: Get active connections count ===
    public int getActiveConnections() {
        return activeConnections.get();
    }

    private void log(String message) {
        Platform.runLater(() -> {
            if (controller != null) {
                controller.log(message);
            }
        });
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        else if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        else return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
}