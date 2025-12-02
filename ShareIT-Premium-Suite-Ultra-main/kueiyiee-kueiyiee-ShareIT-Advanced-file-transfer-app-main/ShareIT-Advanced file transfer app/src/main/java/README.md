# 🚀 ShareIT Premium Suite

<div align="center">
  
  ![Java](https://img.shields.io/badge/Java-17%2B-orange)
  ![JavaFX](https://img.shields.io/badge/JavaFX-17-blue)
  ![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20Linux%20%7C%20macOS-lightgrey)
  ![License](https://img.shields.io/badge/License-MIT-green)
  ![Version](https://img.shields.io/badge/Version-3.0_Premium-purple)

  **Advanced Multi-Threaded Secure File Transfer System**
  
  *"My Logic is your Limit" - Kuei Poch Kuei*

</div>

## 📋 Table of Contents
- [🌟 Features](#-features)
- [🚀 Quick Start](#-quick-start)
- [🎯 Operation Modes](#-operation-modes)
- [🛠️ Installation](#️-installation)
- [📊 Architecture](#-architecture)
- [🎨 UI Features](#-ui-features)
- [🔧 Technical Details](#-technical-details)
- [📁 Project Structure](#-project-structure)
- [🤝 Contributing](#-contributing)
- [👨‍💻 Developer](#-developer)
- [📄 License](#-license)

## 🌟 Features

### 🔥 Core Capabilities
- **⚡ Concurrent File Transfers** - Transfer multiple files simultaneously
- **🔒 Secure Protocol** - Built-in encryption for data security
- **📊 Real-Time Analytics** - Live progress monitoring and statistics
- **🌐 Multi-Platform** - Works across Windows, Linux, and macOS
- **🔄 Dual Calendar Support** - Displays both Gregorian and Ethiopian dates

### 🎨 Premium UI Features
- **🌈 Multiple Themes** - Dark, Light, Blue, and Purple themes
- **🔤 Dynamic Font Scaling** - Adjustable text sizes
- **📱 Responsive Layout** - Adapts to different screen sizes
- **🎯 Professional Dashboard** - Three-panel layout for optimal workflow
- **💫 Modern Animations** - Smooth transitions and visual feedback

### ⚡ Performance Features
- **🚀 Multi-Threaded Engine** - Parallel processing for maximum speed
- **📈 Progress Tracking** - Individual and overall progress monitoring
- **⏱️ Time Estimation** - Accurate remaining time calculations
- **📊 Transfer Analytics** - Speed, data transferred, and completion statistics
- **🔔 Notification System** - Audio and visual completion alerts

## 🚀 Quick Start

### Prerequisites
- **Java JDK 17+** (Download from [Oracle](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html) or [OpenJDK](https://adoptium.net/))
- **Maven 3.6+** (For building from source)
- **JavaFX 17+** (Included in the project dependencies)

### Installation Methods

#### Method 1: Using Pre-built JAR (Recommended)
```bash
# Download the latest release
# Run the application
java -jar ShareIT-Premium-Suite.jar
```

#### Method 2: Build from Source
```bash
# Clone the repository
git clone https://github.com/kueiyiee/ShareIT-Advanced-file-transfer-app.git

# Navigate to project directory
cd "ShareIT-Advanced file transfer app"

# Build with Maven
mvn clean package

# Run the application
java -jar target/ShareIT-Premium-Suite.jar
```

#### Method 3: Run in IDE (IntelliJ IDEA/Eclipse)
1. Import as Maven project
2. Set JDK to version 17+
3. Run `FileTransferApp.java` as main class

## 🎯 Operation Modes

### 🚀 SENDER MODE
- Send multiple files to a receiver
- Up to 5 concurrent transfers
- Queue management system
- Target IP configuration

### 📥 RECEIVER MODE
- Listen for incoming files
- Automatic file organization
- Multi-client support
- Real-time status updates

## 🛠️ Installation Guide

### Step-by-Step Setup

#### Windows Users
```powershell
# 1. Verify Java Installation
java -version

# 2. Download the latest release
# 3. Extract the ZIP file
# 4. Double-click ShareIT-Premium-Suite.jar
```

#### Linux/Mac Users
```bash
# 1. Install Java if not present
sudo apt-get install openjdk-17-jdk  # Ubuntu/Debian
# or
brew install openjdk@17              # macOS

# 2. Make the JAR executable
chmod +x ShareIT-Premium-Suite.jar

# 3. Run the application
java -jar ShareIT-Premium-Suite.jar
```

### Network Configuration
- **Port**: 12345 (default, configurable)
- **Protocol**: TCP/IP with custom secure layer
- **Firewall**: Ensure port 12345 is open for incoming connections
- **LAN Support**: Works on local networks without internet

## 📊 Architecture

### System Architecture
```
┌─────────────────────────────────────────────┐
│           USER INTERFACE LAYER              │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐       │
│  │  Sender │ │Receiver │ │ Settings│       │
│  │   Mode  │ │  Mode   │ │  Panel  │       │
│  └─────────┘ └─────────┘ └─────────┘       │
└─────────────────────────────────────────────┘
                 │
┌─────────────────────────────────────────────┐
│         APPLICATION LOGIC LAYER             │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐       │
│  │Transfer │ │ Progress│ │  Logging│       │
│  │ Manager │ │ Tracker │ │  System │       │
│  └─────────┘ └─────────┘ └─────────┘       │
└─────────────────────────────────────────────┘
                 │
┌─────────────────────────────────────────────┐
│        NETWORK COMMUNICATION LAYER          │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐       │
│  │Client   │ │ Server  │ │ Security│       │
│  │Handler  │ │ Handler │ │  Layer  │       │
│  └─────────┘ └─────────┘ └─────────┘       │
└─────────────────────────────────────────────┘
```

### File Transfer Protocol
1. **Handshake** - Connection establishment and verification
2. **Metadata Exchange** - File information transfer
3. **Chunk Transfer** - Segmented data transmission
4. **Verification** - Integrity check and confirmation
5. **Completion** - Status update and cleanup

## 🎨 UI Features

### Dashboard Layout
```
┌─────────────────────────────────────────────────────────────────┐
│ 🚀 SHAREIT PREMIUM SUITE - Advanced Multi-Threaded System       │
├─────────────────────────────────────────────────────────────────┤
│ [Operation Mode]  🚀 SENDER MODE   📥 RECEIVER MODE             │
│ [IP Configuration] 🌐 Target IP: ___________ [Get My IP]        │
├─────────────────────────────────────────────────────────────────┤
│ 📁 FILE TRANSFER QUEUE        📊 REAL-TIME ANALYTICS    📋 LOG  │
│ ┌─────────────────────────┐ ┌─────────────────────┐ ┌─────────┐ │
│ │ File Name      │ Status │ │ Time: 00:00:00      │ │ System  │ │
│ │───────────────┼─────────│ │ Data: 0 MB          │ │  Logs   │ │
│ │ Size  │ Progress        │ │ Active: 0           │ │         │ │
│ │ Speed │                 │ │ Speed: 0.00 MB/s    │ │         │ │
│ └─────────────────────────┘ └─────────────────────┘ └─────────┘ │
├─────────────────────────────────────────────────────────────────┤
│ 🎯 CONTROLS: [📁 Select Files] [📂 Select Folder] [🗑️ Clear]    │
│            [🚀 Launch Transfer] [⏸️ Pause] [❌ Abort]           │
│ Overall Progress: ████████████████████ 75%                     │
└─────────────────────────────────────────────────────────────────┘
```

### Theme Options
- **🌙 Dark Theme** (Default) - Professional dark interface
- **☀️ Light Theme** - Clean light mode for daytime use
- **🔵 Blue Theme** - Professional blue accent theme
- **🟣 Purple Theme** - Modern purple theme

### Font Sizes
- **Small** - Compact interface
- **Medium** - Recommended default
- **Large** - Enhanced readability
- **Extra Large** - Accessibility mode

## 🔧 Technical Details

### Built With
- **Java 17** - Core programming language
- **JavaFX 17** - Modern UI framework
- **Maven** - Dependency management and build tool
- **TCP/IP** - Network communication protocol
- **Multi-threading** - Concurrent processing engine

### Key Components
1. **FileTransferController** - Main application controller
2. **FileTransferClient** - Client-side transfer logic
3. **FileTransferServer** - Server-side receiver logic
4. **FileTransfer Model** - Data structure for transfer tracking
5. **Analytics Engine** - Real-time performance monitoring

### Performance Metrics
- **Max File Size**: Limited only by available disk space
- **Transfer Speed**: Up to network bandwidth limits
- **Concurrent Transfers**: Configurable (default: 5)
- **Memory Usage**: Optimized for efficiency
- **CPU Usage**: Multi-threaded load distribution

## 📁 Project Structure

```
ShareIT-Advanced-file-transfer-app/
├── src/
│   ├── main/
│   │   ├── java/com/shareit/shareit/
│   │   │   ├── FileTransferApp.java          # Main application entry
│   │   │   ├── FileTransferController.java   # Main controller
│   │   │   ├── FileTransferClient.java       # Client implementation
│   │   │   ├── FileTransferServer.java       # Server implementation
│   │   │   └── FileTransfer.java            # Data model
│   │   └── resources/
│   │       └── view/
│   │           ├── file_transfer_ui.fxml     # UI layout
│   │           └── images/                   # Application images
│   └── test/                                 # Test files
├── target/                                   # Build output
├── pom.xml                                   # Maven configuration
├── README.md                                 # This file
└── LICENSE                                   # MIT License
```

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### Reporting Issues
1. Check existing issues to avoid duplicates
2. Use the issue template with detailed information
3. Include steps to reproduce, expected vs actual behavior

### Feature Requests
1. Clearly describe the proposed feature
2. Explain the use case and benefits
3. Consider if it aligns with project goals

### Pull Requests
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Setup
```bash
# 1. Fork and clone
git clone https://github.com/YOUR_USERNAME/ShareIT-Advanced-file-transfer-app.git

# 2. Create feature branch
git checkout -b feature/your-feature

# 3. Make changes and test
mvn clean test

# 4. Commit changes
git add .
git commit -m "Description of changes"

# 5. Push and create PR
git push origin feature/your-feature
```

## 👨‍💻 Developer

### About Me
**Kuei Poch Kuei**  
3rd Year Computer Science Student  
Dilla University - College of Engineering & Technology  
Department of Computer Science  

### 🎓 Education
- **Institution**: Dilla University
- **College**: College of Engineering and Technology
- **Department**: Computer Science
- **Year**: 3rd Year Student
- **Specialization**: Software Engineering & Network Systems

### 💻 Technical Skills
- **Languages**: Java, Python, JavaScript, C++
- **Frameworks**: JavaFX, Spring Boot, React
- **Networking**: TCP/IP, Socket Programming, Multi-threading
- **Databases**: MySQL, MongoDB, PostgreSQL
- **Tools**: Git, Maven, Docker, IntelliJ IDEA

### 📞 Contact Information
- **📧 Personal Email**: kueiyiee@gmail.com
- **🎓 University Email**: kuei.poch@du.edu.et
- **📱 Phone**: +251 937 910 246
- **💻 GitHub**: [github.com/kueiyiee](https://github.com/kueiyiee)
- **💼 LinkedIn**: [linkedin.com/in/kueiyieeyt](https://www.linkedin.com/in/kueiyieeyt)

### 🎯 Philosophy
*"My Logic is your Limit"* - I believe in pushing the boundaries of what's possible through innovative thinking and robust software engineering. Every line of code is an opportunity to solve real-world problems efficiently and elegantly.

### 🏆 Achievements
- Developed ShareIT Premium Suite from concept to completion
- Implemented multi-threaded file transfer system
- Created intuitive UI with advanced features
- Integrated dual calendar system (Gregorian & Ethiopian)
- Built secure network communication protocol

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Kuei Poch Kuei

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## 🙏 Acknowledgments

- **Dilla University** - For providing excellent education and resources
- **JavaFX Community** - For the powerful UI framework
- **Open Source Contributors** - For inspiration and best practices
- **Computer Science Department** - Faculty and staff support
- **All Users** - For testing and providing valuable feedback

## ⭐ Support the Project

If you find this project useful, please consider:
- Giving it a ⭐ on GitHub
- Sharing with colleagues and friends
- Contributing to the codebase
- Reporting bugs and suggesting features
- Spreading the word on social media

---

<div align="center">
  
  **Made with ❤️ by Kuei Poch Kuei**
  
  *Dilla University • College of Engineering & Technology • Computer Science Department*
  
  🎓 **3rd Year Computer Science Student** • 💡 **"My Logic is your Limit"**

</div>

---

### 📈 Future Roadmap

#### Short-term Goals (Next 3 Months)
- [ ] Mobile companion app
- [ ] Cloud backup integration
- [ ] Enhanced encryption protocols
- [ ] Plugin system for extensions

#### Medium-term Goals (6-12 Months)
- [ ] Web-based administration panel
- [ ] AI-powered transfer optimization
- [ ] Blockchain-based verification
- [ ] Cross-platform mobile apps

#### Long-term Vision
- Enterprise-grade file transfer solution
- Integration with major cloud providers
- Machine learning for transfer prediction
- Global CDN support for large transfers

---

### 🚨 Troubleshooting Guide

#### Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| "Java not found" | Install JDK 17+ and set JAVA_HOME |
| "Port 12345 in use" | Change port in application settings |
| "Connection refused" | Check firewall and network settings |
| "Slow transfer speeds" | Reduce concurrent transfers or check network |
| "UI not loading" | Update JavaFX or check graphics drivers |

#### Getting Help
1. Check the [Wiki](https://github.com/kueiyiee/ShareIT-Advanced-file-transfer-app/wiki) for detailed guides
2. Search existing [Issues](https://github.com/kueiyiee/ShareIT-Advanced-file-transfer-app/issues)
3. Create a new issue with detailed information
4. Contact developer for urgent issues

---

### 📚 Additional Resources

- [Java Documentation](https://docs.oracle.com/javase/17/docs/)
- [JavaFX Documentation](https://openjfx.io/javadoc/17/)
- [Maven Guide](https://maven.apache.org/guides/)
- [Network Programming Tutorial](https://docs.oracle.com/javase/tutorial/networking/)
- [Multi-threading Guide](https://docs.oracle.com/javase/tutorial/essential/concurrency/)

---

**Thank you for using ShareIT Premium Suite!** 🎉