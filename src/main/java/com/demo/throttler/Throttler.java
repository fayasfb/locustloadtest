package com.loconav.throttler;

import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Throttler implements Runnable {
	String url;
	String method;
	Map<String, String> headers;
	String jsonString;
	int limit;

	public Throttler(String url, Map<String, String> headers, String method,int limit) {
		this.url = url;
		this.headers = headers;
		this.method = method;
		this.limit = limit;
	}

	public Throttler(String url, Map<String, String> headers, String method, String jsonString,int limit) {
		this.url = url;
		this.headers = headers;
		this.method = method;
		this.jsonString = jsonString;
		this.limit = limit;
	}

	@Override
	public void run() {
		try {
			Response response = null;
			for (int i = 0; i < limit+5; i++) {
				RequestSpecBuilder specBuilder = new RequestSpecBuilder() {
					{
						addHeaders(headers);
					}
				};
				RequestSpecification reqSpec = specBuilder.build();
				if (method.equalsIgnoreCase("GET")) {
					response = RestAssured.given().spec(reqSpec).get(url);
				}
				if (method.equalsIgnoreCase("POST") && jsonString != null) {
					response = RestAssured.given().spec(reqSpec).body(jsonString).post(url);
				}
				if (method.equalsIgnoreCase("PUT") && jsonString != null) {
					response = RestAssured.given().spec(reqSpec).body(jsonString).put(url);
				}
				if (method.equalsIgnoreCase("DELETE")) {
					response = RestAssured.given().spec(reqSpec).delete(url);
				}
				if (response.statusCode() == 429) {
					System.out.println("After" + " " + i + " " + "iteration");
					System.err.println(response.headers());
					break;
				}
			}
			if (response.statusCode() != 429) {
				System.err.println("Hooray Found a bug (: Limit Exceeded...");
			}
		} catch (Exception e) {

		}

	}

	public static void main(String[] args) throws InterruptedException {
		int window = (int)Integer.valueOf(args[0]);    //provide window wait in milliseconds
		int limit = (int)Integer.valueOf(args[1]);
		Map<String, String> headers = new HashMap<String, String>() {
			{
				put("User-id", System.getProperty("userid"));
				put("Admin-Authentication", System.getProperty("adminauth"));
				put("Authorization", System.getProperty("auth"));
				put("Content-Type", "application/json");
			}
		};
		Throttler throttler = new Throttler(System.getProperty("url"),headers, System.getProperty("method"),limit);
		Thread thread = new Thread(throttler);
		thread.start();
		thread.join();
		System.out.println("----------------------------------------------------------------------------------");
		System.out.println("Applying window wait for "+window/1000 + " seconds...");
		Thread.sleep(window);
		Throttler throttler1 = new Throttler(System.getProperty("url"),headers, System.getProperty("method"),limit);
		Thread thread1 = new Thread(throttler1);
		thread1.start();
		thread1.join();
//		System.out.println("----------------------------------------------------------------------------------");
//		System.out.println("Applying window wait for "+window/60000 + " minutes...");
//		Thread.sleep(window);
//		String jsonString="{\r\n"
//				+ "    \"drivers\": [\r\n"
//				+ "        {\r\n"
//				+ "            \"name\": \"PDriver 2\",\r\n"
//				+ "            \"country_code\": \"IN\",\r\n"
//				+ "            \"phone_number\": \"9898117221\"\r\n"
//				+ "        }\r\n"
//				+ "    ]\r\n"
//				+ "}";
//		Throttler throttler2 = new Throttler("http://ind-stg-marketplace.loconav.com/api/v1/drivers",headers, "Post",jsonString,limit);
//		Thread thread2 = new Thread(throttler2);
//		thread2.start();
//		thread2.join();
//		System.out.println("----------------------------------------------------------------------------------");
//		System.out.println("Applying window wait for "+window/60000 + " minutes...");
//		Thread.sleep(window);
//		String jsonString1="{\r\n"
//				+ "    \"drivers\": [\r\n"
//				+ "        {\r\n"
//				+ "            \"id\": 261,\r\n"
//				+ "            \"name\": \"PDriver 2\",\r\n"
//				+ "            \"country_code\": \"IN\",\r\n"
//				+ "            \"phone_number\": \"9898117221\"\r\n"
//				+ "        }\r\n"
//				+ "    ]\r\n"
//				+ "}";
//		Throttler throttler3 = new Throttler("http://ind-stg-marketplace.loconav.com/api/v1/drivers",headers, "Put",jsonString1,limit);
//		Thread thread3 = new Thread(throttler3);
//		thread3.start();
//		thread3.join();
	}
}
