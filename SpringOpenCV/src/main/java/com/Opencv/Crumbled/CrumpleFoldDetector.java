package com.Opencv.Crumbled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import nu.pattern.OpenCV;

@RestController
@RequestMapping("/canny")
public class CrumpleFoldDetector {
	
	   final static Logger logger = LoggerFactory.getLogger(CrumpleFoldDetector.class);

	   static {
	        OpenCV.loadLocally();
	    }
	   
	    @GetMapping("/detection")
	    public String showForm() {
	        return "canny_form";
	    }
	    
	    @PostMapping("/detection")
	    public ResponseEntity<String> detectCrumpleOrFold(@RequestParam("file") MultipartFile file) {
	        try {
	            boolean isCrumpledOrFolded = isCrumpledOrFolded(file);
	            logger.info("this is "+isCrumpledOrFolded);
	            if (isCrumpledOrFolded) {
	                return ResponseEntity.ok("The image is crumpled or folded.");
	            } else {
	                return ResponseEntity.ok("The image is not crumpled or folded.");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body("Error processing the image.");
	        }
	    }
	    
	    private boolean isCrumpledOrFolded(MultipartFile imageData) throws IOException {
	    	
	    	// Read the image
	    	//image can read on opencv
	    	Mat image = Imgcodecs.imdecode(new MatOfByte(imageData.getBytes()),Imgcodecs.IMREAD_UNCHANGED);

	        // Convert the image to grayscale
	    	//
	        Mat grayImage = new Mat();
	        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
	        
	        // Apply GaussianBlur to reduce noise
	        double ss=0.0;
	        Imgproc.GaussianBlur(grayImage, grayImage, new Size(5, 5),ss);

	        // Apply adaptive thresholding to create a binary image
	        Mat thresholdImage = new Mat();
	        Imgproc.adaptiveThreshold(grayImage, thresholdImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY_INV, 11, 2);

	        // Find contours in the thresholded image
	        List<MatOfPoint> contours = new ArrayList<>();
	        Mat hierarchy = new Mat();
	        Imgproc.findContours(thresholdImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

	        // Filter contours based on area to remove small noise
	        double minContourArea = 1000.0; // Adjust as needed
	        List<MatOfPoint> filteredContours = new ArrayList<>();
	        for (MatOfPoint contour : contours) {
	            double area = Imgproc.contourArea(contour);
	            if (area > minContourArea) {
	                filteredContours.add(contour);
	            }
	        }
	        logger.info("this is "+filteredContours.size());
	        // Determine if the document is crumpled based on the number of contours
	        return filteredContours.size() > 5; // Adjust as needed
	    }

}
