package com.example.mobileapp;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobMapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    private FirebaseFirestore db;
    private Map<Marker, Job> markerJobMap = new HashMap<>();
    private Map<Marker, Long> lastClickTimeMap = new HashMap<>(); // for double tap

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_map, container, false);

        mapView = view.findViewById(R.id.mapView);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        googleMap = gMap;
        fetchJobsAndAddMarkers();
    }

    private void fetchJobsAndAddMarkers() {

        long startTime = System.currentTimeMillis(); // start timer
        db.collection("jobs")
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        Toast.makeText(getContext(), "Failed to load jobs", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    boolean hasLocation = false;

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String title = doc.getString("title");
                        String description = doc.getString("description");
                        double payment = doc.getDouble("payment") != null ? doc.getDouble("payment") : 0;
                        String location = doc.getString("location");
                        String postedBy = doc.getString("postedBy");
                        String postedByUid = doc.getString("userId");
                        String imageUrl = doc.getString("imageUrl");
                        double lat = doc.getDouble("locationLat") != null ? doc.getDouble("locationLat") : 0;
                        double lng = doc.getDouble("locationLng") != null ? doc.getDouble("locationLng") : 0;

                        Job job = new Job(title, description, payment, location, "General",
                                imageUrl, postedBy, postedByUid, lat, lng);

                        if (lat != 0 && lng != 0) {
                            LatLng position = new LatLng(lat, lng);
                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(position)
                                    .title(title) // show job title only
                            );
                            markerJobMap.put(marker, job);
                            boundsBuilder.include(position);
                            hasLocation = true;
                        }
                    }

                    if (hasLocation) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 200));
                    }

                    long endTime = System.currentTimeMillis(); // end timer
                    long duration = endTime - startTime; // duration in ms
                    Log.d("PerformanceTest", "Map loaded in: " + duration + " ms");


                    // marker click listener for single tap info and double tap detail
                    googleMap.setOnMarkerClickListener(marker -> {
                        long now = SystemClock.elapsedRealtime();
                        long lastClick = lastClickTimeMap.containsKey(marker) ? lastClickTimeMap.get(marker) : 0;

                        if (now - lastClick < 500) { // double tap detected
                            Job clickedJob = markerJobMap.get(marker);
                            if (clickedJob != null) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("job", clickedJob);
                                Navigation.findNavController(requireView())
                                        .navigate(R.id.action_jobMap_to_jobDetail, bundle);
                            }
                            lastClickTimeMap.put(marker, 0L);
                        } else { // single tap shows info window
                            marker.showInfoWindow();
                            lastClickTimeMap.put(marker, now);
                        }
                        return true; // consume the click
                    });
                });
    }

    @Override
    public void onResume() { super.onResume(); mapView.onResume(); }
    @Override
    public void onStart() { super.onStart(); mapView.onStart(); }
    @Override
    public void onStop() { super.onStop(); mapView.onStop(); }
    @Override
    public void onPause() { mapView.onPause(); super.onPause(); }
    @Override
    public void onDestroy() { mapView.onDestroy(); super.onDestroy(); }
    @Override
    public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }
}
