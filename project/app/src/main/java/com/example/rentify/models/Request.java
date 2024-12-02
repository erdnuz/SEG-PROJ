package com.example.rentify.models;

import java.io.Serializable;

public class Request implements Serializable {

    // Attributes
    private Long date;
    private Renter renter;
    private Listing listing;
    private int status;
    private String id;
    // Default constructor
    public Request() {}

    // Constructor with parameters
    public Request(Long date, Renter renter, Listing listing) {
        this.date = date;
        this.renter = renter;
        this.listing = listing;
        this.status = 0;
    }

    public Request(Long date, Renter renter, Listing listing, int status) {
        this.date = date;
        this.renter = renter;
        this.listing = listing;
        this.status = status;
    }

    public String getId() {return id;}

    public void setId(String id) {this.id=id;}

    // Getter and Setter for date
    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    // Getter and Setter for renter
    public Renter getRenter() {
        return renter;
    }

    public void setRenter(Renter renter) {
        this.renter = renter;
    }

    public String getFormattedDate() {
        return formatDate(date);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // Getter and Setter for listing
    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    private String formatDate(long dateLong) {
        // Extract year, month, and day using modulus and division
        int year = (int) (dateLong / 10000);          // Extract year
        int month = (int) ((dateLong % 10000) / 100); // Extract month
        int day = (int) (dateLong % 100);            // Extract day

        // Format the date into "dd/MM/yy"
        return day + "/" + month + "/" + year % 100;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Request)) return false;
        Request r  = (Request) other;
        return (r.getStatus() == status && r.getRenter().equals(renter) && r.getListing().equals(listing));
    }

    @Override
    public String toString() {
        return "Request{" +
                "date=" + date +
                ", renter=" + renter +
                ", listing=" + listing +
                '}';
    }
}