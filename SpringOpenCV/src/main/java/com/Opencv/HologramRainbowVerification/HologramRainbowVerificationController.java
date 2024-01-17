package com.Opencv.HologramRainbowVerification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import nu.pattern.OpenCV;

@RestController
@RequestMapping("/hologram-rainbow-verification")
public class HologramRainbowVerificationController {
	
	 

	    @PostMapping("/verify")
	    public ResponseEntity<String> verifyHologramRainbowPrint(@RequestParam("file") MultipartFile file) {
	    	OpenCV.loadShared();
	    	try {
	            // Validate file
	            if (file.isEmpty()) {
	                return ResponseEntity.badRequest().body("Please upload a file.");
	            }

	            // Convert MultipartFile to OpenCV Mat
	            Mat image = convertMultipartFileToMat(file);

	            // Verify hologram/rainbow print
	            boolean isHologramRainbowPrint = verifyHologramRainbowPrint(image);

	            if (isHologramRainbowPrint) {
	                return ResponseEntity.ok("The image contains a hologram or rainbow print.");
	            } else {
	                return ResponseEntity.ok("No hologram or rainbow print detected in the image.");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body("Error processing the uploaded image.");
	        }
	    }

	    private Mat convertMultipartFileToMat(MultipartFile file) throws IOException {
	        byte[] bytes = file.getBytes();
	        Mat image = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
	        return image;
	    }

	    private boolean verifyHologramRainbowPrint(Mat image) {
	        // Convert to grayscale
	        Mat grayImage = new Mat();
	        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

	        // Apply a threshold to segment the hologram/rainbow region
	        Mat thresholdImage = new Mat();
	        Imgproc.threshold(grayImage, thresholdImage, 100, 255, Imgproc.THRESH_BINARY);

	        // Find contours in the thresholded image
	        Mat hierarchy = new Mat();
	        List<MatOfPoint> contours = new ArrayList<>();
	        Imgproc.findContours(thresholdImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

	        // Filter contours based on area
	        double minContourArea = 500;
	        List<MatOfPoint> filteredContours = new ArrayList<>();
	        for (MatOfPoint contour : contours) {
	            if (Imgproc.contourArea(contour) > minContourArea) {
	                filteredContours.add(contour);
	            }
	        }

	        // If there are filtered contours, the hologram/rainbow print is likely present
	        return !filteredContours.isEmpty();
	    }
}
