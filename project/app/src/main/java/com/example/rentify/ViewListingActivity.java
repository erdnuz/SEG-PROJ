package com.example.rentify;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.example.rentify.models.Admin;
import com.example.rentify.models.Category;
import com.example.rentify.models.Lessor;
import com.example.rentify.models.Renter;
import com.example.rentify.util.DatabaseHelper;
import com.example.rentify.models.Listing;
import com.example.rentify.util.QueryCallback;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewListingActivity extends BaseActivity {
    private static Listing listing;
    private LinearLayout manageControls;
    private Button deleteListing, editListing, viewRequests, makeRequest, startDateButton, endDateButton;
    private TextView titleView, categoryView, priceView, descriptionView, lessorView, availabilityView;
    private Uri selectedImageUri;
    private ImageView image;
    private Long startDate, endDate;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    selectedImageUri = uri;// Get the URI of the selected image
                    image.setImageURI(selectedImageUri); // Set the image URI in the ImageView
                    Log.d(TAG, "Image selected: " + selectedImageUri.toString());
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });

    public static void setListing(Listing listing) {
        ViewListingActivity.listing = listing;
    }

    public static void clearListing() {
        ViewListingActivity.listing = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listing_view);
        setUpToolbar();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        manageControls = findViewById(R.id.manageControls);
        deleteListing = findViewById(R.id.deleteListingButton);
        editListing = findViewById(R.id.editListingButton);
        titleView = findViewById(R.id.titleView);
        categoryView = findViewById(R.id.categoryView);
        priceView = findViewById(R.id.priceView);
        descriptionView = findViewById(R.id.dateView);
        lessorView = findViewById(R.id.lessorView);
        image = findViewById(R.id.listingImageView);
        makeRequest = findViewById(R.id.makeRequest);
        viewRequests = findViewById(R.id.viewRequests);
        availabilityView = findViewById(R.id.availabilityTextView);

        loadVisibility();
        updateData();
        setupButtonListeners();
        setUpListing(image, listing, true);
    }

    private void loadVisibility() {
        if (listing.getLessor().equals(currentUser)) {
            manageControls.setVisibility(View.VISIBLE);
            editListing.setVisibility(View.VISIBLE);
            viewRequests.setVisibility(View.VISIBLE);
            makeRequest.setVisibility(View.GONE);
        } else if ("ADMIN".equals(currentUser.getRole())) {
            manageControls.setVisibility(View.VISIBLE);
            editListing.setVisibility(View.GONE);
            viewRequests.setVisibility(View.GONE);
            makeRequest.setVisibility(View.GONE);// Hide edit button for ADMIN
        } else {
            manageControls.setVisibility(View.GONE);
            viewRequests.setVisibility(View.GONE);
            makeRequest.setVisibility(View.VISIBLE);
        }
    }

    private void updateData() {
        startDate = listing.getStartDate();
        endDate = listing.getEndDate();
        titleView.setText(listing.getTitle());
        categoryView.setText(listing.getCategory().getName());
        priceView.setText(String.format("$%.2f", listing.getPrice()));
        descriptionView.setText(listing.getDescription());
        lessorView.setText(listing.getLessor().fullName());
        availabilityView.setText(listing.getStartFormatted()+" - " +  listing.getEndFormatted());// Assuming Lessor has a getName method
    }

    private void setupButtonListeners() {
        deleteListing.setOnClickListener(v -> {
            // Handle delete listing logic
            deleteListing();
        });

        editListing.setOnClickListener(v -> {
            // Handle edit listing logic
            editListing();
        });

        lessorView.setOnClickListener(v -> {
            ProfileActivity.setIntentUser(listing.getLessor()); // Set the user data for the profile
            Intent intent = new Intent(this, ProfileActivity.class); // Create the Intent
            startActivity(intent); // Start ProfileActivity
        });

        makeRequest.setOnClickListener(v -> {
            // Handle edit listing logic
            makeRequest();
        });

        viewRequests.setOnClickListener(v -> {
            RequestsActivity.setIntentListing(listing);
            Intent intent = new Intent(this, RequestsActivity.class); // Create the Intent
            startActivity(intent);
        });

    }

    private void makeRequest() {
        if (!currentUser.getEnabled()) {
            logToast('d', "Account is disabled.");
            return;
        }
        Renter renter = (Renter) currentUser;
        renter.createRequest(listing, new QueryCallback() {

            @Override
            public void onSuccess() {
                logToast('d', "Request created");
                finish();
            }

            @Override
            public void onError(Exception err) {
                logToast('d', "You still have a pending request");
            }
        });
    }

    private void deleteListing() {
        if (!currentUser.getEnabled()) {
            logToast('d', "Account is disabled.");
            return;
        } if (currentUser instanceof Admin) {
            Admin admin = (Admin)currentUser;
            admin.deleteListing(listing);
        } else {
            Lessor lessor = (Lessor)currentUser;
            lessor.deleteListing(listing);
        }

        finish();
    }

    private void showDatePicker(boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();

        // Create a DatePickerDialog limited to today or later
        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);

                    // Convert the selected date to YYYYMMDD format only after selection
                    SimpleDateFormat longFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                    String formattedDate = longFormat.format(calendar.getTime());
                    long dateAsLong = Long.parseLong(formattedDate);

                    if (isStartDate) {
                        startDate = dateAsLong;
                        startDateButton.setText(displayFormat.format(calendar.getTime()));
                    } else {
                        endDate = dateAsLong;
                        endDateButton.setText(displayFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set min and max dates directly using milliseconds
        if (isStartDate) {
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            if (endDate != null) {
                datePicker.getDatePicker().setMaxDate(convertToMillis(endDate));
            }
        } else {
            if (startDate != null) {
                datePicker.getDatePicker().setMinDate(convertToMillis(startDate));
            } else {
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            }
        }

        datePicker.show();
    }

    // Helper to convert YYYYMMDD to milliseconds
    private long convertToMillis(long yyyymmddDate) {
        int year = (int) (yyyymmddDate / 10000);
        int month = (int) ((yyyymmddDate % 10000) / 100) - 1;
        int day = (int) (yyyymmddDate % 100);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private void editListing() {
        if (!currentUser.getEnabled()) {
            logToast('d', "Account is disabled.");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.edit_listing_dialog, null);
        builder.setView(dialogView);

        // Initialize dialog inputs
        TextView header = dialogView.findViewById(R.id.editHeader);
        EditText titleInput = dialogView.findViewById(R.id.titleInput);
        EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);
        EditText priceInput = dialogView.findViewById(R.id.priceInput);

        Button uploadImageButton = dialogView.findViewById(R.id.uploadImageButton);
        Button submitButton = dialogView.findViewById(R.id.submitButton);
        ImageView imagePreview = dialogView.findViewById(R.id.imagePreview);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        startDateButton = dialogView.findViewById(R.id.startDateButton);
        endDateButton = dialogView.findViewById(R.id.endDateButton);

        // Pre-fill the dialog with the existing listing values
        header.setText("Edit listing");
        titleInput.setText(listing.getTitle());
        descriptionInput.setText(listing.getDescription());
        priceInput.setText(String.valueOf(listing.getPrice()));
        submitButton.setText("Confirm");
        setUpListing(imagePreview, listing, false);
        startDateButton.setText(listing.getStartFormatted());
        endDateButton.setText(listing.getEndFormatted());
        // Assuming you have a method to set the spinner selection based on the listing category

        // Fetch categories from the database and set up the spinner
        DatabaseHelper db = DatabaseHelper.getInstance();
        db.getCategories(new QueryCallback() {
            @Override
            public void onSuccess() {
                List<Category> categories = (List<Category>) results.get("categories");
                if (categories != null) {
                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(ViewListingActivity.this, android.R.layout.simple_spinner_item, categories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                    categorySpinner.setSelection(categories.indexOf(listing.getCategory()));
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

        startDateButton.setOnClickListener(v -> {
            try {
                showDatePicker(true); // true indicates it's for start date
                Log.d(TAG, "Start date button clicked");
            } catch (Exception e) {
                Log.e(TAG, "Error showing start date picker: " + e.getMessage());
                Toast.makeText(this, "Error selecting start date.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set OnClickListener for End Date Button
        endDateButton.setOnClickListener(v -> {
            try {
                showDatePicker(false); // false indicates it's for end date
                Log.d(TAG, "End date button clicked");
            } catch (Exception e) {
                Log.e(TAG, "Error showing end date picker: " + e.getMessage());
                Toast.makeText(this, "Error selecting end date.", Toast.LENGTH_SHORT).show();
            }
        });

        // Create the dialog
        AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(v -> {
            Log.d(TAG, "Cancel listing edit");
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

            // Update the existing listing object
            listing.setTitle(title);
            listing.setDescription(description);
            listing.setPrice(price);
            listing.setCategory(category);
            listing.setStartDate(startDate);
            listing.setEndDate(endDate);// Assuming you have a setCategory method

            String listingId = listing.getId();
            Log.d(TAG, "Editing listing with ID: " + listingId);
            db.updateListing(listing);
            if (selectedImageUri != null) {
                uploadImageToFirebase(selectedImageUri, listingId);
            } else {
                Toast.makeText(ViewListingActivity.this, "Listing updated without an image.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Listing updated without an image");
            }
            updateData();
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
