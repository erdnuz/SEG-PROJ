package com.example.rentify.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentify.R;
import com.example.rentify.models.Listing;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {
    private static final String TAG = "ListingAdapter"; // Tag for logging
    private final List<Listing> listings;
    private final OnListingClickListener onListingClickListener;

    public ListingAdapter(List<Listing> listings, OnListingClickListener onListingClickListener) {
        this.listings = listings;
        this.onListingClickListener = onListingClickListener;
    }

    public void updateList(List<Listing> newListings) {
        listings.clear();
        listings.addAll(newListings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listing_item, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        Listing listing = listings.get(position);

        holder.titleTextView.setText(listing.getTitle());
        holder.descriptionTextView.setText(listing.getDescription());

        // Set click listener to return the Listing object
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Listing clicked: " + listing.getTitle());
            onListingClickListener.onListingClick(listing);
        });
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public interface OnListingClickListener {
        void onListingClick(Listing listing); // Callback method to pass Listing
    }

    static class ListingViewHolder extends RecyclerView.ViewHolder {
        final TextView titleTextView;
        final TextView descriptionTextView;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleView);
            descriptionTextView = itemView.findViewById(R.id.dateView);
        }
    }
}
