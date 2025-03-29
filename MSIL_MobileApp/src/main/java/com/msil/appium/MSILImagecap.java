package com.msil.appium;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import com.google.common.collect.ImmutableMap;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;

public class MSILImagecap {
	static AndroidDriver driver;
	static String UATBuid = "67";
	static String Server = "UAT";
	static String speed = "WiFi";
	static String resolution = "50 MP";
	static String PathFormatter = "DD-MMM-yyyy hh.mm a";
	static String OutputFormatter = "DD-MMM-yyyy hh:mm:ss a";
	static String Path = "/Users/amitkumar/eclipse-workspace/MSILAppium/Files/Output/" + Server + "_" + speed + "_"
			+ resolution + driver.getDeviceTime(PathFormatter) + "/";

	@SuppressWarnings("deprecation")
	public static void connection() {
		System.out.println("Connection Method callled.");
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("platformName", "Android"); // Capabilities for Platform Name
		capabilities.setCapability("automationName", "uiAutomator2"); // Capabilities for Automation Name
//		capabilities.setCapability("deviceName", "RZCW51H3AVB"); // Capabilities for Device Name
//		capabilities.setCapability("app", "/Users/amitkumar/eclipse-workspace/MSILAppium/Files/MSIL_Android_Builds/QA/MSIL_55.apk");		//Cap for App Path
		capabilities.setCapability("autoGrantPermissions", "true"); // Capabilities for Premissions to be granted always

		URL url = null;
		try {
			url = new URL("http://127.0.0.1:4723");
			System.out.println("\n\nConnection Sucessful...\n");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		// Initialize the AndroidDriver
		driver = new AndroidDriver(url, capabilities);
		System.out.println("\n\nDriver Sucessfully Initiated...");
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		System.out.println("Main Method");
		connection();

		FileUtils.forceMkdir(new File(Path));

//		Start Screen Recording
		driver.startRecordingScreen(
				AndroidStartScreenRecordingOptions.startScreenRecordingOptions().withTimeLimit(Duration.ofMinutes(30)));

//		Time for Screen Recording Start
		System.out.println("\nScreen Recording Started Sucessfully at: " + driver.getDeviceTime(OutputFormatter));


//		imageCap Method Call to capture images.
		imageCap(driver);

		// Stop screen recording and get the video file as a Base64 encoded string
		String base64Video = driver.stopRecordingScreen();

		// Decode Base64 string to bytes
		byte[] decodedBytes = Base64.getDecoder().decode(base64Video);
//
//		Update time for Screen Recording Stop
		System.out.println("\nScreen Recording Stopped Sucessfully at: " + driver.getDeviceTime(OutputFormatter));
//
//		// Save the video to a file
		Files.write(Paths.get(Path + "Build_" + UATBuid + "_" + driver.getDeviceTime(PathFormatter) + ".mp4"),
				decodedBytes);

		driver.quit();
		System.out.println("Driver Sucessfully Closed...");

	}

	public static void imageCap(AndroidDriver driver) throws InterruptedException, IOException {
		System.out.println("\n******************************* imageCap Method Called *******************************");

//		Date and Time Variables
		Duration duration;
		long ImageInterval = 1;

//		Open MSIL Application
		driver.activateApp("com.msilapp");

		System.out.println("\nThread Sleep for 5 Seconds after Opening MSIL App.");
		Thread.sleep(5000);

//		Open Camera Application via MSIL App
		WebElement MSILcam = driver.findElement(By.xpath(
				"//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[2]/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[1]/android.view.ViewGroup/android.view.ViewGroup/android.widget.FrameLayout/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup[3]/android.view.ViewGroup[2]/com.horcrux.svg.SvgView/com.horcrux.svg.l/com.horcrux.svg.l"));
		MSILcam.click();

//		Camera Shutter Button WebElement Variable
		WebElement shutterbtn = driver.findElement(
				By.xpath("//android.view.View[@resource-id=\"com.sec.android.app.camera:id/bottom_background\"]"));

//		Number of images to be captured and Thread Sleep time between them.
		int images = 5; // <--------------------------------Change Image Count and interval here
		int interval = 3;
		String StartTime = driver.getDeviceTime(PathFormatter);
		String EndTime = null;

//		Date and Time update in variables

		try (FileWriter writer = new FileWriter(Path + "MSIL_" + StartTime + ".csv")) {
			writer.append("MSIL,Online Mode,Build No.: " + UATBuid + ",Date: " + driver.getDeviceTime(OutputFormatter)
					+ ",Speed: " + speed + "\n");
			writer.append(images + "-Images," + "Intverval: " + interval + " seconds, Image Resolution: ," + resolution
					+ "\n");
			writer.append("S.No.,Interval,Shutter Time,Captured Date,File Name,Size Bytes,Result\n");

			System.out.println("\nThread Sleep for 5 Seconds after Opening Camera App.");
			Thread.sleep(5000);

			for (int i = 1; i <= images; i++) {
				try {
					System.out.println("\n\n\n*********************** Image: " + i + " Start ***********************");
					// Shutter Button click of camera
					shutterbtn.click();

					// Get current date and time
					StartTime = driver.getDeviceTime(OutputFormatter);

					// Thread sleep for interval between two images to be captured
					Thread.sleep(interval * 1000);
					System.out.println("\nWaiting " + interval + " seconds for image to be captured...");

					String recentImage = getMostRecentImage(driver);
//					String recentImagePath = "/sdcard/DCIM/Camera/" + recentImage;

					byte[] imageData = driver.pullFile("/sdcard/DCIM/Camera/" + recentImage);

					// Save the image file locally
//					FileUtils.writeByteArrayToFile(new File(Path + "Images/" + recentImage), imageData);
					File localImageFile = new File(Path + "Images/" + recentImage);

					System.out.println(
							"\nImage saved at this path " + Path + "Images/" + " with filename " + recentImage);

					// Ensure the directory exists
					localImageFile.getParentFile().mkdirs();

					try (FileOutputStream fos = new FileOutputStream(localImageFile)) {
						fos.write(imageData);
					}

					BasicFileAttributes attrs = Files.readAttributes(localImageFile.toPath(),
							BasicFileAttributes.class);

					// Format the creation time
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a")
							.withZone(ZoneId.systemDefault());
					String formattedCreationTime = formatter.format(attrs.creationTime().toInstant());

					System.out.println("\nFormatted Image Timestamp: " + formattedCreationTime);

					System.out.println("\nImage Size: " + attrs.size() + " bytes");

					EndTime = driver.getDeviceTime(OutputFormatter);

					// Duration (Time taken) between capturing two photos.
					duration = Duration.between(null, null);
					ImageInterval = duration.getSeconds();

					System.out.println("\nNumber of Images Captured: " + i + ", with interval of: " + ImageInterval
							+ " Seconds, at: " + EndTime);

					System.out.println("\n************************* Image: " + i + " End *************************");
					writer.append("Image: " + i);
					writer.append(",");
					writer.append(ImageInterval + " Seconds");
					writer.append(",");
					writer.append(EndTime);
					writer.append(",");
					writer.append(formattedCreationTime);
					writer.append(",");
					writer.append(recentImage);
					writer.append(",");
					writer.append(String.valueOf(attrs.size()));
					writer.append(",");
					writer.append("Pass");
					writer.append("\n");

				} catch (Exception e) {
					System.out.println("An error occurred: " + e.getMessage());
					writer.append("Image: " + i);
					writer.append(",");
					writer.append(ImageInterval + " Seconds");
					writer.append(",");
					writer.append(EndTime);
					writer.append(",");
					writer.append("NA");
					writer.append(",");
					writer.append("Fail");
					writer.append("\n");
					continue;
				}
			}
		}

//		Open Gallery
		driver.activateApp("com.sec.android.gallery3d");

//		Screenshot using adb shell commands
//		driver.executeScript("mobile: shell",
//				ImmutableMap.of("command", "screencap /sdcard/Screenshot" + randomNumber + ".png"));
//
		Thread.sleep(10000);
//
//		driver.findElement(By.xpath(
//				"(//android.widget.FrameLayout[@resource-id=\"com.sec.android.gallery3d:id/deco_view_layout\"])[1]"))
//				.click();

	}

	public static void imgDiff() {

		// Initially assigning null
		BufferedImage imgA = null;
		BufferedImage imgB = null;

		// Try block to check for exception
		try {
			// creating object of File class
			File fileA = new File("/Users/amitkumar/Downloads/srinivas_venkataraman_swaminathan_1721299887228.jpg");
			File fileB = new File("/Users/amitkumar/Downloads/Profile2.jpg");

			// Reading files
			imgA = ImageIO.read(fileA);
			imgB = ImageIO.read(fileB);
		}
		// Catch block to check for exceptions
		catch (IOException e) {
			// Display the exceptions on console
			System.out.println(e);
		}

		// Assigning dimensions to image
		int width1 = imgA.getWidth();
		int width2 = imgB.getWidth();
		int height1 = imgA.getHeight();
		int height2 = imgB.getHeight();

		// Checking whether the images are of same size or
		// not
		if ((width1 != width2) || (height1 != height2))

			// Display message straightaway
			System.out.println("Error: Images dimensions" + " mismatch");
		else {

			// By now, images are of same size

			long difference = 0;

			// treating images likely 2D matrix

			// Outer loop for rows(height)
			for (int y = 0; y < height1; y++) {

				// Inner loop for columns(width)
				for (int x = 0; x < width1; x++) {

					int rgbA = imgA.getRGB(x, y);
					int rgbB = imgB.getRGB(x, y);
					int redA = (rgbA >> 16) & 0xff;
					int greenA = (rgbA >> 8) & 0xff;
					int blueA = (rgbA) & 0xff;
					int redB = (rgbB >> 16) & 0xff;
					int greenB = (rgbB >> 8) & 0xff;
					int blueB = (rgbB) & 0xff;

					difference += Math.abs(redA - redB);
					difference += Math.abs(greenA - greenB);
					difference += Math.abs(blueA - blueB);
				}
			}

			double total_pixels = width1 * height1 * 3;

			// Note: Average pixels per color component
			double avg_different_pixels = difference / total_pixels;

			// There are 255 values of pixels in total
			double percentage = (avg_different_pixels / 255) * 100;

			// Lastly print the difference percentage
			System.out.println("Difference Percentage-->" + percentage);

		}
	}

	public static String getMostRecentImage(AndroidDriver driver) {
		String recentImage = driver
				.executeScript("mobile: shell", ImmutableMap.of("command", "ls -t /sdcard/DCIM/Camera | head -n 1"))
				.toString().trim();

//		System.out.println("\n\n\n\nPath: " + recentImagePath);
		return recentImage;
	}


}

//System.out.println("");
//System.out.println("");
//System.out.println("");
//System.out.println("");
//System.out.println("");
//System.out.println("");
//System.out.println("");
