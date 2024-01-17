package com.Opencv.LBSController;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FaceRecognitionController {

	 @PostMapping("/api/face-recognition")
	    public String recognizeFace(@RequestParam("file") MultipartFile file) {
	        // Check if the file is empty
	        if (file.isEmpty()) {
	            return "Please upload a file.";
	        }

	        try {
	            // Load the face cascade for detection
	            CascadeClassifier faceCascade = new CascadeClassifier("haarcascades/haarcascade_frontalface_default.xml");

	            // Load the LBPH face recognizer
	            LBPHFaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
	            faceRecognizer.read("path/to/your/trained/model.xml");

	            // Convert the input image to Mat
	            //Mat inputImage = ImageUtils.multipartToMat(file);
	         

	            // Convert the input image to grayscale
	            Mat grayImage = new Mat();
	            org.bytedeco.opencv.global.opencv_imgproc.cvtColor(inputImage, grayImage, org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2GRAY);

	            // Detect faces in the input image
	            RectVector faces = new RectVector();
	            faceCascade.detectMultiScale(grayImage, faces);

	            // Perform face recognition on each detected face
	            for (Rect face : faces.get()) {
	                // Extract the face region
	                Mat detectedFace = new Mat(grayImage, face);

	                // Resize the face image to the required size for recognition
	                Mat resizedFace = new Mat();
	                org.bytedeco.opencv.global.opencv_imgproc.resize(detectedFace, resizedFace, new Size(100, 100));

	                // Perform LBPH face recognition
	                int[] label = new int[1];
	                double[] confidence = new double[1];
	                faceRecognizer.predict(resizedFace, label, confidence);

	                // Display the result
	                System.out.println("Predicted Label: " + label[0]);
	                System.out.println("Confidence: " + confidence[0]);
	            }

	            return "Face recognition completed!";
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "Error processing the file: " + e.getMessage();
	        }
	    }
}
