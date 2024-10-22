package com.example.rentify.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class DatabaseHelper {

    private static DatabaseHelper instance;
    private final DatabaseReference dbRef;

    private DatabaseHelper() {
        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    // Singleton pattern
    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    /* -------------- Users Table Methods -------------- */

    public void createUser(User user) {
        String userId = dbRef.child("users").push().getKey();
        user.setId(userId);
        dbRef.child("users").child(userId).setValue(user);
    }

    public void getUser(String id, QueryCallback callback) {
        Query query = dbRef.child("users").child(id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    User user = null;

                    // Check the role and instantiate the appropriate subclass
                    if ("RENTER".equals(role)) {
                        user = dataSnapshot.getValue(Renter.class);
                    } else if ("LESSOR".equals(role)) {
                        user = dataSnapshot.getValue(Lessor.class);
                    } else if ("ADMIN".equals(role)) {
                        user = dataSnapshot.getValue(Admin.class);
                    }

                    if (user != null) {
                        user.setId(dataSnapshot.getKey());
                        callback.results.put("user", user);
                        callback.onSuccess();
                    } else {
                        callback.onError(new NullPointerException("User is null"));
                    }
                } else {
                    callback.onError(new ArrayIndexOutOfBoundsException("No data found."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void userExists(String email, String phoneNumber, QueryCallback callback) {
        // Check if the email exists
        dbRef.child("users")
                .orderByChild("email")
                .equalTo(email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot emailQuery = task.getResult();
                        if (emailQuery.exists() && emailQuery.hasChildren()) {
                            callback.results.put("status", true);
                            callback.results.put("message", "Email is already in use.");
                            callback.onSuccess();
                        } else {
                            // Now check for phone number
                            dbRef.child("users")
                                    .orderByChild("phone")
                                    .equalTo(phoneNumber)
                                    .get()
                                    .addOnCompleteListener(phoneTask -> {
                                        if (phoneTask.isSuccessful()) {
                                            DataSnapshot phoneQuery = phoneTask.getResult();
                                            if (phoneQuery.exists() && phoneQuery.hasChildren()) {
                                                callback.results.put("status", true);
                                                callback.results.put("message", "Phone number is already in use.");
                                                callback.onSuccess();
                                            } else {
                                                callback.results.put("status", false);
                                                callback.onSuccess();
                                            }
                                        } else {
                                            callback.onError(new IndexOutOfBoundsException("Phone check failed."));
                                        }
                                    });
                        }
                    } else {
                        callback.onError(new IndexOutOfBoundsException("Email check failed."));
                    }
                });
    }

    public void updateUser(User user) {
        dbRef.child("users").child(user.getId()).updateChildren(user.toMap());
    }

    public void enableUser(String id, boolean status) {
        dbRef.child("users").child(id).child("enabled").setValue(status);
    }

    public void deleteUser(String id) {
        dbRef.child("users").child(id).removeValue();
    }

    /* -------------- Listings Table Methods -------------- */

    public void createListing(Listing l) {
        String listingId = dbRef.child("listings").push().getKey();
        l.setId(listingId);
        dbRef.child("listings").child(listingId).setValue(l.toMap());
    }

    // Maintains an arrayList of listing objects
    public void getListings(String searchBy, String searchValue, QueryCallback callback) {
        Query query = dbRef.child("listings").orderByChild(searchBy).equalTo(searchValue);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Listing> listings = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Listing listing = snapshot.getValue(Listing.class);
                    String listingId = snapshot.getKey();
                    listing.setId(listingId);
                    listings.add(listing);
                }
                callback.results.put("listings", listings);
                callback.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void updateListing(Listing l) {
        dbRef.child("listings").child(l.getId()).updateChildren(l.toMap());
    }

    public void deleteListingByCategory(String category) {
        dbRef.child("listings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
                    String listingCategory = listingSnapshot.child("category").getValue(String.class);
                    if (category.equals(listingCategory)) {
                        listingSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error getting listings: " + databaseError.getMessage());
            }
        });
    }

    public void getListing(String listingId, QueryCallback callback) {
        Query query = dbRef.child("listings").child(listingId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Listing listing = dataSnapshot.getValue(Listing.class);
                    listing.setId(dataSnapshot.getKey());
                    callback.results.put("listing", listing);
                    callback.onSuccess();
                } else {
                    callback.onError(new NullPointerException("No listing found."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void deleteListing(String id) {
        dbRef.child("listings").child(id).removeValue();
    }

    /* -------------- Reviews Table Methods -------------- */

    public void createReview(Review r) {
        String id = dbRef.child("reviews").push().getKey();
        r.setId(id);
        dbRef.child("reviews").child(id).setValue(r.toMap());
    }

    public void getReviews(String listingId, QueryCallback callback) {
        Query query = dbRef.child("reviews").orderByChild("listingId").equalTo(listingId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Review> reviews = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Review review = snapshot.getValue(Review.class);
                    String reviewId = snapshot.getKey();
                    review.setId(reviewId);
                    reviews.add(review);
                }
                callback.results.put("reviews", reviews);
                callback.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void updateReview(Review r) {
        dbRef.child("reviews").child(r.getId()).updateChildren(r.toMap());
    }

    public void deleteReviewByListing(String listingId) {
        dbRef.child("reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean hasDeleted = false;

                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    String reviewListingId = reviewSnapshot.child("listingId").getValue(String.class);

                    if (listingId.equals(reviewListingId)) {
                        reviewSnapshot.getRef().removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    System.out.println("Review with ID: " + reviewSnapshot.getKey() + " deleted successfully.");
                                })
                                .addOnFailureListener(e -> {
                                    System.err.println("Error deleting review: " + e.getMessage());
                                });
                        hasDeleted = true;
                    }
                }

                if (!hasDeleted) {
                    System.out.println("No reviews found for the listing ID: " + listingId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error getting reviews: " + databaseError.getMessage());
            }
        });
    }

    public void deleteReview(String reviewId) {
        dbRef.child("reviews").child(reviewId).removeValue();
    }

    /* -------------- Availability Slots Table Methods -------------- */

    public String createAvailabilitySlot(AvailabilitySlot ava) {
        String slotId = dbRef.child("availability-slots").push().getKey();
        ava.setId(slotId);
        dbRef.child("availability-slots").child(slotId).setValue(ava.toMap());
        return slotId;
    }

    public void getAvailabilitySlotById(String slotId, QueryCallback callback) {
        DatabaseReference slotRef = dbRef.child("availability-slots").child(slotId);

        slotRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    AvailabilitySlot slot = dataSnapshot.getValue(AvailabilitySlot.class);
                    slot.setId(dataSnapshot.getKey()); // Set the ID of the retrieved slot
                    callback.results.put("slot", slot);
                    callback.onSuccess();
                } else {
                    callback.onError(new NullPointerException("No slot found with the given ID."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }



    public void updateAvailabilitySlot(AvailabilitySlot slot) {
        dbRef.child("availability-slots").child(slot.getId()).updateChildren(slot.toMap());
    }

    public void deleteAvailabilitySlot(String slotId) {
        dbRef.child("availability-slots").child(slotId).removeValue();
    }
}
