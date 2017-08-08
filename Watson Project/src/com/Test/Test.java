package com.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JsonObject user = new JsonObject();
		user.addProperty("id", "ABC");
		user.addProperty("name", "에이비씨");
		
		JsonArray addrHistory = new JsonArray();
		
		JsonObject item = new JsonObject();
		item.addProperty("ord", 1);
		item.addProperty("addr", "서울시");
		
		addrHistory.add(item);
		
		item = new JsonObject();
		item.addProperty("ord", 2);
		item.addProperty("addr", "제주시");
		
		addrHistory.add(item);
		
		JsonObject root = new JsonObject();
		root.add("user", user);
		root.add("addrHistory", addrHistory);
		
		System.out.println(root.toString());

	}

}
