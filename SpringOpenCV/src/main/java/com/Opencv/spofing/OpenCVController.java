package com.Opencv.spofing;

import java.io.IOException;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.SIFT;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import nu.pattern.OpenCV;

@RestController
@RequestMapping("/opencv")
public class OpenCVController {


//	    @GetMapping("/example")
//	    public String opencvExample() {
//	        // OpenCV code goes here
//	    	System.out.println("this is cool");
//	    	OpenCV.loadShared();
//	    	System.out.println("this is cool");
//	    	System.out.println("this is cool");
//	        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
//	        return "OpenCV Mat:\n" + mat.dump();
//	    }
	
		

	    @RequestMapping("/spoofing")
	    public String spoofingPage() {
	        return "spoofing";
	    }

	    @PostMapping("/detectSpoofing")
	    public ResponseEntity<String> detectSpoofing(@RequestParam("file") MultipartFile file, Model model) {
	    	OpenCV.loadShared();
	        try {
	            byte[] bytes = file.getBytes();
	            Mat image = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);

	            // Perform image spoofing detection (SIFT-based example)
	            boolean isSpoofed = detectSpoofing(image);

	            //model.addAttribute("result", isSpoofed ? "Spoofed Image" : "Genuine Image");
	            String result = isSpoofed ? "Spoofed Image" : "Genuine Image";
	            return ResponseEntity.ok(result);
	            //return ResponseEntity.ok(result);
	        } catch (IOException e) {
//	            e.printStackTrace();
//	            model.addAttribute("result", "Error processing image");
	        	 e.printStackTrace();
	             return ResponseEntity.status(500).body("Error processing image");
	        }

	    }

	    private boolean detectSpoofing(Mat image) {
	        // Perform image processing and spoofing detection
	        // (This is a simplified example and may not be effective in all scenarios)

	        // Convert image to grayscale
	        Mat grayImage = new Mat();
	        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

	        // Use SIFT (Scale-Invariant Feature Transform) to detect keypoints
	        SIFT sift = SIFT.create();
	        MatOfKeyPoint keypoints = new MatOfKeyPoint();
	        sift.detect(grayImage, keypoints);

	        // Draw keypoints on the image
	        Mat outputImage = new Mat();
	        Features2d.drawKeypoints(image, keypoints, outputImage);

	        // Spoofing detection logic (simplified example)
	        int numKeypoints = (int) keypoints.size().height;
	        return numKeypoints < 100; // Adjust threshold based on your scenario
	    }

}
