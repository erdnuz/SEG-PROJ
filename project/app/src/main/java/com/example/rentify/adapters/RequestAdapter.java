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
import com.example.rentify.models.Request;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private static final String TAG = "RequestAdapter"; // Tag for logging
    private final List<Request> requests;
    private final OnRequestClickListener onRequestClickListener;
    private final boolean listingIntent;

    public RequestAdapter(List<Request> requests, OnRequestClickListener onRequestClickListener, boolean listingIntent) {
        this.requests = requests;
        this.onRequestClickListener = onRequestClickListener;
        this.listingIntent = listingIntent;
    }

    public void updateList(List<Request> newRequests) {
        requests.clear();
        requests.addAll(newRequests);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_item, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requests.get(position);

        Listing listing = request.getListing();
        if (listingIntent) {
            holder.titleTextView.setText(request.getRenter().fullName());
        } else {
            holder.titleTextView.setText(listing.getTitle());
        }

        holder.dateTextView.setText(request.getFormattedDate());
        holder.statusTextView.setText( request.getStatus() == 0 ? "Pending": request.getStatus() == -1 ? "Declined":"Approved");

        // Set click listener to return the Listing object
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Listing clicked: " + listing.getTitle());
            onRequestClickListener.onRequestClick(request);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public interface OnRequestClickListener {
        void onRequestClick(Request request); // Callback method to pass Listing
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        final TextView titleTextView;
        final TextView dateTextView;
        final TextView statusTextView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleView);
            dateTextView = itemView.findViewById(R.id.dateView);
            statusTextView = itemView.findViewById(R.id.statusView);
        }
    }
}
