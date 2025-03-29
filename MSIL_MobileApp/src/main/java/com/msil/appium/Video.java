package com.msil.appium;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.remote.MobileCapabilityType;

public class Video {
	static AndroidDriver driver;
	static String Path = "/Users/amitkumar/eclipse-workspace/MSILAppium/Files/Videos/";
	static String OutputFormatter = "DD-MMM-yyyy hh:mm:ss a";
	static String PathFormatter = "DD-MMM-yyyy hh mm a";

	public static void main(String[] args) throws InterruptedException, IOException {

//		Initialize the Appium Connection
		connection();

//		Creating a directory
		FileUtils.forceMkdir(new File(Path));

//		Start Screen Recording
		driver.startRecordingScreen(
				AndroidStartScreenRecordingOptions.startScreenRecordingOptions().withTimeLimit(Duration.ofMinutes(10)));

//		Updated time for Screen Recording Start
		System.out.println("\nScreen Recording Started Sucessfully at: " + driver.getDeviceTime(OutputFormatter));

//		Open MSIL app Method
		openMSIL();

//		Start Video Record Method
		VideoRecord(driver);

//		Open Gallery Method
		openGallery();

		driver.stopRecordingScreen();
		System.out.println("\nScreen Recording Stopped Sucessfully at: " + driver.getDeviceTime(OutputFormatter));

		Thread.sleep(3000);

		String base64Video = driver.stopRecordingScreen();

		// Decode base64 to binary and save as a video file
		byte[] decodedVideo = Base64.getDecoder().decode(base64Video);
		try (FileOutputStream fos = new FileOutputStream(new File("screen_recording.mp4"))) {
			fos.write(decodedVideo);
		}

		driver.quit();

		System.out.println("\nDriver Sucessfully Closed...");
	}

	public static void openMSIL() throws InterruptedException {

//		Open MSIL Application
		driver.activateApp("com.msilapp");

		System.out.println("\nThread Sleep for 5 Seconds after Opening MSIL App.");
		Thread.sleep(5000);

//		Open Camera Application via MSIL App
		WebElement MSILcam = driver.findElement(By.xpath(
				"//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.view.ViewGroup/android.view.ViewGroup/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[2]/com.horcrux.svg.SvgView/com.horcrux.svg.l"));
		MSILcam.click();

	}

	public static void openGallery() {

//		Open Samsung Gallery
		driver.activateApp("com.sec.android.gallery3d");
	}

	public static void VideoRecord(AndroidDriver driver) throws InterruptedException {

		Thread.sleep(2000);

//		Camera Shutter Button WebElement Variable
		WebElement shutterbtn = driver.findElement(
				By.xpath("//android.view.View[@resource-id=\"com.sec.android.app.camera:id/bottom_background\"]"));

//		How many videso want to record?
		int numberofVideos = 6;
		int x = 0;
		int y = 0;
		String Orientation = driver.getOrientation().toString();

		System.out.println("\n\nTime: " + driver.getDeviceTime(OutputFormatter));

		for (int i = 1; i <= numberofVideos; i++) {

			Thread.sleep(2000);

			Orientation = driver.getOrientation().toString();

			if (Orientation.equals("LANDSCAPE")) {
				System.out.print("\nOrientation: " + Orientation + "\n");
				x = 2040;
				y = 475;

			} else if (Orientation.equals("PORTRAIT")) {
				System.out.print("\nOrientation: " + Orientation + "\n");
				x = 630;
				y = 2030;
			}

			shutterbtn.click();

			System.out.println(
					"\nVideo " + i + " Recording Started Sucessfully at: " + driver.getDeviceTime(OutputFormatter));

			Thread.sleep((i * 10000) + 1000);

			PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
			Sequence sequence = new Sequence(finger, 1)
					.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
					.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
					.addAction(new Pause(finger, Duration.ofMillis(150)))
					.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
			driver.perform(Collections.singletonList(sequence));

			System.out.println(
					"\nVideo " + i + " Recording Completed Sucessfully at: " + driver.getDeviceTime(OutputFormatter));

		}
	}

	@SuppressWarnings("deprecation")
	public static void connection() {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("platformName", "Android"); // Capabilities for Platform Name
		capabilities.setCapability("automationName", "uiAutomator2"); // Capabilities for Automation Name
//		capabilities.setCapability("deviceName", "RZCW51H3AVB"); // Capabilities for Device Name
//		capabilities.setCapability("app", "/Users/amitkumar/eclipse-workspace/MSILAppium/Files/MSIL_Android_Builds/QA/MSIL_55.apk");		//Cap for App Path
		capabilities.setCapability("autoGrantPermissions", "true"); // Capabilities for Premissions to be granted always
		capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 300);

		URL url = null;
		try {
			url = new URL("http://127.0.0.1:4723");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Initialize the AndroidDriver
		driver = new AndroidDriver(url, capabilities);
		System.out.println("\nDriver Sucessfully Initiated...\n");
	}

}
