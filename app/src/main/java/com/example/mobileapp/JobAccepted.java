package com.example.mobileapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;

import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class JobAccepted extends Fragment {

    private LinearLayout acceptedJobsContainer;
    private ArrayList<Job> acceptedJobsList = new ArrayList<>(); // store accepted jobs

    public JobAccepted() {
        // required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("JobAcceptedDebug", "JobAcceptedFragment created!");
        return inflater.inflate(R.layout.fragment_job_accepted, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        EdgeToEdge.enable(requireActivity());
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        acceptedJobsContainer = view.findViewById(R.id.accepted_jobs_container);

        // get passed jobs from arguments
        if (getArguments() != null) {
            Job job = (Job) getArguments().getSerializable("acceptedJob");
            if (job != null) {
                addJobToContainer(job);
            }
        }

//        // back button
//        view.findViewById(R.id.back_button).setOnClickListener(v ->
//                requireActivity().onBackPressed()
//        );
    }

    private void addJobToContainer(Job job) {
        // create a simple view for each accepted job
        View jobItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_job, acceptedJobsContainer, false);
        TextView jobName = jobItemView.findViewById(R.id.job_name);
        jobName.setText(job.getTitle());
        acceptedJobsContainer.addView(jobItemView);
        acceptedJobsList.add(job);
    }
}
