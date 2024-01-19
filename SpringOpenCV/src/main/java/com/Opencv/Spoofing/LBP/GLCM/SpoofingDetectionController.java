package com.Opencv.Spoofing.LBP.GLCM;

import java.io.IOException;
import java.util.Arrays;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import nu.pattern.OpenCV;

@RestController
public class SpoofingDetectionController {

	final static Logger logger = LoggerFactory.getLogger(SpoofingDetectionController.class);
	
	static {
        OpenCV.loadLocally();
    }
	
	@PostMapping("/detectspoofing")
    public String detectSpoofing(@RequestParam("InputFile") MultipartFile InputFile) throws IOException 
    {
        // Read the image
        Mat image=Imgcodecs.imdecode(new MatOfByte(InputFile.getBytes()),Imgcodecs.IMREAD_UNCHANGED);

        // Convert the image to grayscale
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Calculate LBP features
        Mat lbpFeatures = calculateLBP(grayImage);
        logger.info("LBP Completeed");
        // Calculate GLCM features
        double[] glcmFeatures = calculateGLCM(grayImage);
        logger.info("GLCM Completeed");

        // Print the features (for demonstration purposes)
        for(double i:glcmFeatures) {
        	logger.info("this si "+i);
        }
        //System.out.println("LBP Features: " + Arrays.toString(((DoubleStream) lbpFeatures).toArray()));
        //System.out.println("GLCM Features: " + Arrays.toString(glcmFeatures));

        // Perform spoofing detection (placeholder logic)
        if (isSpoofed(lbpFeatures, glcmFeatures)) {
            return "The image is likely manipulated or spoofed.";
        } else {
            return "The image is likely authentic.";
        }
        
	}
	
	private Mat calculateLBP(Mat image) {
		 // Initialize the output LBP image
        Mat lbpImage = new Mat(image.size(), CvType.CV_8UC1);

        // Apply LBP transformation
        Imgproc.Laplacian(image, lbpImage, CvType.CV_8U);

        return lbpImage;
    }

	private static double[] calculateGLCM(Mat image) {
        // Convert the image to integer values
        Mat intImage = new Mat();
        image.convertTo(intImage, CvType.CV_8U);

        // Calculate GLCM
        Mat glcm = new Mat();
        Imgproc.calcHist(Arrays.asList(intImage), new MatOfInt(0), new Mat(), glcm, new MatOfInt(256), new MatOfFloat(0, 256), false);

        // Normalize the GLCM
        Core.normalize(glcm, glcm, 1, 0, Core.NORM_MINMAX);

        // Extract GLCM features (contrast, energy, correlation, etc.)
        double[] glcmFeatures = new double[4];
        glcmFeatures[0] = Core.norm(glcm, Core.NORM_INF); // Contrast
        glcmFeatures[1] = Core.norm(glcm, Core.NORM_L1); // Energy
        glcmFeatures[2] = Core.norm(glcm, Core.NORM_L2); // Homogeneity
        glcmFeatures[3] = (glcm.rows() * glcm.cols()) * Core.trace(glcm).val[0] - 1.0; // Correlation

        return glcmFeatures;
    }

    private boolean isSpoofed(Mat lbpFeatures, double[] glcmFeatures) {
        // Placeholder for spoofing detection logic
        // Replace with your actual detection logic (e.g., machine learning model)
        // For simplicity, using a basic threshold-based detection
        double lbpThreshold = 10.0;
        double glcmThreshold = 0.5;

        return lbpFeatures.rows() > lbpThreshold || Arrays.stream(glcmFeatures).anyMatch(value -> value > glcmThreshold);
    }
	
	
	
}
