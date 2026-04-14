package com.example.mobileapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FindJob extends Fragment {

    private EditText searchEditText;
    private EditText mapSearchEditText;
    private ImageView mapImageView;

    public FindJob() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle back button
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                } else {
                    requireActivity().finish();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_findjob, container, false);

        initializeViews(view);
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        searchEditText = view.findViewById(R.id.search);
        mapSearchEditText = view.findViewById(R.id.map_search);
        mapImageView = view.findViewById(R.id.map);

    }

    private void setupListeners() {

        // Map search
        mapImageView.setOnClickListener(v -> {
            String locationQuery = mapSearchEditText.getText().toString().trim();
            if (locationQuery.isEmpty()) locationQuery = "nearby jobs";

            Bundle bundle = new Bundle();
            bundle.putString("location", locationQuery);

            Navigation.findNavController(v)
                    .navigate(R.id.action_findJob_to_jobMap, bundle);
        });


    }

    private void navigateToHome(String category) {
        Bundle bundle = new Bundle();
        bundle.putString("categoryFilter", category);

        Navigation.findNavController(requireView())
                .navigate(R.id.action_findJob_to_homeFragment, bundle);
    }
}
