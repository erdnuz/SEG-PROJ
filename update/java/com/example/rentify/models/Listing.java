package com.example.rentify.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Listing {
	protected static ArrayList<String> categories = new ArrayList<>();
	// Example categories: {"Tools", "Camping", "Instruments"}

	private String id;
	private String category;
	private boolean hourly;
	private String description;
	private String title;
	private Lessor lessor;
	private int price;

	// Constructor to initialize Listing object
	public Listing(String id, String category, boolean hourly, String description, String title, Lessor lessor, int price) {
		this.id = id;
		this.category = category;
		this.hourly = hourly;
		this.description = description;
		this.title = title;
		this.lessor = lessor;
		this.price = price;
	}

	// Setters and getters
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}

	// Method to convert the Listing object to a Map
	public Map<String, Object> toMap() {
		Map<String, Object> listingMap = new HashMap<>();

		listingMap.put("id", id);
		listingMap.put("category", category);
		listingMap.put("hourly", hourly);
		listingMap.put("description", description);
		listingMap.put("title", title);
		listingMap.put("lessor", lessor.getId());
		listingMap.put("price", price);

		return listingMap;
	}
}

