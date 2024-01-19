package com.Opencv.CannyEdgeDetectionController;

import java.util.Base64;


import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import nu.pattern.OpenCV;

@Controller
@RequestMapping("/canny")
public class CannyEdgeController {

	    @GetMapping("/form")
	    public String showForm() {
	        return "canny_form";
	    }

	    @PostMapping("/detect")
	    public String detectEdges(@RequestParam("file") MultipartFile file, Model model) {
	    	OpenCV.loadShared();
	        try {
	        	
	            // Load the input image
	        	//MayOfByte i sOpencv data Stucture
	        	//.getBytes can convert into bytecode
	        	//Mat is Fundamaent class of opencv image
	        	//This function decodes an image from the specified MatOfByte object. 
	        	//IMREAD_COLOR indicates that the image should be read in color.
	            Mat originalImage = Imgcodecs.imdecode(new MatOfByte(file.getBytes()), Imgcodecs.IMREAD_COLOR);

	            // Convert the image to grayscale
	            // store data for GaryImage in Mat
	            Mat grayImage = new Mat();
	            
	            //This line uses the cvtColor function from the Imgproc class to convert the originalImage from the BGR color space to grayscale
	            Imgproc.cvtColor(originalImage, grayImage, Imgproc.COLOR_BGR2GRAY);

	            // Apply GaussianBlur to reduce noise and improve edge detection
	            //new Size(5, 5): The size of the Gaussian kernel. In this case, it's a 5x5 kernel. The larger the kernel, the stronger the blur effect.
	            //0: The standard deviation of the kernel along both X and Y directions. A value of 0 indicates that the standard deviation is computed based on the kernel size.
	            Imgproc.GaussianBlur(grayImage, grayImage, new Size(5, 5), 0);

	            // Perform Canny Edge Detection
	            //Opencv class to store image in Mat class 
	            Mat edges = new Mat();
	            
	            //canny edge algoritm
	            Imgproc.Canny(grayImage, edges, 50, 150);

	            // Convert the original and edge images to byte arrays
	            MatOfByte originalMatOfByte = new MatOfByte();
	            Imgcodecs.imencode(".png", originalImage, originalMatOfByte);
	            byte[] originalBytes = originalMatOfByte.toArray();
	            String originalBase64 = Base64.getEncoder().encodeToString(originalBytes);
	          

	            MatOfByte edgesMatOfByte = new MatOfByte();
	            Imgcodecs.imencode(".png", edges, edgesMatOfByte);
	            byte[] edgesBytes = edgesMatOfByte.toArray();
	            String edgesBase64 = Base64.getEncoder().encodeToString(edgesBytes);

	            // Add the resources to the model for rendering in the HTML page
	            model.addAttribute("originalImage", originalBase64);
	            model.addAttribute("edgesImage", edgesBase64);

	            return "canny_result";

	        } catch (Exception e) {
	            e.printStackTrace();
	            return "error";
	        }
	    }
}
