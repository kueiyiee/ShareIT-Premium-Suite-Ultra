package com.shareit.shareit;

import javafx.application.Platform;
import java.io.*;
import java.net.*;

public class FileTransferClient {
    private FileTransferController controller;
    private String serverHost;
    private int serverPort;
    private int bufferSize = 8192 * 8; // Default 64KB buffer for ultra-speed

    // Constructor with default buffer
    public FileTransferClient(FileTransferController controller) {
        this.controller = controller;
    }

    // Constructor with custom buffer size
    public FileTransferClient(FileTransferController controller, int bufferSize) {
        this.controller = controller;
        this.bufferSize = bufferSize;
        log("⚡ CLIENT: Ultra-speed client initialized with " + (bufferSize/1024) + "KB buffer");
    }

    public boolean sendFile(File file, String host, int port) {
        this.serverHost = host;
        this.serverPort = port;

        log("🚀 CLIENT: Starting standard transfer for " + file.getName());

        // Create a task for file transfer
        FileSendTask sendTask = new FileSendTask(file, host, port, bufferSize, false);

        // Execute in background thread
        Thread sendThread = new Thread(sendTask);
        sendThread.setDaemon(true);
        sendThread.start();

        return true;
    }

    // === NEW: Ultra-Speed File Transfer ===
    public boolean sendFileUltraSpeed(File file, String host, int port, int ultraBufferSize) {
        this.serverHost = host;
        this.serverPort = port;

        log("⚡ CLIENT: Starting ULTRA-SPEED transfer for " + file.getName());
        log("⚡ CLIENT: Using " + (ultraBufferSize/1024) + "KB buffer for maximum performance");

        // Create ultra-speed task
        FileSendTask sendTask = new FileSendTask(file, host, port, ultraBufferSize, true);

        // Execute in background thread with high priority
        Thread sendThread = new Thread(sendTask);
        sendThread.setDaemon(true);
        sendThread.setPriority(Thread.MAX_PRIORITY);
        sendThread.start();

        return true;
    }

    // === NEW: Enhanced Connection Test ===
    public boolean testUltraSpeedConnection(String host, int port) {
        log("🔌 CLIENT: Testing ultra-speed connection to " + host + ":" + port);

        try (Socket socket = new Socket()) {
            // Set socket options for better performance
            socket.setTcpNoDelay(true); // Disable Nagle's algorithm for low latency
            socket.setSoTimeout(5000); // 5 second timeout
            socket.setSendBufferSize(bufferSize); // Set send buffer size

            // Connect with timeout
            socket.connect(new InetSocketAddress(host, port), 3000);

            // Test with small data packet
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            // Send test signal
            out.write("SHAREIT_ULTRA_TEST".getBytes());
            out.flush();

            // Read response
            byte[] response = new byte[18];
            int bytesRead = in.read(response);

            String responseStr = new String(response, 0, bytesRead);
            boolean success = "SHAREIT_ULTRA_READY".equals(responseStr);

            if (success) {
                log("✅ CLIENT: Ultra-speed connection test PASSED");
                log("⚡ CLIENT: Socket optimized for high-speed transfers");
                return true;
            } else {
                log("⚠️ CLIENT: Connection test response unexpected");
                return false;
            }

        } catch (SocketTimeoutException e) {
            log("⏰ CLIENT: Connection timeout - Server might be busy");
            return false;
        } catch (ConnectException e) {
            log("❌ CLIENT: Cannot connect to server - Check IP/port and firewall");
            return false;
        } catch (IOException e) {
            log("⚠️ CLIENT: Connection test failed - " + e.getMessage());
            return false;
        }
    }

    public boolean testConnection(String host, int port) {
        log("🔌 CLIENT: Testing standard connection to " + host + ":" + port);

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 3000); // 3 second timeout

            // Simple test with protocol handshake
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write("SHAREIT_TEST".getBytes());
            out.flush();

            byte[] response = new byte[12];
            int bytesRead = in.read(response);

            String responseStr = new String(response, 0, bytesRead);
            boolean success = "SHAREIT_READY".equals(responseStr);

            log(success ? "✅ CLIENT: Connection test PASSED" : "⚠️ CLIENT: Connection test response unexpected");
            return success;

        } catch (SocketTimeoutException e) {
            log("⏰ CLIENT: Connection timeout");
            return false;
        } catch (ConnectException e) {
            log("❌ CLIENT: Cannot connect to server");
            return false;
        } catch (IOException e) {
            log("⚠️ CLIENT: Connection test failed - " + e.getMessage());
            return false;
        }
    }

    // === NEW: Get Current Buffer Size ===
    public int getCurrentBufferSize() {
        return bufferSize;
    }

    // === NEW: Set Buffer Size ===
    public void setBufferSize(int newBufferSize) {
        this.bufferSize = newBufferSize;
        log("⚡ CLIENT: Buffer size updated to " + (newBufferSize/1024) + "KB");
    }

    private class FileSendTask implements Runnable {
        private final File file;
        private final String host;
        private final int port;
        private final int taskBufferSize;
        private final boolean isUltraSpeed;

        public FileSendTask(File file, String host, int port, int bufferSize, boolean isUltraSpeed) {
            this.file = file;
            this.host = host;
            this.port = port;
            this.taskBufferSize = bufferSize;
            this.isUltraSpeed = isUltraSpeed;
        }

        @Override
        public void run() {
            if (isUltraSpeed) {
                sendFileUltraSpeedToServer();
            } else {
                sendFileToServer();
            }
        }

        // === ORIGINAL: Standard File Transfer ===
        private void sendFileToServer() {
            String fileName = file.getName();
            long fileSize = file.length();

            log("📤 CLIENT: Sending " + fileName + " (" + formatFileSize(fileSize) + ") to " + host + ":" + port);

            try (Socket socket = new Socket(host, port);
                 FileInputStream fileInputStream = new FileInputStream(file);
                 BufferedInputStream bufferedIn = new BufferedInputStream(fileInputStream);
                 OutputStream outputStream = socket.getOutputStream();
                 DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {

                // Set socket options for better performance
                socket.setTcpNoDelay(true); // Disable Nagle's algorithm
                socket.setSoTimeout(30000); // 30 second timeout
                socket.setSendBufferSize(taskBufferSize); // Use specified buffer size

                // Send file metadata
                dataOutputStream.writeUTF(fileName);
                dataOutputStream.writeLong(fileSize);
                dataOutputStream.writeUTF("SHAREIT_PREMIUM_v3"); // Protocol identifier

                // Send file data
                byte[] buffer = new byte[taskBufferSize];
                int bytesRead;
                long totalBytesSent = 0;
                long startTime = System.currentTimeMillis();
                long lastUpdateTime = System.currentTimeMillis();
                long lastBytesSent = 0;

                while ((bytesRead = bufferedIn.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                    totalBytesSent += bytesRead;

                    // Update progress every 250ms for smoother display
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastUpdateTime > 250) {
                        double progress = (double) totalBytesSent / fileSize;
                        // Calculate speed in bytes per second
                        long bytesDiff = totalBytesSent - lastBytesSent;
                        long timeDiff = currentTime - lastUpdateTime;
                        double bytesPerSecond = timeDiff > 0 ? (bytesDiff * 1000.0) / timeDiff : 0;

                        // FIXED: Call the correct method signature with all 5 parameters
                        updateFileProgress(fileName, progress, bytesPerSecond, totalBytesSent, fileSize);

                        lastUpdateTime = currentTime;
                        lastBytesSent = totalBytesSent;
                    }

                    // Small delay to prevent overwhelming the network
                    if (bytesRead == buffer.length) {
                        Thread.yield();
                    }
                }

                // Ensure all data is sent
                dataOutputStream.flush();

                // Transfer complete
                long transferTime = (System.currentTimeMillis() - startTime) / 1000;
                double averageSpeed = fileSize / (1024.0 * 1024.0) / (transferTime > 0 ? transferTime : 1);

                log("✅ CLIENT: File sent successfully: " + fileName +
                        " (" + formatFileSize(fileSize) + ") in " + transferTime + "s (" +
                        String.format("%.2f", averageSpeed) + " MB/s)");

                // Update UI
                Platform.runLater(() -> {
                    if (controller != null) {
                        controller.markFileAsCompleted(fileName);
                    }
                });

            } catch (ConnectException e) {
                log("💥 CLIENT ERROR: Cannot connect to server " + host + ":" + port + " - " + e.getMessage());
                Platform.runLater(() -> {
                    if (controller != null) {
                        controller.updateFileStatus(fileName, "Connection failed");
                    }
                });
            } catch (IOException e) {
                log("💥 CLIENT ERROR: Failed to send file " + fileName + " - " + e.getMessage());
                Platform.runLater(() -> {
                    if (controller != null) {
                        controller.updateFileStatus(fileName, "Transfer failed");
                    }
                });
            } catch (Exception e) {
                log("💥 CLIENT ERROR: Unexpected error while sending " + fileName + " - " + e.getMessage());
                Platform.runLater(() -> {
                    if (controller != null) {
                        controller.updateFileStatus(fileName, "Unexpected error");
                    }
                });
            }
        }

        // === NEW: Ultra-Speed File Transfer ===
        private void sendFileUltraSpeedToServer() {
            String fileName = file.getName();
            long fileSize = file.length();

            log("⚡ CLIENT: ULTRA-SPEED transfer: " + fileName + " (" + formatFileSize(fileSize) + ") to " + host + ":" + port);

            try (Socket socket = new Socket(host, port);
                 FileInputStream fileInputStream = new FileInputStream(file);
                 BufferedInputStream bufferedIn = new BufferedInputStream(fileInputStream, taskBufferSize);
                 OutputStream outputStream = socket.getOutputStream();
                 DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {

                // Optimize socket for ultra-speed transfers
                socket.setTcpNoDelay(true); // Critical for high-speed transfers
                socket.setSoTimeout(60000); // 60 second timeout for large files
                socket.setSendBufferSize(taskBufferSize * 2); // Double buffer for better throughput
                socket.setPerformancePreferences(0, 2, 1); // Prioritize bandwidth over latency

                // Send file metadata with ultra-speed protocol
                dataOutputStream.writeUTF("SHAREIT_ULTRA_START");
                dataOutputStream.writeUTF(fileName);
                dataOutputStream.writeLong(fileSize);
                dataOutputStream.writeInt(taskBufferSize); // Send buffer size info
                dataOutputStream.writeUTF("SHAREIT_ULTRA_v3"); // Ultra-speed protocol identifier

                // Send file data with ultra-speed optimization
                byte[] buffer = new byte[taskBufferSize];
                int bytesRead;
                long totalBytesSent = 0;
                long startTime = System.currentTimeMillis();
                long lastUpdateTime = System.currentTimeMillis();
                long lastBytesSent = 0;
                long lastSpeedTime = startTime;
                long lastSpeedBytes = 0;
                double maxSpeed = 0.0;

                while ((bytesRead = bufferedIn.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                    totalBytesSent += bytesRead;

                    // Ultra-fast progress updates every 100ms
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastUpdateTime > 100) {
                        double progress = (double) totalBytesSent / fileSize;

                        // Calculate instantaneous speed in bytes per second
                        long bytesDiff = totalBytesSent - lastBytesSent;
                        long timeDiff = currentTime - lastUpdateTime;
                        double bytesPerSecond = timeDiff > 0 ? (bytesDiff * 1000.0) / timeDiff : 0;

                        // Convert to MB/s for logging
                        double currentSpeedMBps = bytesPerSecond / (1024.0 * 1024.0);
                        maxSpeed = Math.max(maxSpeed, currentSpeedMBps);

                        // FIXED: Call the correct method signature with all 5 parameters
                        updateFileProgress(fileName, progress, bytesPerSecond, totalBytesSent, fileSize);

                        // Log ultra-speed milestones
                        if (currentSpeedMBps > 50) { // Over 50 MB/s
                            log("⚡ CLIENT: ULTRA-SPEED ACHIEVED: " + String.format("%.1f", currentSpeedMBps) + " MB/s for " + fileName);
                        } else if (currentSpeedMBps > 10) { // Over 10 MB/s
                            log("🚀 CLIENT: HIGH SPEED: " + String.format("%.1f", currentSpeedMBps) + " MB/s for " + fileName);
                        }

                        lastUpdateTime = currentTime;
                        lastBytesSent = totalBytesSent;
                    }

                    // No artificial delays for ultra-speed mode
                    // Let the TCP congestion control handle it
                }

                // Send completion signal
                dataOutputStream.writeUTF("SHAREIT_ULTRA_COMPLETE");
                dataOutputStream.flush();

                // Transfer complete - ultra-speed performance report
                long transferTime = (System.currentTimeMillis() - startTime) / 1000;
                double finalAverageSpeed = fileSize / (1024.0 * 1024.0) / (transferTime > 0 ? transferTime : 1);

                log("🎉 CLIENT: ULTRA-SPEED transfer COMPLETE: " + fileName);
                log("📊 CLIENT: File size: " + formatFileSize(fileSize));
                log("⏱️  CLIENT: Total time: " + transferTime + " seconds");
                log("⚡ CLIENT: Average speed: " + String.format("%.2f", finalAverageSpeed) + " MB/s");
                log("🚀 CLIENT: Peak speed: " + String.format("%.2f", maxSpeed) + " MB/s");

                // Performance rating
                if (finalAverageSpeed > 50) {
                    log("🏆 CLIENT: PERFORMANCE: ULTRA-HIGH SPEED ACHIEVED!");
                } else if (finalAverageSpeed > 10) {
                    log("⭐ CLIENT: PERFORMANCE: EXCELLENT SPEED");
                } else if (finalAverageSpeed > 1) {
                    log("👍 CLIENT: PERFORMANCE: GOOD SPEED");
                }

                // Update UI
                Platform.runLater(() -> {
                    if (controller != null) {
                        controller.markFileAsCompleted(fileName);
                    }
                });

            } catch (ConnectException e) {
                log("💥 CLIENT ERROR: Cannot connect to server " + host + ":" + port + " - " + e.getMessage());
                Platform.runLater(() -> {
                    if (controller != null) {
                        controller.updateFileStatus(fileName, "Connection failed");
                    }
                });
            } catch (SocketTimeoutException e) {
                log("⏰ CLIENT ERROR: Transfer timeout for " + fileName + " - Server may be busy");
                Platform.runLater(() -> {
                    if (controller != null) {
                        controller.updateFileStatus(fileName, "Transfer timeout");
                    }
                });
            } catch (IOException e) {
                log("💥 CLIENT ERROR: Failed to send file " + fileName + " - " + e.getMessage());
                Platform.runLater(() -> {
                    if (controller != null) {
                        controller.updateFileStatus(fileName, "Transfer failed");
                    }
                });
            } catch (Exception e) {
                log("💥 CLIENT ERROR: Unexpected error while sending " + fileName + " - " + e.getMessage());
                Platform.runLater(() -> {
                    if (controller != null) {
                        controller.updateFileStatus(fileName, "Unexpected error");
                    }
                });
            }
        }

        // FIXED: Update File Progress method - Now matches the controller's signature
        private void updateFileProgress(String fileName, double progress, double bytesPerSecond, long bytesSent, long totalSize) {
            Platform.runLater(() -> {
                if (controller != null) {
                    // The controller now expects: fileName, progress, transferRate, bytesTransferred, fileSize
                    // We pass bytesPerSecond as the transferRate parameter
                    controller.updateFileProgress(fileName, progress, bytesPerSecond, bytesSent, totalSize);
                }
            });
        }
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