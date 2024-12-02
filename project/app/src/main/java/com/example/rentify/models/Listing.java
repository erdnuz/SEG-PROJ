package com.example.rentify.models;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Listing implements Serializable {
	private String id;
	private Category category;
	private String description;
	private String title;
	private Lessor lessor;
	private double price;
	private long startDate, endDate;

	// Constructor
	public Listing() {

	}

	public Listing(Category category, String description, String title, Lessor lessor, double price, Long startDate, Long endDate) {
		this.category = category;
		this.description = description;
		this.title = title;
		this.lessor = lessor;
		this.price = price;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public boolean isMatch(Category category, String search) {
		// If category is specified, check if it matches the current object's category
		if (category != null && !category.equals(this.category)) {
			return false;
		}

		// If search is null or empty, assume it's a match for all listings (if no filtering is needed)
		if (search == null || search.isEmpty()) {
			return true;
		}

		// Combine title and description and check if the search string is contained
		return (this.title + this.description).toLowerCase().contains(search.toLowerCase());
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


	public String getStartFormatted() {
		return formatDate(startDate);
	}

	public String getEndFormatted() {
		return formatDate(endDate);
	}

	private String formatDate(long dateLong) {
		// Extract year, month, and day using modulus and division
		int year = (int) (dateLong / 10000);          // Extract year
		int month = (int) ((dateLong % 10000) / 100); // Extract month
		int day = (int) (dateLong % 100);            // Extract day

		// Format the date into "dd/MM/yy"
		return day + "/" + month + "/" + year % 100;
	}

	public Long getStartDate() {
		return this.startDate;
	}

	public Long getEndDate() {
		return this.endDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Listing)) return false;
		Listing l = (Listing) other;
		if (!l.getTitle().equals(this.title)) return false;
		return (l.getLessor().equals(this.lessor));
	}
}