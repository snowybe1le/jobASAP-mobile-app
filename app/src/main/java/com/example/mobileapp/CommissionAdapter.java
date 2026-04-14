package com.example.mobileapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommissionAdapter extends RecyclerView.Adapter<CommissionAdapter.ViewHolder> {

    private List<CommissionItem> items;

    public CommissionAdapter(List<CommissionItem> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobName;
        TextView commissionAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobName = itemView.findViewById(R.id.textJobName);
            commissionAmount = itemView.findViewById(R.id.textCommissionAmount);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_commission, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommissionItem item = items.get(position);
        holder.jobName.setText(item.getJobName());
        holder.commissionAmount.setText("RM" + item.getAmount());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
