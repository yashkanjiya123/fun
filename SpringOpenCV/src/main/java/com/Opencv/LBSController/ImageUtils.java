package com.Opencv.LBSController;

import java.io.IOException;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.web.multipart.MultipartFile;

public class ImageUtils {

	public static Mat multipartToMat(MultipartFile file) throws IOException {
        // Convert MultipartFile to byte array
        byte[] bytes = file.getBytes();

        // Convert byte array to Mat
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        return converter.convert(new org.bytedeco.opencv.opencv_core.IplImage(new BytePointer(bytes)));
    }
}
