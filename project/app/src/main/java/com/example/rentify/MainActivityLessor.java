package com.example.rentify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Import Log for logging
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentify.adapters.ListingAdapter;
import com.example.rentify.models.Category;
import com.example.rentify.models.Lessor;
import com.example.rentify.models.Listing;
import com.example.rentify.util.QueryCallback;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivityLessor handles the user interface for Lessors in the Rentify application.
 * It allows Lessors to create listings and provides admin controls for managing users.
 */
public class MainActivityLessor extends BaseActivity implements ListingAdapter.OnListingClickListener {

    private static final String TAG = "MainActivityLessor"; // Tag for logging

    private LinearLayout createListingLayout;
    private Button cancelButton, submitButton, uploadImageButton;
    private TextView numberOfListings, numberOfRequests;
    private static Lessor intentUser;
    private Uri selectedImageUri;
    private ImageView imagePreview;
    private Spinner categorySpinner;
    private List<Listing> listings;
    private ListingAdapter listingAdapter;

    public static void setIntentUser(Lessor intent) {
        intentUser = intent;
    }

    public static void clearIntentUser() {
        intentUser = null;
    }
    // Declare the ActivityResultLauncher as a member variable

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    selectedImageUri = uri;// Get the URI of the selected image
                    imagePreview.setImageURI(selectedImageUri); // Set the image URI in the ImageView
                    Log.d(TAG, "Image selected: " + selectedImageUri.toString());
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge layout
        setContentView(R.layout.activity_main_lessor);
        setUpToolbar();
        selectedImageUri = null; // Initialize selected image URI to null

        // Initialize the layouts and buttons
        createListingLayout = findViewById(R.id.createListingLayout);
        numberOfListings = findViewById(R.id.numberOfListings);
        numberOfRequests = findViewById(R.id.numberOfRequests);

        // Set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "Current user: " + currentUser);
        Log.d(TAG, "Intent user: " + intentUser);

        // Determine visibility based on user type
        if (currentUser != null && intentUser!=null && currentUser.getEmail().equals(intentUser.getEmail())) {
            createListingLayout.setVisibility(View.VISIBLE);
            createListingLayout.setOnClickListener(v -> {
                Log.d(TAG, "Creating listing dialog opened");
                showCreateListingDialog((Lessor) currentUser);
            });
        } else {
            createListingLayout.setVisibility(View.GONE);
        }

        RecyclerView listingsRecyclerView = findViewById(R.id.listingsRecyclerView);
        listingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize listings and adapter with an empty list
        listings = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.listingsRecyclerView);
        listingAdapter = new ListingAdapter(listings, this);
        recyclerView.setAdapter(listingAdapter);
        listingsRecyclerView.setAdapter(listingAdapter);

        // Fetch all listings and update the adapter on success
        updateListingList();
    }

    @Override
    public void onListingClick(Listing listing) {
        // Handle the Listing object here, such as passing it to a new activity
        ViewListingActivity.setListing(listing);
        Intent intent = new Intent(this, ViewListingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateListingList();
    }

    protected void updateListingList() {
        db.getListingsLessor(intentUser, new QueryCallback() {
            @Override
            public void onSuccess() {

                listings.clear();
                listings.addAll( (List<Listing>) results.get("listings"));
                Log.d(TAG, "Got listings: "+ listings.size());
                numberOfListings.setText(String.valueOf(listings.size()));
                listingAdapter.notifyDataSetChanged(); // Notify adapter of the change
            }

            @Override
            public void onError(Exception err) {
                listings.clear(); // Clear the data list on error
                Log.e(TAG, "Listing fetch error: " +err);
                listingAdapter.notifyDataSetChanged(); // Notify adapter of the cleared data
            }
        });
    }


    /**
     * Displays a dialog for creating a new listing.
     *
     * @param lessor The Lessor creating the listing.
     */
    private void showCreateListingDialog(Lessor lessor) {
        if (!lessor.getEnabled()) {
            logToast('d', "Account is disabled.");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.edit_listing_dialog, null);
        builder.setView(dialogView);

        // Initialize dialog inputs
        EditText titleInput = dialogView.findViewById(R.id.titleInput);
        EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);
        EditText priceInput = dialogView.findViewById(R.id.priceInput);
        uploadImageButton = dialogView.findViewById(R.id.uploadImageButton);
        submitButton = dialogView.findViewById(R.id.submitButton);
        imagePreview = dialogView.findViewById(R.id.imagePreview);
        cancelButton = dialogView.findViewById(R.id.cancelButton);
        categorySpinner = dialogView.findViewById(R.id.categorySpinner);

        db.getCategories(new QueryCallback() {
            @Override
            public void onSuccess() {
                List<Category> categories = (List<Category>) results.get("categories");
                if (categories != null) {

                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(MainActivityLessor.this, android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);

                    Log.d(TAG, "Set category spinner: " + categories);
                } else {
                    Log.e(TAG, "No categories found in the database.");
                }
            }

            @Override
            public void onError(Exception err) {
                Log.e(TAG, "Categories search error.");
            }
        });


        // Handle image upload
        uploadImageButton.setOnClickListener(v -> {
            Log.d(TAG, "Opening image chooser");
            openImageChooser();
        });



        // Create the dialog
        AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(v -> {
            Log.d(TAG, "Cancel listing create");
            dialog.dismiss();
        });

        // Handle the submit button click
        submitButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String priceStr = priceInput.getText().toString().trim();

            // Validation checks
            if (TextUtils.isEmpty(title)) {
                titleInput.setError("Title is required.");
                return;
            }
            if (TextUtils.isEmpty(description)) {
                descriptionInput.setError("Description is required.");
                return;
            }
            if (TextUtils.isEmpty(priceStr)) {
                priceInput.setError("Price is required.");
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                priceInput.setError("Please enter a valid price.");
                return;
            }

            Category category = (Category) categorySpinner.getSelectedItem();

            Listing listing = lessor.createListing(title, description, category, price);

            String listingId = listing.getId();
            Log.d(TAG, "Creating listing with ID: " + listingId);

            if (selectedImageUri != null) {
                uploadImageToFirebase(selectedImageUri, listingId);
            } else {
                Toast.makeText(MainActivityLessor.this, "Listing created without an image.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Listing created without an image");
            }
            dialog.dismiss();
        });


        dialog.show();
    }

    /**
     * Opens an image chooser to select an image from the gallery.
     */
    private void openImageChooser() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.VideoOnly.INSTANCE)
                .build());
    }

    /**
     * Uploads the selected image to Firebase Storage.
     *
     * @param imageUri  The URI of the image to upload.
     * @param listingId The ID of the listing associated with the image.
     */
    private void uploadImageToFirebase(Uri imageUri, String listingId) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("listings");
        StorageReference fileRef = storageRef.child(listingId + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        // Log the image upload success
                        Log.d(TAG, "Image uploaded successfully: " + downloadUri.toString());
                        // You can store the download URL in your database here
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Upload failed: " + e.getMessage()); // Log the error
                });
    }
}
