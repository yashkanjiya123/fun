package com.Opencv.Spoofing;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImageSpoofingController {

	    @PostMapping("/detect-spoofing")
	    public ResponseEntity<String> detectSpoofing(@RequestParam("Oraginalfile") MultipartFile Oraginalfile,@RequestParam("InputFile") MultipartFile InputFile) 
	    {
	        try {
	            boolean isSpoofed = detectSpoofingcheck(Oraginalfile,InputFile);
	            if (isSpoofed) {
	                return ResponseEntity.ok("The image is likely manipulated or spoofed.");
	            } else {
	                return ResponseEntity.ok("The image is likely authentic.");
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body("Error during spoofing detection.");
	        }
	    }

	    private static boolean detectSpoofingcheck(MultipartFile originalImagePath,MultipartFile manipulatedImagePath) throws Exception {
	        // Load the original and manipulated images
	        //Mat originalImage = Imgcodecs.imread(originalImagePath);
	        Mat originalImage=Imgcodecs.imdecode(new MatOfByte(originalImagePath.getBytes()),Imgcodecs.IMREAD_UNCHANGED);
	        //Mat manipulatedImage = Imgcodecs.imread(manipulatedImagePath);
	        Mat manipulatedImage = Imgcodecs.imdecode(new MatOfByte(manipulatedImagePath.getBytes()),Imgcodecs.IMREAD_UNCHANGED);

	        // Convert images to grayscale
	        Mat grayOriginal = new Mat();
	        Mat grayManipulated = new Mat();
	        Imgproc.cvtColor(originalImage, grayOriginal, Imgproc.COLOR_BGR2GRAY);
	        Imgproc.cvtColor(manipulatedImage, grayManipulated, Imgproc.COLOR_BGR2GRAY);

	        // Calculate histogram differences
	        Mat histOriginal = calculateHistogram(grayOriginal);
	        Mat histManipulated = calculateHistogram(grayManipulated);

	        // Compare histograms using Chi-Square test
	        double result = Imgproc.compareHist(histOriginal, histManipulated, Imgproc.HISTCMP_CHISQR);

	        // Adjust this threshold based on experimentation and the nature of your images
	        double threshold = 5000;

	        return result > threshold;
	    }

	    private static Mat calculateHistogram(Mat image) {
	        List<Mat> images = new ArrayList<>();
	        images.add(image);

	        MatOfInt channels = new MatOfInt(0);
	        MatOfInt histSize = new MatOfInt(256);
	        MatOfFloat ranges = new MatOfFloat(0, 256);

	        Mat hist = new Mat();
	        Imgproc.calcHist(images, channels, new Mat(), hist, histSize, ranges);

	        Core.normalize(hist, hist, 0, hist.rows(), Core.NORM_MINMAX, -1, new Mat());

	        return hist;
	    }
	    
}
