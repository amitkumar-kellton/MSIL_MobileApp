package com.msil.appium;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class API {

	static DateTimeFormatter OutputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm:ss a");
	static String createdAt;
	static String imageFullPath;
	static String imageThumbFullPath;
	static boolean isVideo;
	static int id;
	static String image;
	static String imagePath;
	static int size;
	static String captureDate;
	static int employeeId;

	public static void listAPI(String mediaType, String count, int user) throws IOException {
		DateTimeFormatter DateFormat = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		LocalDateTime currentTime = LocalDateTime.now();

		String Path = null;
		if (user == 1) {
			Path = "/Users/amitkumar/eclipse-workspace/MSILAppium/Files/API Reports/" + currentTime.format(DateFormat)
					+ "/23145/";
			FileUtils.forceMkdir(new File(Path));
		} else if (user == 3) {
			Path = "/Users/amitkumar/eclipse-workspace/MSILAppium/Files/API Reports/" + currentTime.format(DateFormat)
					+ "/90217/";
			FileUtils.forceMkdir(new File(Path));
		} else if (user == 4) {
			Path = "/Users/amitkumar/eclipse-workspace/MSILAppium/Files/API Reports/" + currentTime.format(DateFormat)
					+ "/77777/";
			FileUtils.forceMkdir(new File(Path));
		}

		try {
			Boolean QA = false;
			String BaseURL;
			String token;
			String Server;

			if (QA == true) {
				Server = "QA";
				BaseURL = "https://msil-qa-api.kellton.net/api/";
				token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyUHJpbmNpcGFsTmFtZSI6IjIzMTQ1QG5pc2hhbnRzY2x1Yi5vbm1pY3Jvc29mdC5jb20iLCJjcmVhdGVkQXQiOiIyMDI0LTAzLTE0VDAwOjE2OjE3LjAwMFoiLCJpYXQiOjE3MjQzMTA2MzEsImV4cCI6MTcyNDM5NzAzMX0.Y-qZP_i_Pp5kxezB9JLkaZg3l26Cf5djAdvzpLS1_lc";
			} else {
				Server = "UAT";
				BaseURL = "https://msil-uat-api.kellton.net/api/";
				token = "";
			}

			// Create a URL object for the API endpoint
			@SuppressWarnings("deprecation")
			URL url = new URL(BaseURL + "users/" + mediaType + "/" + user);
			System.out.println("URL: " + url + "\n");
			// URL url = new URL(BaseURL + "videos");

			// Create an HttpURLConnection instance
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Set request method to GET
			connection.setRequestMethod("POST");

			// Set Bearer token in Authorization header
			connection.setRequestProperty("Authorization", "Bearer " + token);
			connection.setRequestProperty("Content-Type", "application/json");

			// Enable output for the connection
			connection.setDoOutput(true);

			// Create the JSON body for the request
			JSONObject requestBody = new JSONObject();
			requestBody.put("limit", count);

			// Write the body to the connection's output stream
			try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
				outputStream.writeBytes(requestBody.toString());
				outputStream.flush();
			}

			// Get the response code
			int responseCode = connection.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			// Read response data into BufferedReader
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Input and output date formats
			SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
			String formattedTimestamp;
			Date uploadDate;
			Date capDate;

			JSONObject jsonResponse = new JSONObject(response.toString());
			JSONObject result = jsonResponse.getJSONObject("result");

			// Extract data from the "data" array
			JSONArray dataArray = result.getJSONArray("data");
			JSONObject pagination = result.getJSONObject("pagination");
			JSONObject userInfo = result.getJSONObject("userInfo");

			int employeeId = userInfo.getInt("employeeId");

			int totalCount = pagination.getInt("totalCount");
			System.out.println("\n\nTotal Count: " + totalCount);

			try (FileWriter writer = new FileWriter(
					Path + mediaType + "_" + currentTime.format(DateFormat) + "_" + employeeId + ".csv")) {

				writer.append(" , , , , ,Server: " + Server + ",User: " + employeeId + ",Total " + mediaType + ": ,"
						+ totalCount + ",Report Date: " + currentTime.format(OutputFormatter) + "\n");
				writer.append("S.No.,ID,Emp. ID,Size,Capture Date,Uploaded Date,Upload Time," + mediaType + " Name,"
						+ mediaType + " Path," + mediaType + " URL\n");

				JSONObject dataObject;
				JSONObject UserModel;

				long totalTime = 0;
				long averageTimeMili = 0;
				double totalSize = 0;
				long leastTime = 0;
				long temp = 0;
				int i = 0;
				DecimalFormat df = new DecimalFormat("#.00");

				for (i = 0; i < dataArray.length(); i++) {
					dataObject = dataArray.getJSONObject(i);
					UserModel = dataObject.getJSONObject("UserModel");

					System.out.println("\n************************ Start Print API Data ************************\n");

					// Adding Serial Number in the sheet
					writer.append(i + 1 + ",");

					id = dataObject.getInt("id");
					writer.append(id + ",");
					System.out.println(i + 1 + ". " + mediaType + " id: " + id + "\n");

					employeeId = UserModel.getInt("employeeId");
					writer.append(employeeId + ",");
					System.out.println(i + 1 + ". Employee ID: " + employeeId + "\n");

					isVideo = dataObject.getBoolean("isVideo");
					// writer.append(isVideo + ",");
					System.out.println(i + 1 + ". isVideo: " + isVideo + "\n");

					// size = dataObject.getInt("size");
					double size = dataObject.getDouble("size");
					double fileSizeInMB = 0;

					if (isVideo == true) {
						writer.append(size + ",");
						totalSize += size;
						System.out.println(i + 1 + ". " + mediaType + " size: " + size + "\n");
					} else {
						// Convert the file size to megabytes
						fileSizeInMB = size / (1024.0 * 1024.0);
						String formattedFileSize = df.format(fileSizeInMB);
						writer.append(formattedFileSize + ",");
						System.out.println(i + 1 + ". " + mediaType + " size: " + formattedFileSize + "\n");
					}

					totalSize += fileSizeInMB;

					captureDate = dataObject.getString("captureDate");
					capDate = inputFormat.parse(captureDate);
					formattedTimestamp = outputFormat.format(capDate);
					writer.append(formattedTimestamp + ",");
					System.out.println(i + 1 + ". " + mediaType + " captureDate: " + capDate + "\n");

					createdAt = dataObject.getString("createdAt");
					uploadDate = inputFormat.parse(createdAt);
					formattedTimestamp = outputFormat.format(uploadDate);
					writer.append(formattedTimestamp + ",");
					System.out.println(i + 1 + ". " + mediaType + " Uploaded at: " + uploadDate + "\n");

					// Calculate time difference
					long diffInMillis = uploadDate.getTime() - capDate.getTime();
					totalTime += diffInMillis;

					if (temp <= 0) {
						temp = diffInMillis;
					}

					if (diffInMillis <= temp) {

						leastTime = diffInMillis;
						temp = diffInMillis;
					}

					long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis);
					long days = TimeUnit.SECONDS.toDays(diffInSeconds);
					diffInSeconds -= TimeUnit.DAYS.toSeconds(days);
					long hours = TimeUnit.SECONDS.toHours(diffInSeconds);
					diffInSeconds -= TimeUnit.HOURS.toSeconds(hours);
					long minutes = TimeUnit.SECONDS.toMinutes(diffInSeconds);
					diffInSeconds -= TimeUnit.MINUTES.toSeconds(minutes);

					// Print and write the time difference
					String timeDifference = String.format("%d days %d hours %d minutes %d seconds", days, hours,
							minutes, diffInSeconds);
					writer.append(timeDifference + ",");
					System.out.println(i + 1 + ". Time Difference: " + timeDifference + "\n");

					image = dataObject.getString("image");
					writer.append(image + ",");
					System.out.println(i + 1 + ". " + mediaType + ": " + image + "\n");

					imagePath = dataObject.getString("imagePath");
					writer.append(imagePath + ",");
					System.out.println(i + 1 + ". " + mediaType + " Path: " + imagePath + "\n");

					imageFullPath = dataObject.getString("imageFullPath");
					// @SuppressWarnings("deprecation")
					// URL imageUrl = new URL(imageFullPath);
					// BufferedImage imageProcessing = ImageIO.read(imageUrl);
					// ImageIO.write(imageProcessing, "jpg", new File(Path + image));
					writer.append(imageFullPath);
					System.out.println(i + 1 + ". " + mediaType + " FullPath: " + imageFullPath + "\n");

					writer.append("\n");

					System.out.println("\n************************ End Print API Data ************************\n");

				}
				System.out.println("\n\nLeast Time taken by an Image Milli: " + leastTime);
				System.out.println(
						"\n\nLeast Time taken by an Image Seconds: " + TimeUnit.MILLISECONDS.toSeconds(leastTime));

				averageTimeMili = (totalTime / dataArray.length());
				long averageTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(averageTimeMili);

				System.out.println("\n\nAverage Time: " + averageTimeMinutes + " Minutes");

				System.out.println("\n\nAverage Time: " + averageTimeMinutes / 60 + " Hours " + averageTimeMinutes % 60
						+ " Minutes");

				if (totalSize >= 1024) {
					totalSize = (totalSize / 1024);
					System.out.println("Total Size of data: " + df.format(totalSize) + " GB");
					writer.append(", ,Size: ," + df.format(totalSize) + " GB");
				} else {
					System.out.println("Total Size of data: " + df.format(totalSize) + " MB");
					writer.append(", ,Size: " + df.format(totalSize) + " MB");
				}
				writer.append(", , ,Average Time: " + averageTimeMinutes / 60 + " Hours "
						+ averageTimeMinutes % 60 + " Minutes");
			}

			// Close the connection
			connection.disconnect();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public static void timeStampConvertor(String test) {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String formattedTimestamp;
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");

		System.out.println("Testing " + test);

		String timeStamp = "2024-08-13T07:24:47.000Z";
		try {
			Date inputTimeStamp = inputFormat.parse(timeStamp);
			formattedTimestamp = outputFormat.format(inputTimeStamp);
			System.out.println("\n\nFormatted Time Stamp for the given Input: " + formattedTimestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws InterruptedException, IOException {

		String mediaType = "videos";
		int user = 1;
		String count = "50";

		listAPI(mediaType, count, user);
		// photosListAPI();

		// timeStampConvertor(mediaType);

	}
}