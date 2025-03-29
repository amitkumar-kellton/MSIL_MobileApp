package com.msil.appium;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.common.collect.ImmutableMap;

import io.appium.java_client.android.AndroidDriver;

public class MSILStart {

	static AndroidDriver driver;
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
	static Random random = new Random();

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws InterruptedException, IOException {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("platformName", "Android"); // Capabilities for Platform Name
		capabilities.setCapability("automationName", "uiAutomator2"); // Capabilities for Automation Name
		capabilities.setCapability("deviceName", "RZCW51H3AVB"); // Capabilities for Device Name
		capabilities.setCapability("app", "C:\\Users\\amitk\\Desktop\\MSIL Android Builds\\QA\\MSIL_54.apk"); // Cap for
																												// App
																												// Path
		capabilities.setCapability("autoGrantPermissions", "true"); // Capabilities for Premissions to be granted always

		URL url = null;
		try {
			url = new URL("http://127.0.0.1:4723");
			System.out.println("\n\nConnection Sucessful...");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

//		Initialize the AndroidDriver
		driver = new AndroidDriver(url, capabilities);
		System.out.println("\n\nDriver Sucessfully Initiated...");

//		Random Number output for file renaming
		int randomNumber = random.nextInt(100) + 1;

//		Updated time for Screen Recording Start
		LocalDateTime now = LocalDateTime.now();
		String formattedcurrentTime = now.format(formatter);

//      Screen Recording Started using ADB Commands
		String command = "adb shell screenrecord /sdcard/screenrecord" + randomNumber + ".mp4";
		Process process = Runtime.getRuntime().exec(command);
		System.out.println("\nScreen Recording Started Sucessfully at: " + formattedcurrentTime);

//		Thread Sleep for 5 Seconds before Login Method
		System.out.println("\nThread Sleep for 5 Seconds before Login Method.");
		Thread.sleep(5000);

//		Login Method Call to capture images.
		Login(driver);

//		Thread Sleep for 5 Seconds before imageCap Method
		System.out.println("\nThread Sleep for 5 Seconds before imageCap Method.");
		Thread.sleep(5000);

//		imageCap Method Call to capture images.
		imageCap(driver);

//      Stop screen recording
		process.destroy();

//		Updated time for Screen Recording Stop
		now = LocalDateTime.now();
		formattedcurrentTime = now.format(formatter);
		System.out.println("\nScreen Recording Stopped Sucessfully at: " + formattedcurrentTime);

		driver.quit();
	}

	public static void Login(AndroidDriver driver) throws InterruptedException {
		System.out.println("\n******************************** Login Method Called ********************************");

//		MSIL Login Button Click on HomeScreen
		WebElement msilLoginbtn = driver.findElement(By.xpath("//android.widget.TextView[@text=\"Login\"]"));
		msilLoginbtn.click();

//		Thread Sleep for 10 Seconds before Microsoft Login Page
		System.out.println("\nThread Sleep for 10 Seconds");
		Thread.sleep(10000);

//		Switch to WebView from the Native App
		Set<String> contextHandles = driver.getContextHandles();
		System.out.println("\n\nContext Set List: \n" + contextHandles);

		// Variable to Print current app package and activity
		String appPackage = driver.getCurrentPackage();
		String appActivity = driver.currentActivity();

		for (String context : contextHandles) {
			System.out.println("Contexts in the For Loop: " + context);

			if (context.contains("WEBVIEW_chrome")) {
				driver.context(context);
				System.out.println(
						"\nSwitched to WEBVIEW_chrome Context in the for loop and If Condition of WebView: " + context);

				driver.findElement(By.xpath("//input[@value='Continue']")).click();
				System.out.println("\n\nContinue Button Clicked Sucessfully in For Loop.");
			}
			;

			System.out.println("\n\nCurrent App Package: " + appPackage);
			System.out.println("\n\nCurrent App Activity: " + appActivity);

			driver.getContextHandles();
			System.out.println("Contexts after the If Condition: " + contextHandles);

		}
//		driver.startActivity("com.android.chrome", "org.chromium.chrome.browser.customtabs.CustomTabActivity");
	}

//	************************************************* imageCap Method ********************************************************

	public static void imageCap(AndroidDriver driver) throws InterruptedException, IOException {
		System.out.println("\n******************************* imageCap Method Called *******************************");

//		Switch to Native App from the WebView.
		Set<String> contextHandles = driver.getContextHandles();
		System.out.println("Contexts after switchToWebView Method: " + contextHandles);

		for (String context : contextHandles) {
			if (context.contains("NATIVE_APP")) {
				System.out.println("Contexts in the For Loop: " + context);
				driver.context(context);
			}
		}

//		Thread Sleep for 5 Seconds
		System.out.println("\nThread Sleep for 5 Seconds after loop");
		Thread.sleep(5000);

//		Tap on MSIL Camera Icon
		WebElement MSILcam = driver.findElement(By.xpath(
				"//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.view.ViewGroup/android.view.ViewGroup/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[2]/com.horcrux.svg.SvgView/com.horcrux.svg.l/com.horcrux.svg.l"));
		MSILcam.click();

//		Thread Sleep for 5 Seconds
		System.out.println("\nThread Sleep for 5 Seconds after loop");
		Thread.sleep(5000);

//		Camera Shutter Button WebElement Variable
		WebElement shutterbtn = driver.findElement(
				By.xpath("//android.view.View[@resource-id=\"com.sec.android.app.camera:id/bottom_background\"]"));

//		Number of images to be captured and time duration between them.
		int images = 20;
		int interval = 10;

		for (int i = 1; i <= images; i++) {
			int randomNumber = random.nextInt(100) + 1;

			// Get current date and time
			LocalDateTime now = LocalDateTime.now();
			String formattedDateTime = now.format(formatter);

			shutterbtn.click();

			System.out.println("\n\nNumber of Images Captured: " + i + ", with interval of: " + interval
					+ " Seconds, at: " + formattedDateTime);
			Thread.sleep(interval * 1000);

			String recentImage = getMostRecentImage(driver);
			byte[] imageData = driver.pullFile(recentImage);
			FileUtils.writeByteArrayToFile(new File(
					"C:/Users/amitk/eclipse-workspace/AppiumDemo/Files/Images/recentImage-" + randomNumber + ".jpg"),
					imageData);
		}

//		Open Gallery
		driver.activateApp("com.sec.android.gallery3d");

//		Thread Sleep for 5 Seconds
		Thread.sleep(5000);

		driver.findElement(By.xpath(
				"(//android.widget.FrameLayout[@resource-id=\"com.sec.android.gallery3d:id/deco_view_layout\"])[1]"))
				.click();

//		Thread Sleep for 1 Minutes after Gallery
		Thread.sleep(10000);

	}

	public static String getMostRecentImage(AndroidDriver driver) {
//		driver.context("NATIVE_APP");
		String recentImagePath = driver
				.executeScript("mobile: shell", ImmutableMap.of("command", "ls -t /sdcard/DCIM/Camera | head -n 1"))
				.toString().trim();
		return "/sdcard/DCIM/Camera/" + recentImagePath;
	}


}





//driver.findElement(By.xpath(""));
//driver.findElement(By.xpath(""));
//
//driver.findElement(By.xpath(""));
//driver.findElement(By.xpath(""));
//driver.findElement(By.xpath(""));

//System.out.println("\n\n");
