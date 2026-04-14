package com.example.mobileapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ReviewListFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;

    ArrayList<Review> reviewList = new ArrayList<>();
    ReviewAdapter adapter;
    String uidToReview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FloatingActionButton addReviewButton = view.findViewById(R.id.addReviewButton);



        RecyclerView recyclerView = view.findViewById(R.id.reviewRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ReviewAdapter(reviewList);
        recyclerView.setAdapter(adapter);

        // get reviewed user id from arguments, default to current user
        if (getArguments() != null) {
            uidToReview = getArguments().getString("reviewedUserId", auth.getCurrentUser().getUid());
        } else {
            uidToReview = auth.getCurrentUser().getUid();
        }

        // fetch reviews
        db.collection("reviews")
                .whereEqualTo("reviewedUserId", uidToReview)
                .addSnapshotListener((value, error) -> {
                    if (value == null) return;

                    reviewList.clear();
                    value.forEach(doc -> {
                        Review r = doc.toObject(Review.class);
                        reviewList.add(r);
                    });
                    adapter.notifyDataSetChanged();
                });

        addReviewButton.bringToFront();
        // add "Add Review" button programmatically (only for other users)
        // only show add review button if reviewing another user
        if (!uidToReview.equals(auth.getCurrentUser().getUid())) {
            addReviewButton.setVisibility(View.VISIBLE);
            addReviewButton.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("reviewedUserId", uidToReview);
                NavHostFragment.findNavController(ReviewListFragment.this)
                        .navigate(R.id.action_reviewListFragment_to_addReviewFragment, bundle);
            });
        } else {
            addReviewButton.setVisibility(View.GONE);
        }

    }
}
