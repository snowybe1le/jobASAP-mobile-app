package com.example.mobileapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    ArrayList<Review> list;

    public ReviewAdapter(ArrayList<Review> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review r = list.get(position);

        // set rating and comment as before
        holder.rating.setText("Rating: " + r.rating);
        holder.comment.setText(r.comment);

        // fetch reviewer info from Firestore
        // fetch reviewer info
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(r.reviewerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String username = documentSnapshot.getString("username");
                        String avatarId = documentSnapshot.getString("avatarId");

                        holder.reviewerName.setText(username);

                        int avatarResId = holder.itemView.getContext()
                                .getResources()
                                .getIdentifier(avatarId, "drawable", holder.itemView.getContext().getPackageName());
                        holder.reviewerAvatar.setImageResource(avatarResId);
                    }
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewerName, rating, comment;
        ImageView reviewerAvatar;

        ViewHolder(View v) {
            super(v);
            reviewerAvatar = v.findViewById(R.id.reviewerAvatar); // bind the new ImageView
            reviewerName = v.findViewById(R.id.reviewerName);
            rating = v.findViewById(R.id.rating);
            comment = v.findViewById(R.id.comment);
        }
    }
}
