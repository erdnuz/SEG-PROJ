package com.example.rentify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentify.R;
import com.example.rentify.models.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final List<Category> categories;
    private final CategoryClickListener clickListener;

    public interface CategoryClickListener {
        void editCategory(Category category); // Add method for editing a category
    }

    public CategoryAdapter(List<Category> categories, CategoryClickListener clickListener) {
        this.categories = categories;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryDescription.setText(category.getDescription());

        // Set up click listener for the item view
        holder.itemView.setOnClickListener(v -> clickListener.editCategory(category)); // Handle category click
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView categoryDescription;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName); // Ensure this ID matches the XML
            categoryDescription = itemView.findViewById(R.id.categoryDescription); // Ensure this ID matches the XML
        }
    }
}
