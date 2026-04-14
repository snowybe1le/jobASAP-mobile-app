package com.example.mobileapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private List<Job> jobList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // RecyclerView setup
        recyclerView = view.findViewById(R.id.jobs_button_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        jobList = new ArrayList<>();
        jobAdapter = new JobAdapter(jobList);
        recyclerView.setAdapter(jobAdapter);

        // Offer Job button
        MaterialButton offerJobButton = view.findViewById(R.id.offer_job_button);
        offerJobButton.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_to_offerJob)
        );

        // SearchView click
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setFocusable(false);
        searchView.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_to_FindJob)
        );

        // fetch jobs from Firestore
        fetchJobsFromFirestore();
    }

    private void fetchJobsFromFirestore() {

        long startTime = System.currentTimeMillis(); // start timer
        db.collection("jobs")
                .orderBy("postedAt")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        jobList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String location = document.getString("location"); // display name
                            String postedByEmail = document.getString("postedBy"); // now directly stored as email
                            String imageUrl = document.getString("imageUrl"); // optional
                            String uid = document.getString("uid");

                            // convert payment to double
                            double payment = 0;
                            if (document.get("payment") != null) {
                                try {
                                    payment = Double.parseDouble(document.get("payment").toString());
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            // get lat/lng
                            double lat = document.getDouble("locationLat") != null ? document.getDouble("locationLat") : 0;
                            double lng = document.getDouble("locationLng") != null ? document.getDouble("locationLng") : 0;

                            // create Job object
                            Job job = new Job(title, description, payment, location,
                                    "General", imageUrl, postedByEmail, uid, lat, lng);
                            jobList.add(job);
                        }
                        jobAdapter.updateJobs(jobList);

                        // end timer and log duration
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime; // duration in ms
                        Log.d("PerformanceTest", "Jobs loaded in: " + duration + " ms");


                    }
                });


    }

}
