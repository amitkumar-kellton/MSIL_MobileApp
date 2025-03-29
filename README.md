# MSIL Mobile App Automation Testing

This repository contains automated test scripts for the **MSIL Mobile App**, focusing on the **image capturing process** using **Appium, Java, and Selenium**. The automation ensures seamless testing of camera functionality, image capture, and validation within the application.

## Features

### 1. Image Capturing Automation
The test suite verifies the image capturing process across different scenarios.

#### **Test Steps:**
1. Launch the mobile application using Appium.
2. Navigate to the camera screen.
3. Capture an image using the device camera.
4. Close the application.

## Getting Started

### **Prerequisites**
- **Java JDK 8+**
- **Maven**
- **Eclipse/IntelliJ IDEA**
- **Appium Server**
- **Selenium WebDriver**
- **Android Emulator / Real Device**
- **Git**

### **Setup Instructions**
1. **Clone the repository:**
   ```sh
   git clone https://github.com/amitkumar-kellton/MSIL_MobileApp.git
   cd your-repository
   ```
2. **Install dependencies:**
   ```sh
   mvn clean install
   ```
3. **Start Appium Server:**
   ```sh
   appium
   ```
4. **Run tests:**
   ```sh
   mvn test
   ```
