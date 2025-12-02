# ShareIT Premium Suite Ultra 🚀

**Ultra-High Speed Multi-Threaded Secure File Transfer System**

ShareIT Premium Suite Ultra is a professional-grade file transfer application built with JavaFX that delivers blazing-fast cross-network file transfers with advanced analytics, real-time monitoring, and intelligent optimization features.

## ✨ Key Features

- **⚡ Ultra-High Speed Transfers**: Multi-threaded architecture for maximum throughput
- **🌐 Cross-Network Capability**: Works seamlessly across local and public networks
- **📊 Real-Time Analytics**: Live speed monitoring, progress tracking, and performance metrics
- **🎨 Customizable UI**: Multiple themes (Dark, Light, Blue, Purple) and font scaling
- **🔄 Parallel Transfers**: Concurrent file transfers with intelligent queuing
- **🔧 Network Optimization**: Automatic buffer sizing and connection optimization
- **📅 Dual Calendar Support**: Displays both Gregorian and Ethiopian dates
- **💾 Log Management**: Comprehensive logging with export capabilities

## 📋 Prerequisites

- **Java 21+** (Recommended: Java 21.0.9 or higher)
- **JavaFX SDK** (Included in project structure)
- **Minimum 4GB RAM** (8GB recommended for large transfers)
- **Network connectivity** (Wi-Fi/Ethernet for cross-network transfers)

## 🚀 Installation & Setup

### Step 1: Clone the Repository
```bash
git clone https://github.com/kueiyiee/ShareIT-Premium-Suite-Ultra.git
cd ShareIT-Premium-Suite-Ultra
```

### Step 2: Import into IDE
1. Open IntelliJ IDEA or Eclipse
2. Import as Maven/Gradle project (depending on your setup)
3. Ensure JavaFX SDK is properly configured
4. Set Java 21 as project SDK

### Step 3: Configure JavaFX
For IntelliJ IDEA:
- Go to `Run` → `Edit Configurations`
- Add VM Options:
```
--module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
```

## 🎯 How to Use - Step by Step Guide

### **Step 1: Launch the Application**
- Run `Main.java` or execute the packaged JAR file
- Application starts with Dark Theme (default)

### **Step 2: Choose Operation Mode**
#### **🔹 SENDER MODE** (To Send Files):
1. Select **"Sender Mode"** radio button
2. Enter receiver's IP address in the IP field
3. Click **"Get My IP"** if you need to check your own IP

#### **🔹 RECEIVER MODE** (To Receive Files):
1. Select **"Receiver Mode"** radio button
2. Click **"Get My IP"** to display your IP address
3. Share this IP with the sender
4. Click **"Start Listening"** to activate receiver

### **Step 3: Select Files for Transfer**
#### For Senders:
1. Click **"Select Files"** to choose individual files
   - Supports all file types
   - Multiple selection enabled
2. Click **"Select Folder"** to transfer entire directories
3. View selected files in the transfer queue table
4. Click **"Clear Files"** to reset selection if needed

### **Step 4: Initiate Transfer**
#### For Senders:
1. Ensure receiver IP is entered correctly
2. Click **"🚀 LAUNCH TRANSFER"** to begin
3. Monitor real-time progress in the analytics panel

#### For Receivers:
1. Ensure **"Start Listening"** is active (button disabled when listening)
2. Wait for incoming connection
3. Files auto-save to `Downloads/ShareIT_Received/`

### **Step 5: Monitor Transfer Progress**
#### Real-Time Analytics Display:
- **📈 Transfer Speed**: Live speed in KB/s, MB/s, or GB/s
- **⏱️ Time Elapsed**: Total transfer duration
- **💾 Data Transferred**: Cumulative data transferred
- **🔄 Active Transfers**: Number of concurrent transfers
- **✅ Completed Files**: Files successfully transferred
- **⏳ Remaining Time**: Estimated time to completion

#### File-Level Tracking:
- Individual file progress bars
- Per-file transfer speeds
- Status updates (Queued, Transferring, Completed, Failed)

### **Step 6: Transfer Controls**
- **⏸️ PAUSE ALL**: Temporarily pause all transfers
- **▶️ RESUME ALL**: Resume paused transfers
- **❌ CANCEL ALL**: Abort all active transfers
- Note: Pause/Resume works on all transfers simultaneously

### **Step 7: Post-Transfer Actions**
1. **Review Logs**: Check transfer details in log panel
2. **Save Logs**: Click floppy disk icon to export logs
3. **Clear Queue**: Remove completed transfers from list
4. **New Session**: Start fresh transfer from menu

## 🎨 UI Customization

### **Themes** (Access via Menu Button → Appearance):
- **🌙 Dark Theme**: Default professional look
- **☀️ Light Theme**: Bright interface
- **🔵 Blue Theme**: Calming blue tones
- **🟣 Purple Theme**: Vibrant purple scheme

### **Font Sizing** (Access via Menu Button → Appearance):
- **Small**: Compact text
- **Medium**: Default size
- **Large**: Enhanced readability
- **Extra Large**: Maximum visibility

## ⚙️ Network Configuration

### **Optimization Settings** (Menu Button → Network Settings):
- **⚡ Local Network**: Optimized for LAN (128KB buffer, 15 concurrent)
- **🌍 Cross-Network**: Optimized for WAN (32KB buffer, 5 concurrent)
- **🔧 Port Configuration**: Customize server port (default: 12345)

### **Network Requirements:**
- **Port 12345** must be open on receiver's firewall
- **Static IP** recommended for reliable connections
- **High-speed network** for optimal performance

## 📊 Understanding the Interface

### **Main Dashboard Sections:**
1. **Header**: Mode selection and IP configuration
2. **File Management**: File selection and queue controls
3. **Transfer Analytics**: Real-time performance metrics
4. **File Table**: Individual file transfer status
5. **Log Panel**: System and transfer logs
6. **Footer**: Copyright and status information

### **Status Indicators:**
- **🟢 READY**: Application ready for operation
- **🔵 LISTENING**: Receiver waiting for connection
- **⚡ TRANSFERRING**: Active file transfer in progress
- **🟡 PAUSED**: Transfers temporarily paused
- **🎉 COMPLETED**: All transfers finished successfully
- **❌ ERROR**: Transfer failed or aborted

## 🔧 Advanced Features

### **Developer Tools** (Menu Button → Developer Profile):
- Developer information and contact details
- System information display
- Date/time in both Gregorian and Ethiopian calendars

### **Log Management:**
- **Auto-timestamping**: All logs include precise timestamps
- **Export Function**: Save logs as text files
- **Search Capability**: Filter through transfer history
- **Clear Option**: Reset log display

### **Performance Optimization:**
- **Adaptive Buffering**: Auto-adjusts based on network type
- **Connection Pooling**: Efficient resource management
- **Error Recovery**: Automatic retry on network interruptions
- **Memory Management**: Optimized for large file transfers

## 🛠️ Troubleshooting

### **Common Issues & Solutions:**

#### **❌ Connection Failed:**
1. Verify receiver IP address is correct
2. Ensure receiver is in "Start Listening" mode
3. Check firewall allows port 12345
4. Confirm both devices on same network (for local transfers)

#### **⚠️ Slow Transfer Speeds:**
1. Switch to appropriate network optimization mode
2. Reduce concurrent transfers in settings
3. Check network bandwidth
4. Close other bandwidth-intensive applications

#### **🔴 Application Freezes:**
1. Reduce number of concurrent transfers
2. Increase JVM heap size if transferring large files
3. Check available system memory
4. Restart application

#### **📁 File Save Issues (Receiver):**
1. Verify write permissions in download directory
2. Check available disk space
3. Ensure antivirus isn't blocking file operations
4. Manual save location: `User/Downloads/ShareIT_Received/`

### **Network Diagnostics:**
1. Use built-in network test feature
2. Verify port accessibility with network tools
3. Check router settings for port forwarding (WAN transfers)
4. Test with small file first before large transfers

## 📈 Performance Tips

### **For Maximum Speed:**
1. **Use Ethernet** instead of Wi-Fi when possible
2. **Close background apps** consuming bandwidth
3. **Select appropriate** network optimization mode
4. **Transfer during off-peak** network hours
5. **Use SSD storage** on both ends if available

### **For Large Transfers:**
1. **Break into batches** if having stability issues
2. **Monitor system resources** during transfer
3. **Use pause/resume** for network interruptions
4. **Verify file integrity** after transfer completion

## 🔒 Security Notes

### **Transfer Security:**
- **Local Network**: Transfers stay within your private network
- **Public IP**: Only share with trusted parties for WAN transfers
- **No Encryption**: Currently transfers are unencrypted (planned feature)
- **Temporary Files**: Cleared after transfer completion

### **Privacy Considerations:**
- No data collection or telemetry
- All logs stay locally on your machine
- No cloud storage or external servers
- Complete user control over all data

## 📱 System Requirements

### **Minimum:**
- **OS**: Windows 10, macOS 10.14+, Linux with GUI
- **CPU**: Dual-core processor
- **RAM**: 4GB minimum
- **Storage**: 500MB free space
- **Network**: 100Mbps Ethernet or Wi-Fi 5

### **Recommended:**
- **OS**: Latest stable version
- **CPU**: Quad-core processor or better
- **RAM**: 8GB or more
- **Storage**: SSD with 1GB+ free space
- **Network**: Gigabit Ethernet or Wi-Fi 6

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Developer

**Kuei Poch Kuei**
- 🎓 3rd Year Computer Science, Dilla University
- 📧 Email: kueiyiee@gmail.com
- 📱 Phone: +251 937 910 246
- 💼 GitHub: [github.com/kueiyiee](https://github.com/kueiyiee)
- 💡 Motto: *"My Logic is your Limit"*

## 🆘 Support

For issues, questions, or feature requests:
1. Check the [Troubleshooting](#-troubleshooting) section
2. Review closed GitHub issues
3. Contact developer via email
4. Include system details and error logs when reporting issues

---

**⭐ If you find this project useful, please give it a star on GitHub!**

**🚀 Happy Ultra-Fast File Transferring!**
