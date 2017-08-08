package com.vr;

import java.io.File;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

public class GeneralClassifier {

	private static final String API_KEY = "";
	
	public void testClassifier() throws Exception{
		VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		service.setApiKey(API_KEY);
		
		System.out.println("Classifiy an image : ");
		
		ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
				.images(new File("resource/dog.jpg"))
				.build();
		
		VisualClassification result = service.classify(options).execute();
		
		System.out.print(result);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			(new GeneralClassifier()).testClassifier();
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}
