package com.example.mobileapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddReviewFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;

    String reviewedUserId;

    public AddReviewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        ImageView profilePic = view.findViewById(R.id.profilePicReview);
        TextView usernameText = view.findViewById(R.id.usernameReview);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText commentInput = view.findViewById(R.id.commentInput);
        MaterialButton submitButton = view.findViewById(R.id.submitReviewButton);

        // get reviewedUserId from arguments
        if (getArguments() != null) {
            reviewedUserId = getArguments().getString("reviewedUserId");
        }

        // load username and avatar of the reviewed user
        if (reviewedUserId != null) {
            db.collection("users")
                    .document(reviewedUserId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String avatarId = documentSnapshot.getString("avatarId");

                            usernameText.setText(username);

                            // optionally load avatar image from resources based on avatarId
                            int avatarResId = getResources().getIdentifier(
                                    avatarId, "drawable", getContext().getPackageName());
                            if (avatarResId != 0) {
                                profilePic.setImageResource(avatarResId);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to load user info", Toast.LENGTH_SHORT).show();
                    });
        }

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String comment = commentInput.getText().toString().trim();

            if (rating == 0 || TextUtils.isEmpty(comment)) {
                Toast.makeText(getContext(), "Please provide rating and comment", Toast.LENGTH_SHORT).show();
                return;
            }

            String reviewerId = auth.getCurrentUser().getUid();
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("reviewedUserId", reviewedUserId);
            reviewData.put("reviewerId", reviewerId);
            reviewData.put("rating", rating);
            reviewData.put("comment", comment);
            reviewData.put("timestamp", System.currentTimeMillis());

            db.collection("reviews")
                    .add(reviewData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Review submitted", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(AddReviewFragment.this).popBackStack();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
