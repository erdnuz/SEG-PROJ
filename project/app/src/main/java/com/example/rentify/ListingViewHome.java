package com.example.rentify;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentify.adapters.CategoryAdapter;
import com.example.rentify.adapters.ListingAdapter;
import com.example.rentify.models.Admin;
import com.example.rentify.models.Category;
import com.example.rentify.models.Listing;
import com.example.rentify.util.QueryCallback;
import com.example.rentify.models.Slot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ListingViewHome extends BaseActivity implements ListingAdapter.OnListingClickListener, CategoryAdapter.CategoryClickListener {
    private Listing listingReq;
    private static final String TAG = "ListingViewHome"; // For logging
    private List<Category> categories;
    private CategoryAdapter categoryAdapter, categoryAdapterFilter;
    private ListingAdapter listingAdapter;
    private List<Listing> listings = new ArrayList<>();
    private List<Listing> allListings = new ArrayList<>();
    private Long startDate, endDate, filterStart, filterEnd;
    private Button startDateButton, endDateButton, getAllUsers;
    private String filterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge layout
        setContentView(R.layout.activity_listing_home); // Set the content view for the activity

        setUpToolbar();
        // Set visibility for the "Manage Categories" button based on user role
        View manageCategories = findViewById(R.id.manageCategories);
        getAllUsers = findViewById(R.id.getAllUsers);

        Log.d(TAG, "ListingViewHome opened");
        if ("ADMIN".equals(currentUser.getRole())) {
            Log.d(TAG, "Loading admin view");
            getAllUsers.setVisibility(View.VISIBLE);
            manageCategories.setVisibility(View.VISIBLE);
            manageCategories.setOnClickListener(v -> showCategoryDialog(this));
        } else {
            Log.d(TAG, "Loading default view");
            manageCategories.setVisibility(View.GONE);
            getAllUsers.setVisibility(View.GONE);
        }


        // Setup system bar padding for immersive UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView for listings
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

        Button searchButton = findViewById(R.id.searchButton);
        SearchView search = findViewById(R.id.search);
        Slot slot;
        if (startDate!=null&&endDate!=null) {
            slot = new Slot(startDate, endDate);
        } else {slot=null;}
        searchButton.setOnClickListener(v -> {
            Log.d(TAG, "Getting search query");
            String query = search.getQuery().toString().trim();
            listings = db.filterListings(allListings, filterCategory, slot, query);
            listingAdapter.updateList(listings); // Ensure adapter reflects the new list
        });

        // Filter button click listener
        ImageView filterButton = findViewById(R.id.filter);
        filterButton.setOnClickListener(v -> showFilterDialog());

        getAllUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, AllUsersActivity.class);
            startActivity(intent);
        });
    }

    protected void updateListingList() {
        db.getAllListings(new QueryCallback() {
            @Override
            public void onSuccess() {

                listings.clear();
                allListings = (List<Listing>) results.get("listings");
                listings.addAll(allListings);
                Log.d(TAG, "Got listings: "+ listings.size());
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

    /**
     * Displays a dialog to manage categories with the ability to add and delete categories.
     */
    public void showCategoryDialog(Context context) {
        Admin admin = (Admin) db.getCurrentUser();

        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.manage_categories_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Initialize dialog views
        EditText newCategoryEditText = dialogView.findViewById(R.id.newCategory);
        EditText newDescriptionEditText = dialogView.findViewById(R.id.description);
        Button addCategoryButton = dialogView.findViewById(R.id.addCategoryButton);
        RecyclerView categoriesList = dialogView.findViewById(R.id.categoriesList);

        // Initialize categories list and adapter
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categories, category -> editCategory(category));

        // Set up RecyclerView for categories in dialog
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        categoriesList.setAdapter(categoryAdapter);

        // Fetch categories from the database
        fetchCategories();

        // Add category button listener
        addCategoryButton.setOnClickListener(v -> {
            String newCategory = newCategoryEditText.getText().toString().trim();
            String newDescription = newDescriptionEditText.getText().toString().trim();
            if (validateInputs(newCategory, newDescription, newCategoryEditText, newDescriptionEditText)) {
                newCategory = toTitleCase(newCategory);
                Category finalNewCategory = new Category(newCategory, newDescription);
                admin.addCategory(finalNewCategory, new QueryCallback() {
                    @Override
                    public void onSuccess() {
                        // Add the new category to the list and notify the adapter
                        categories.add(finalNewCategory);
                        newCategoryEditText.setText("");
                        newDescriptionEditText.setText("");
                        categoryAdapter.notifyItemInserted(categories.size() - 1); // Notify that a new item was inserted
                    }

                    @Override
                    public void onError(Exception err) {
                        newCategoryEditText.setError(err.getMessage());
                        Log.e(TAG, "Failed to add category: " + err.getMessage());
                    }
                });
            }
        });


        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return the input as is if it's null or empty
        }

        StringBuilder titleCase = new StringBuilder();
        String[] words = input.split(" "); // Split the input into words

        for (String word : words) {
            if (word.length() > 0) {
                // Capitalize the first letter and make the rest lowercase
                String capitalizedWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                titleCase.append(capitalizedWord).append(" "); // Append to the title case string
            }
        }

        return titleCase.toString().trim(); // Trim any trailing spaces
    }


    public void editCategory(Category category) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_category_dialog);

        EditText editCategoryName = dialog.findViewById(R.id.editCategoryName);
        EditText editCategoryDescription = dialog.findViewById(R.id.editCategoryDescription);
        Button saveButton = dialog.findViewById(R.id.saveButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button deleteButton = dialog.findViewById(R.id.deleteCategoryButton);

        // Set existing values
        editCategoryName.setText(category.getName());
        editCategoryDescription.setText(category.getDescription());

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            String newName = editCategoryName.getText().toString().trim();
            String newDescription = editCategoryDescription.getText().toString().trim();

            // Clear previous error messages
            editCategoryName.setError(null);
            editCategoryDescription.setError(null);
            if (validateInputs(newName, newDescription, editCategoryName, editCategoryDescription)) {
                newName = toTitleCase(newName);

                // Input validation
                category.setName(newName);
                category.setDescription(newDescription);
                categoryAdapter.notifyDataSetChanged(); // Update the RecyclerView
                db.updateCategory(category);
                // Save changes to the database if necessary
                dialog.dismiss();
            }// Close the dialog

        });

        // Cancel button click listener
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Delete button click listener
        deleteButton.setOnClickListener(v -> showDeleteConfirmation(this, category, dialog));

        dialog.show();
    }

    // Method to validate inputs and set errors
    private boolean validateInputs(String name, String description, EditText nameField, EditText descriptionField) {
        boolean isValid = true;

        // Check if name is empty
        if (name.isEmpty()) {
            nameField.setError("Category name is required.");
            isValid = false; // Validation failed
        } else if (name.length() < 3) {
            nameField.setError("Name should be at least 3 characters.");
            isValid = false; // Validation failed
        }

        // Check if description is empty
        if (description.isEmpty()) {
            descriptionField.setError("Category description is required.");
            isValid = false; // Validation failed
        } else if (description.length() < 5) {
            descriptionField.setError("Description should be at least 5 characters.");
            isValid = false; // Validation failed
        }

        return isValid; // Return the validation result
    }



    /**
     * Fetches categories from the database and updates the category list.
     */
    private void fetchCategories() {
        db.getCategories(new QueryCallback() {
            @Override
            public void onSuccess() {
                categories.clear();
                categories.addAll((List<Category>) results.get("categories"));
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception err) {
                Log.e(TAG, "Categories search error: " + err.getMessage());
            }
        });
    }

    /**
     * Displays a confirmation dialog to delete a category.
     *
     * @param context  The context of the application.
     * @param category The category to be deleted.
     */
    private void showDeleteConfirmation(Context context, Category category, Dialog dialog) {
        Admin admin = (Admin) db.getCurrentUser();
        new AlertDialog.Builder(context)
                .setMessage("Delete category: " + category.getName() + "?")
                .setPositiveButton("Delete", (d, which) -> {
                    admin.removeCategory(category, new QueryCallback() {
                        @Override
                        public void onSuccess() {
                            // Remove the category from the list and notify the adapter
                            int position = categories.indexOf(category);
                            if (position >= 0) {
                                categories.remove(position);
                                categoryAdapter.notifyItemRemoved(position); // Notify that an item was removed
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(Exception err) {
                            new AlertDialog.Builder(context)
                                    .setTitle("Error")
                                    .setMessage("Failed to delete category: " + err.getMessage())
                                    .setPositiveButton("OK", null)
                                    .show();
                            Log.e(TAG, "Failed to delete category: " + err.getMessage());
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    /**
     * Displays a dialog for filtering listings.
     */
    private void showFilterDialog() {
        // Fetch categories from the database
        filterStart = null;
        filterEnd = null;
        filterCategory = null;

        fetchCategories();
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.filter_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Initialize dialog views
        startDateButton = dialogView.findViewById(R.id.startDateButton);
        endDateButton = dialogView.findViewById(R.id.endDateButton);
        // Spinner and category list setup
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        Log.d(TAG, "Available categories: " + categories);

        List<Category> spinnerCategories = new ArrayList<>();
        Category dummy = new Category("Any category", "");
        spinnerCategories.add(dummy); // Default selection

        ArrayAdapter<Category> categoryAdapterFilter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerCategories) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(spinnerCategories.get(position).getName()); // Set category name
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(spinnerCategories.get(position).getName()); // Set category name
                return view;
            }
        };

        categoryAdapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapterFilter);


        // Log categories after setting up the adapter


        // Set OnClickListener for Start Date Button
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

        // Set OnClickListener for Apply Filter Button
        Button applyFilterButton = dialogView.findViewById(R.id.applyFilterButton);
        applyFilterButton.setOnClickListener(v -> {

            try {
                filterCategory = !categorySpinner.getSelectedItem().equals(dummy) ? categorySpinner.getSelectedItem().toString() : "";
                filterStart = startDate;
                filterEnd = endDate;
                // Log the selected filter category
                Log.d(TAG, "Selected filter category: " + filterCategory);
                dialog.dismiss();
            } catch (Exception e) {
                Log.e(TAG, "Error applying filter: " + e.getMessage());
                Toast.makeText(this, "Error applying filter. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Show the dialog
        dialog.show();
        Log.d(TAG, "Filter dialog shown");
    }
}
