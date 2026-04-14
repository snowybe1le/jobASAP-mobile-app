package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class JobOfferingFragment extends Fragment {

    private EditText jobTitleEditText, descriptionEditText, priceEditText;
    private Button postButton, pickLocationButton;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private LatLng selectedLatLng; // store picked location
    private static final int REQUEST_PICK_LOCATION = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offering_job, container, false);

        // initialize views
        jobTitleEditText = view.findViewById(R.id.job_title);
        descriptionEditText = view.findViewById(R.id.description);
        priceEditText = view.findViewById(R.id.price);
        postButton = view.findViewById(R.id.post_button);
        pickLocationButton = view.findViewById(R.id.pick_location_button);

        // firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // pick location button
        pickLocationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PickLocationActivity.class);
            startActivityForResult(intent, REQUEST_PICK_LOCATION);
        });

        // post button
        postButton.setOnClickListener(v -> postJobOffering());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_LOCATION && resultCode == getActivity().RESULT_OK && data != null) {
            double lat = data.getDoubleExtra("lat", 0);
            double lng = data.getDoubleExtra("lng", 0);
            selectedLatLng = new LatLng(lat, lng);

            // update button text so user sees a location was picked
            pickLocationButton.setText("Location selected");
        }
    }

    private void postJobOffering() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            return;
        }

        String jobTitle = jobTitleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();

        if (jobTitle.isEmpty() || description.isEmpty() || priceStr.isEmpty() || selectedLatLng == null) {
            Toast.makeText(getContext(), "Please fill all fields and pick a location", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid price", Toast.LENGTH_SHORT).show();
            return;
        }

        // create job object
        Map<String, Object> job = new HashMap<>();
        job.put("title", jobTitle);
        job.put("description", description);
        job.put("payment", price);
        job.put("postedBy", user.getEmail());  // save email instead of UID
        job.put("uid", user.getUid());         // optional: keep UID if needed
        job.put("postedAt", FieldValue.serverTimestamp());
        job.put("locationLat", selectedLatLng.latitude);
        job.put("locationLng", selectedLatLng.longitude);

        // push to firestore
        db.collection("jobs")
                .add(job)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(getContext(), "Job posted successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to post job", Toast.LENGTH_SHORT).show());
    }

    private void clearForm() {
        jobTitleEditText.setText("");
        descriptionEditText.setText("");
        priceEditText.setText("");
        selectedLatLng = null;
        pickLocationButton.setText("Pick location on map");
    }
}
