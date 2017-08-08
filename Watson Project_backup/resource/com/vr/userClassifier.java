package com.vr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;

	public class userClassifier {

	private static final String API_KEY = "eefb0ae9edf72ba5fd48d02298b77ff8420afbea";
	private static final String CLASSIFIER_ID = "food";
	
	
	public void testClassifier() throws Exception{
		VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		service.setApiKey(API_KEY);
		
		//runClassifier(service,"boots1.jpg");
		//runClassifier(service,"oxford2.jpg");
		//runClassifier(service,"sandal1.jpg");
		runClassifierByUrl(service,"https://en.wikipedia.org/wiki/Tomato#/media/File:Farmer%27s_Market_I.jpg");//"http://cfile29.uf.tistory.com/image/141F0848513ACCED37E91A");
	}
	
	public void runClassifierByUrl(VisualRecognition service, String imgUrl) throws Exception{
	System.out.println("Classify an image : " + imgUrl);
		
		ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
				.url(imgUrl)
				.classifierIds(CLASSIFIER_ID)
				.threshold(0)
				.build();
		
		VisualClassification result = service.classify(options).execute();
		
		System.out.println(result);
		System.out.println("-------------------");
		
		
	}
	
	public void runClassifier(VisualRecognition service, String image) throws Exception{
		System.out.println("Classify an image : " + image);
		
		ClassifyImagesOptions options = new ClassifyImagesOptions.Builder()
				.images(new File("resource/" + image))
				.classifierIds(CLASSIFIER_ID)
				.threshold(0)
				.build();
		
		VisualClassification result = service.classify(options).execute();
		
		System.out.println(result);
		System.out.println("-------------------");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			(new userClassifier()).testClassifier();
		} catch(Exception e){
			e.printStackTrace();
		}

	}

}
