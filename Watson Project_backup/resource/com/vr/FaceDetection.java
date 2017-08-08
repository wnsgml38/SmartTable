package com.vr;

import java.io.File;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.DetectedFaces;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualRecognitionOptions;

public class FaceDetection {

	private static final String API_KEY = "";
	
	public void faceDetection() throws Exception{
		VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		service.setApiKey(API_KEY);
		
		System.out.println("Face Detection : ");
		VisualRecognitionOptions options = new VisualRecognitionOptions.Builder()
				.images(new File("resource/face.jpg"))
				.build();
		
		DetectedFaces result = service.detectFaces(options).execute();
		
		System.out.println(result);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			(new FaceDetection()).faceDetection();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
