package com.example.mobileapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class JobDetailFragment extends Fragment {

    private EditText titleEditText, descriptionEditText, priceEditText;
    private TextView postedByTextView;
    private Button locationButton, acceptButton;

    private Job job;

    public JobDetailFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_job_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // normal EditText fields
        titleEditText = view.findViewById(R.id.job_detail_title);
        descriptionEditText = view.findViewById(R.id.job_detail_description);
        priceEditText = view.findViewById(R.id.job_detail_price);

        // postedBy is TextView now
        postedByTextView = view.findViewById(R.id.job_detail_posted_by);

        locationButton = view.findViewById(R.id.job_detail_location);
        acceptButton = view.findViewById(R.id.accept_button);

        if (getArguments() != null) {
            job = (Job) getArguments().getSerializable("job");

            if (job != null) {
                titleEditText.setText(job.getTitle());
                descriptionEditText.setText(job.getDescription());
                priceEditText.setText(String.valueOf(job.getPayment()));
                postedByTextView.setText(job.getPostedBy());
                locationButton.setText(job.getLocation());

                double lat = job.getLat();
                double lng = job.getLng();

                // map button click
                locationButton.setOnClickListener(v -> openMap(lat, lng, job.getLocation()));

                // make email clickable
                postedByTextView.setClickable(true);
                postedByTextView.setFocusable(true);
                postedByTextView.setTextColor(Color.parseColor("#0259DE")); // make it look like a link
                postedByTextView.setPaintFlags(postedByTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                postedByTextView.setOnClickListener(v -> openUserProfile(job.getPostedBy()));
            }
        }

        acceptButton.setOnClickListener(v -> {
            if (job == null) return;

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null) {
                Toast.makeText(getContext(), "You must be logged in to accept a job", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentUserUid = currentUser.getUid();

            // ❌ user trying to accept their own job
            if (job.getUid().equals(currentUserUid)) {
                Toast.makeText(getContext(), "You cannot accept job posted by you", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ allowed to accept
            Bundle bundle = new Bundle();
            bundle.putSerializable("acceptedJob", job);
            Navigation.findNavController(v)
                    .navigate(R.id.action_jobDetail_to_jobAccepted, bundle);
        });


        if (getArguments() != null) {
            job = (Job) getArguments().getSerializable("job");

            if (job != null) {
                // ✅ ADD THIS LOGGING BLOCK
                Log.d("JobDetail", "=== JOB DATA ===");
                Log.d("JobDetail", "Title: " + job.getTitle());
                Log.d("JobDetail", "Posted by: " + job.getPostedBy());
                Log.d("JobDetail", "getUserId(): " + job.getUserId());
                Log.d("JobDetail", "getUid(): " + job.getUid());
                Log.d("JobDetail", "Are they the same? " + (job.getUserId() == job.getUid()));

                // Your existing code...
                titleEditText.setText(job.getTitle());
                descriptionEditText.setText(job.getDescription());
                // etc...
            }
        }

    }

    private void openMap(double lat, double lng, String label) {
        String uri;
        if (lat != 0 && lng != 0) {
            uri = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + Uri.encode(label) + ")";
        } else {
            uri = "geo:0,0?q=" + Uri.encode(label);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Google Maps not installed", Toast.LENGTH_SHORT).show();
        }
    }

    private void openUserProfile(String userEmail) {
        // pass data in a Bundle
        Bundle bundle = new Bundle();
        bundle.putString("userEmail", userEmail);
        bundle.putString("userUid",job.getUid()); // ✅ pass UID too


        // navigate using NavController (must be defined in nav_graph.xml)
        Navigation.findNavController(requireView())
                .navigate(R.id.action_jobDetail_to_otherUserProfileFragment, bundle);
    }
}
