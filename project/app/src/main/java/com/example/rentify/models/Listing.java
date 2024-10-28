package com.example.rentify.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Listing implements Serializable {
	private String id;
	private Category category;
	private String description;
	private String title;
	private Lessor lessor;
	private double price;
	private Availability availability;

	// Constructor
	public Listing() {
		this.availability = new Availability();
	}

	public Listing(Category category, String description, String title, Lessor lessor, double price) {
		this.category = category;
		this.description = description;
		this.title = title;
		this.lessor = lessor;
		this.price = price;
		this.availability = new Availability();
	}

	public Listing(Category category, String description, String title, Lessor lessor, double price, Availability ava) {
		this.category = category;
		this.description = description;
		this.title = title;
		this.lessor = lessor;
		this.price = price;
		this.availability = ava;
	}

	// Methods
	public void reserveAvailability(Slot slot) {
		availability.reserve(slot);
	}

	public boolean isAvailable(Slot slot) {
		return availability.isAvailable(slot);
	}

	public boolean isMatch(String category, Slot slot, String title) {
		boolean matchesCategory = (category == null || category.equals(this.category));
		boolean matchesTitle = (title == null || title.toLowerCase().contains(title.toLowerCase()));
		boolean matchesAvailability = (availability == null || isAvailable(slot));
		return matchesAvailability && matchesCategory && matchesTitle;
	}

	// Setters and getters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Lessor getLessor() {
		return lessor;
	}

	public void setLessor(Lessor lessor) {
		this.lessor = lessor;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Availability getAvailability() {
		return availability;
	}

	public void setAvailability(Availability ava) {
		this.availability = ava;
	}
}
