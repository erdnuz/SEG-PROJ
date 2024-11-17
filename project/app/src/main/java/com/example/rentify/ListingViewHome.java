package com.example.rentify;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;
import android.text.TextWatcher;

public class ListingViewHome extends BaseActivity implements ListingAdapter.OnListingClickListener, CategoryAdapter.CategoryClickListener {
    private static final String TAG = "ListingViewHome"; // For logging
    private List<Category> categories, filterCategories;
    private CategoryAdapter categoryAdapterManage;
    private ListingAdapter listingAdapter;
    private ArrayAdapter<Category> spinnerAdapter;
    private List<Listing> listings = new ArrayList<>();
    private List<Listing> allListings = new ArrayList<>();
    private Button getAllUsers;
    private Category dummy;
    private Category selectedCategory;

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


        // Initialize listings and adapter with an empty list
        listings = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.listingsRecyclerView);
        listingAdapter = new ListingAdapter(listings, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listingAdapter);

        dummy = new Category("Any category", "");



        categories = new ArrayList<>();

        filterCategories = new ArrayList<>();
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filterCategories
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        fetchCategories();
        selectedCategory = dummy;
        updateListingList();

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected category
                selectedCategory = (Category) parent.getItemAtPosition(position);

                // Log the selected category and update the listing
                Log.d(TAG, "Selected category: " + selectedCategory.getName());
                filter(selectedCategory,
                        null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle cases where no category is selected (optional)
                Log.d(TAG, "No category selected.");
            }
        });


        getAllUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, AllUsersActivity.class);
            startActivity(intent);
        });

        EditText searchQuery = findViewById(R.id.searchQuery);

// Set a listener to handle text changes
        searchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int after) {
                // Optional: Handle text before change if needed (e.g., track what was deleted)
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                // Only filter listings when a character is typed (not empty)
                String searchText = searchQuery.getText().toString().trim();
                if (!searchText.isEmpty()) {
                    filter(selectedCategory, searchText);
                } else {
                    filter(selectedCategory, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // After text has been changed (useful for additional actions)
            }
        });


    }

    protected void updateListingList() {
        db.getAllListings(new QueryCallback() {
            @Override
            public void onSuccess() {

                allListings = (List<Listing>) results.get("listings");

                listingAdapter.updateList(allListings);


                Log.d(TAG, "Got listings: "+ allListings.size()); // Notify adapter of the change
            }

            @Override
            public void onError(Exception err) {// Clear the data list on error
                Log.e(TAG, "Listing fetch error: " +err);
                listingAdapter.updateList(allListings);// Notify adapter of the cleared data
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

        categoryAdapterManage = new CategoryAdapter(categories, category -> editCategory(category));

        // Set up RecyclerView for categories in dialog
        categoriesList.setLayoutManager(new LinearLayoutManager(this));
        categoriesList.setAdapter(categoryAdapterManage);

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
                        categoryAdapterManage.notifyItemInserted(categories.size() - 1);
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
                categoryAdapterManage.notifyDataSetChanged(); // Update the RecyclerView
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

    private void filter(Category category, String search) {
        listingAdapter.updateList(filterListings(allListings, category, search));
    }



    /**
     * Fetches categories from the database and updates the category list.
     */
    private void fetchCategories() {
        db.getCategories(new QueryCallback() {
            @Override
            public void onSuccess() {
                categories.clear();
                filterCategories.clear();
                filterCategories.add(dummy);
                categories.addAll((List<Category>) results.get("categories"));
                filterCategories.addAll((List<Category>) results.get("categories"));
                if (categoryAdapterManage!=null) {
                    categoryAdapterManage.notifyDataSetChanged();
                }

                spinnerAdapter.notifyDataSetChanged();

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
                                categoryAdapterManage.notifyItemRemoved(position); // Notify that an item was removed
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


    public List<Listing> filterListings(List<Listing> listings, Category category, String search) {

        // Make a copy of the listings list to avoid modifying the original list
        ArrayList<Listing> filtered = new ArrayList<>(listings);
        // Log the size of the original list
        Log.d("filterListings", "Original list size: " + listings.size());

        // Perform filtering
        filtered.removeIf(l -> {
            boolean match;
            if (category == dummy) {
                match = l.isMatch(null, search);
            } else {
                match = l.isMatch(category, search);
            }
            return !match;
        });


        // Log the size of the filtered list
        Log.d("filterListings", "Filtered list size: " + filtered.size());

        return filtered;
    }
}
