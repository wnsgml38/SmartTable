package com.vr;

import java.io.File;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier.VisualClass;

	public class userClassifier {

	private static final String API_KEY = "eefb0ae9edf72ba5fd48d02298b77ff8420afbea";
	private static final String CLASSIFIER_ID = "food";
	
	
	public void testClassifier() throws Exception{
		VisualRecognition service = new VisualRecognition(VisualRecognition.VERSION_DATE_2016_05_20);
		service.setApiKey(API_KEY);
		
		//runClassifier(service,"boots1.jpg");
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
		
		List<ImageClassification> imgCls = result.getImages();
		JsonArray classes = new JsonArray();
		if(imgCls !=null){
			for(ImageClassification ic : imgCls){
				List<VisualClassifier> vc = ic.getClassifiers();
				for(VisualClassifier v : vc){
					List<VisualClass> vss = v.getClasses();
					for(VisualClass one : vss){
						JsonObject c = new JsonObject();
						c.addProperty("name", one.getName());
						c.addProperty("score", one.getScore());
						classes.add(c);
					}
				}
			}
		}
		String foodresult = classes.get(0).getAsJsonObject().get("name").toString(); 
		System.out.println(foodresult);
		
		
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
