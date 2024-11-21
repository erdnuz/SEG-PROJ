package com.example.rentify.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentify.R;
import com.example.rentify.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private final UserClickListener userClickListener; // Callback interface for click events

    public UserAdapter(List<User> userList, UserClickListener userClickListener) {
        this.userList = userList;
        this.userClickListener = userClickListener; // Initialize the click listener
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.fullName());
        holder.userId.setText(user.getId());
        holder.userRole.setText(user.getRole());

        // Set an onClickListener for each user item
        holder.itemView.setOnClickListener(v -> {
            // Invoke the callback with the clicked user
            userClickListener.onUserClick(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void update(List<User> newUsers) {
        this.userList.clear(); // Clear existing data
        this.userList.addAll(newUsers); // Add new data
        notifyDataSetChanged(); // Notify the adapter of data changes
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userId;
        TextView userRole;
        TextView userName;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userId = itemView.findViewById(R.id.userId);
            userRole = itemView.findViewById(R.id.userRole);
            userName = itemView.findViewById(R.id.userName);
        }
    }

    // Callback interface for user clicks
    public interface UserClickListener {
        void onUserClick(User user);
    }
}
