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
		this.title = title;
		this.description = description;
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
		listingMap.put("title", title);
		listingMap.put("description", description);
		listingMap.put("lessor", lessor.getId());
		listingMap.put("price", price);
		listingMap.put("available", true);
		listingMap.put("availableFrom", null);
		listingMap.put("availableUntil", null);
		listingMap.put("requests", new ArrayList<>()); // Deliverable 4 feature

		// Additional feature for the +10 bonus on Deliverable 3
		listingMap.put("images", new ArrayList<>());

		/*
		** POSSIBLE additional features for the +15 bonus on Deliverable 4 **
		listingMap.put("reviews", new ArrayList<>());
		listingMap.put("rating", 0);
		listingMap.put("numReviews", 0);
		listingMap.put("comments", new ArrayList<>());
		listingMap.put("likes", 0);
		listingMap.put("dislikes", 0);
		listingMap.put("views", 0);
		listingMap.put("createdAt", null);
		listingMap.put("updatedAt", null);
		listingMap.put("deletedAt", null);
		listingMap.put("deleted", false);
		listingMap.put("deletedBy", null);
		listingMap.put("deletedReason", null);
		listingMap.put("deletedAt", null);
		listingMap.put("deletedBy", null);
		listingMap.put("deletedReason", null);
		listingMap.put("deletedAt", null);
		listingMap.put("deletedBy", null);
		listingMap.put("deletedReason", null);

		 */


		return listingMap;

	}

}

