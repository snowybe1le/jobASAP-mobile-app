package com.example.mobileapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.Navigation;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private List<Job> jobList;

    public JobAdapter(List<Job> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.bind(job);
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public void updateJobs(List<Job> newJobs) {
        this.jobList = newJobs;
        notifyDataSetChanged();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        private TextView jobName;
        private View statusBlock;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobName = itemView.findViewById(R.id.job_name);
            statusBlock = itemView.findViewById(R.id.status_block);
        }

        public void bind(Job job) {
            jobName.setText(job.getTitle());
            statusBlock.setBackgroundResource(R.drawable.status_red); // you can customize

            itemView.setOnClickListener(v -> {
                // pass the full Job object
                Bundle bundle = new Bundle();
                bundle.putSerializable("job", job);

                Navigation.findNavController(v)
                        .navigate(R.id.action_home_to_jobDetail, bundle);
            });
        }
    }
}
