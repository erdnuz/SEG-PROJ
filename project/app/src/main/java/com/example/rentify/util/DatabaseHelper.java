package com.example.rentify.util;


import com.example.rentify.R;
import com.example.rentify.models.Admin;
import com.example.rentify.models.Category;
import com.example.rentify.models.Lessor;
import com.example.rentify.models.Listing;
import com.example.rentify.models.Renter;
import com.example.rentify.models.Request;
import com.example.rentify.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.widget.ImageView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class DatabaseHelper {

    private static DatabaseHelper instance;
    private final DatabaseReference dbRef;
    private User currentUser;
    private List<Category> categories;

    private static final String TAG = "DatabaseHelper"; // Tag for logging

    // Private constructor (Singleton pattern)
    private DatabaseHelper() {
        dbRef = FirebaseDatabase.getInstance().getReference();

    }

    // Singleton pattern for getting the DatabaseHelper instance
    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    // Set the current user
    public void setCurrentUser(User user) {
        this.currentUser = user;
        Log.d(TAG, "Current user set: " + user); // Logging user set
    }

    // Get the current user
    public User getCurrentUser() {
        return currentUser;
    }

    // Get the list of categories
    public void getCategories(QueryCallback callback) {
        dbRef.child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {// Initialize the list of categories
                    categories = new ArrayList<Category>();
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        // Convert each child snapshot to a Category object
                        Category category = categorySnapshot.getValue(Category.class);
                        category.setId(categorySnapshot.getKey());
                        if (category != null) {
                            categories.add(category);
                        }
                    }
                } else {
                    // If the "categories" node does not exist, create it
                    createCategories();
                }
                callback.results.put("categories", categories);
                callback.onSuccess();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                categories.clear();
                callback.results.put("categories", categories);
                Log.e(TAG, "Error loading categories: " + databaseError.getMessage()); // Log any error
            }
        });
    }

    public void updateCategory(Category category) {

        // Get the unique key for the category at the specified index
        String categoryId = category.getId(); // Assuming Category has a method getId()

        // Update the category in the database
        dbRef.child("categories").child(categoryId).setValue(category)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Category updated successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update category: " + e.getMessage());
                });
    }


    // Remove a category from the list and update the database
    public void removeCategory(Category category, QueryCallback callback) {
        if (categories.size() > 3) {
            categories.remove(category);
            deleteListingByCategory(category);
            dbRef.child("categories").setValue(categories);
            callback.results.put("categories", categories);
            Log.d(TAG, "Category removed: " + category);
            callback.onSuccess();// Logging category removal
        } else {
            callback.results.put("error", "Minimum 3 categories required.");
            Log.w(TAG, "Cannot remove category, minimum 3 categories required.");
            callback.onError(new IllegalStateException("Must have at least 3 categories."));// Warning log
        }
    }

    // Add a category to the list if it doesn't already exist
    public void addCategory(Category category, QueryCallback callback) {
        if (!categories.contains(category)) {
            categories.add(category);
            dbRef.child("categories").setValue(categories);
            callback.results.put("categories", categories);
            Log.d(TAG, "Category added: " + category); // Logging category addition
            callback.onSuccess();
        } else {
            Log.w(TAG, "Category already exists: " + category);
            callback.results.put("error", "Category already exists.");
            callback.onError(new IllegalStateException("Category already exists."));
        }
    }

    // Create initial categories if none exist in the database
    private void createCategories() {
        categories = new ArrayList<>();// Add default categories with descriptions
        categories.add(new Category("Instruments", "Musical instruments like guitars, pianos, and drums."));
        categories.add(new Category("Cars", "Various car models available for short-term or long-term rent."));
        categories.add(new Category("Tools", "Tools for construction, gardening, and DIY projects."));

        dbRef.child("categories").setValue(categories); // Store categories in the database
        Log.d(TAG, "Default categories created: " + categories); // Logging default categories creation
    }


    /* -------------- Users Table Methods -------------- */

    // Load the user's avatar from Firebase Storage
    public void loadUserAvatar(User user, ImageView imageView) {
        StorageReference avatarRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://rentify-34.appspot.com")
                .child("avatars")
                .child(user.getId());

        avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the avatar using Picasso
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.def_avatar)
                    .error(R.drawable.def_avatar)
                    .into(imageView);
            Log.d(TAG, "Avatar loaded for user: " + user.getEmail()); // Logging avatar load
        }).addOnFailureListener(exception -> {
            imageView.setImageResource(R.drawable.def_avatar);
            Log.e(TAG, "Failed to load avatar for user: " + user.getEmail(), exception); // Log avatar load failure
        });
    }

    // Create a new user in the database
    public void createUser(User user) {
        String userId = dbRef.child("users").push().getKey();
        user.setId(userId);
        dbRef.child("users").child(userId).setValue(user);
        Log.d(TAG, "User created with ID: " + userId); // Logging user creation
    }

    public void getAllUsers(QueryCallback callback) {
        Query query = dbRef.child("users");
        List<User> users = new ArrayList<>();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        if (!"ADMIN".equals(userSnapshot.child("role").getValue(String.class))){
                            users.add(userSnapshot.getValue(User.class));
                        }

                    }


                }
                callback.results.put("users", users);
                callback.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "User query cancelled: " + databaseError.getMessage()); // Log query cancellation
                callback.onError(databaseError.toException());
            }
        });
    }

    // Retrieve a user by email
    public void getUserFromEmail(String email, QueryCallback callback) {
        Log.d(TAG, "Querying user with email: " + email); // Logging query start
        Query query = dbRef.child("users").orderByChild("email").equalTo(email).limitToFirst(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        getUser(userId, callback);
                        break; // Exit after finding the first match
                    }
                } else {
                    Log.w(TAG, "No user found with email: " + email); // Logging no match found
                    callback.onError(new NullPointerException("No user found with this email"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "User query cancelled: " + databaseError.getMessage()); // Log query cancellation
                callback.onError(databaseError.toException());
            }
        });
    }

    // Retrieve a user by their ID
    public void getUser(String id, QueryCallback callback) {
        Log.d(TAG, "Querying user with ID: " + id); // Logging query start
        Query query = dbRef.child("users").child(id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User superUser = dataSnapshot.getValue(User.class);
                    superUser.setId(dataSnapshot.getKey());
                    String role = superUser.getRole();
                    // Determine the user type based on their role
                    if ("RENTER".equals(role)) {
                        callback.results.put("user", dataSnapshot.getValue(Renter.class));
                    } else if ("LESSOR".equals(role)) {
                        callback.results.put("user", dataSnapshot.getValue(Lessor.class));
                    } else if ("ADMIN".equals(role)) {
                        callback.results.put("user", dataSnapshot.getValue(Admin.class));
                    }
                    Log.d(TAG, "User found: " + id + ", Role: " + role); // Logging found user
                    callback.onSuccess();
                } else {
                    Log.w(TAG, "No user found with ID: " + id); // Logging no data found
                    callback.onError(new ArrayIndexOutOfBoundsException("No data found."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "User query cancelled: " + databaseError.getMessage()); // Log query cancellation
                callback.onError(databaseError.toException());
            }
        });
    }

    // Check if a user exists by their email
    public void userExists(String email, QueryCallback callback) {
        Log.d(TAG, "Checking if user exists with email: " + email); // Logging check start
        dbRef.child("users").orderByChild("email").equalTo(email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot emailQuery = task.getResult();
                if (emailQuery.exists() && emailQuery.hasChildren()) {
                    callback.results.put("status", true);
                    callback.results.put("message", "Email is already in use.");
                    Log.d(TAG, "Email already in use: " + email); // Logging email in use
                    callback.onSuccess();
                } else {
                    callback.results.put("status", false);
                    Log.d(TAG, "Email not found: " + email); // Logging email not found
                    callback.onSuccess();
                }
            } else {
                Log.e(TAG, "Error checking email existence", task.getException()); // Logging check failure
                callback.onError(new IndexOutOfBoundsException("Email check failed."));
            }
        });
    }


    public void updateUser(User user) {
        dbRef.child("users").child(user.getId()).setValue(user);
    }

    public void deleteUser(String userId) {dbRef.child("users").child(userId).removeValue();}
    public void deleteUser(User user) {dbRef.child("users").child(user.getId()).removeValue();}

    public void sendPassResetEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = "email";

        auth.sendPasswordResetEmail(emailAddress);
    }

    /* -------------- Listings Table Methods -------------- */

    public void createListing(Listing l) {
        String listingId = dbRef.child("listings").push().getKey();
        l.setId(listingId);
        dbRef.child("listings").child(listingId).setValue(l);
    }

    public void getListingThumbnail(Listing listing, ImageView imageView) {

        // Reference to Firebase Storage for the listing thumbnail
        StorageReference thumbnailRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://rentify-34.appspot.com")
                .child("listings")
                .child(listing.getId());

        // Attempt to fetch and display the thumbnail from Firebase Storage
        thumbnailRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the thumbnail into the ImageView using Picasso
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.def_thumbnail)  // Optional placeholder image for thumbnails
                    .error(R.drawable.def_thumbnail)        // Fallback if loading fails
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            // If fetching the thumbnail fails, load the default thumbnail
            imageView.setImageResource(R.drawable.def_thumbnail);
        });
    }

    // Maintains an arrayList of listing objects
    public void getAllListings(QueryCallback callback) {
        Query query = dbRef.child("listings");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Listing> listings = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Check if the snapshot value is not null and is of the expected type
                    if (snapshot.getValue() instanceof Map) {
                        try {
                            Listing listing = snapshot.getValue(Listing.class);
                            if (listing != null) {
                                String listingId = snapshot.getKey();
                                listing.setId(listingId);
                                if (listing.getLessor().getEnabled()) {
                                    listings.add(listing);
                                }
                            } else {
                                Log.w(TAG, "Listing is null for snapshot: " + snapshot.getKey());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing listing for snapshot: " + snapshot.getKey(), e);
                        }
                    } else {
                        Log.w(TAG, "Unexpected data type for listing at snapshot: " + snapshot.getKey() + ", type: " + snapshot.getValue().getClass().getSimpleName());
                    }
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

    public void getListingsLessor(Lessor target, QueryCallback callback) {
        Query query = dbRef.child("listings");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Listing> listings = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Check if the snapshot value is not null and is of the expected type
                    if (snapshot.getValue() instanceof Map) {
                        try {
                            if (target.equals(snapshot.child("lessor").getValue(Lessor.class))) {
                                String listingId = snapshot.getKey();
                                Listing listing = snapshot.getValue(Listing.class);
                                listing.setId(listingId);
                                listings.add(listing);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing listing for snapshot: " + snapshot.getKey(), e);
                        }
                    } else {
                        Log.w(TAG, "Unexpected data type for listing at snapshot: " + snapshot.getKey() + ", type: " + snapshot.getValue().getClass().getSimpleName());
                    }
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
        dbRef.child("listings").child(l.getId()).setValue(l);
    }

    public void deleteListingByCategory(Category category) {
        dbRef.child("listings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
                    Category listingCategory = listingSnapshot.child("category").getValue(Category.class);
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

    public void deleteListingByLessor(Lessor lessor) {
        dbRef.child("listings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot listingSnapshot : dataSnapshot.getChildren()) {
                    Lessor lessor1 = listingSnapshot.child("lessor").getValue(Lessor.class);
                    if (lessor.equals(lessor1)) {
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

    public void deleteListing(Listing listing) {
        dbRef.child("listings").child(listing.getId()).removeValue();
    }

    /** Request methods**/
    public void createRequest(Request r, QueryCallback callback) {
        getRequestsByRenter(r.getRenter(), new QueryCallback() {
            @Override
            public void onSuccess() {
                List<Request> requests = (List<Request>) results.get("requests");
                for (Request request: requests) {
                    if (r.equals(request)) {
                        callback.onError(new IllegalStateException("Request already exists."));
                        return;
                    }
                }
                String requestId = dbRef.child("requests").push().getKey();
                r.setId(requestId);
                dbRef.child("requests").child(requestId).setValue(r);
                callback.onSuccess();

            }

            @Override
            public void onError(Exception err) {
                callback.onError(new RuntimeException("Unknown error in request fetch"));
            }
        });

    }

    public void updateRequest(Request r) {
        dbRef.child("requests").child(r.getId()).setValue(r);
    }

    public void getRequestsByListing(Listing target, QueryCallback callback) {
        Query query = dbRef.child("requests");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Request> requests = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Check if the snapshot value is not null and is of the expected type
                    if (snapshot.getValue() instanceof Map) {
                        try {
                            Request request = snapshot.getValue(Request.class);
                            if (request != null && target.equals(request.getListing())) {
                                String requestId = snapshot.getKey();
                                request.setId(requestId);
                                requests.add(request);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing request for snapshot: " + snapshot.getKey(), e);
                        }
                    } else {
                        Log.w(TAG, "Unexpected data type for request at snapshot: " + snapshot.getKey() + ", type: " + snapshot.getValue().getClass().getSimpleName());
                    }
                }

                callback.results.put("requests", requests);
                callback.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void getRequestsByRenter(Renter target, QueryCallback callback) {
        Query query = dbRef.child("requests");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Request> requests = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Check if the snapshot value is not null and is of the expected type
                    if (snapshot.getValue() instanceof Map) {
                        try {
                            Request request = snapshot.getValue(Request.class);
                            if (request != null && target.equals(request.getRenter())) {
                                String requestId = snapshot.getKey();
                                request.setId(requestId);
                                requests.add(request);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing request for snapshot: " + snapshot.getKey(), e);
                        }
                    } else {
                        Log.w(TAG, "Unexpected data type for request at snapshot: " + snapshot.getKey() + ", type: " + snapshot.getValue().getClass().getSimpleName());
                    }
                }

                callback.results.put("requests", requests);
                callback.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void getRequestCountByLessor(Lessor target, QueryCallback callback) {
        Query query = dbRef.child("requests");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int requestCount = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Check if the snapshot value is not null and is of the expected type
                    if (snapshot.getValue() instanceof Map) {
                        try {
                            Request request = snapshot.getValue(Request.class);
                            if (request != null && request.getStatus() == 0 && request.getListing() != null &&
                                    target.equals(request.getListing().getLessor())) {
                                requestCount++;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing request for snapshot: " + snapshot.getKey(), e);
                        }
                    } else {
                        Log.w(TAG, "Unexpected data type for request at snapshot: " + snapshot.getKey() + ", type: " + snapshot.getValue().getClass().getSimpleName());
                    }
                }

                // Return the count through the callback
                callback.results.put("requestCount", requestCount);
                callback.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void deleteRequest(Request request) {
        dbRef.child("requests").child(request.getId()).removeValue();
    }



}
