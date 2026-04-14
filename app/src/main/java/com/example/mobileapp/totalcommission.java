package com.example.mobileapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class totalcommission extends Fragment {

    // ... your existing code ...

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_totalcommission, container, false);
    }

    // <-- add this method
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.commissionRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // create dummy data
        List<CommissionItem> data = new ArrayList<>();
        data.add(new CommissionItem("Food Delivery", 5.00));
        data.add(new CommissionItem("Parcel Drop-off", 3.50));
        data.add(new CommissionItem("Grocery Shopping", 8.00));
        data.add(new CommissionItem("Laundry Service", 4.00));
        data.add(new CommissionItem("Dog Walking", 6.50));
        data.add(new CommissionItem("House Cleaning", 7.00));
        data.add(new CommissionItem("Tutoring", 10.00));
        data.add(new CommissionItem("Car Wash", 3.00));
        data.add(new CommissionItem("Gardening", 5.50));
        data.add(new CommissionItem("Babysitting", 9.00));
        data.add(new CommissionItem("Errand Running", 4.50));
        data.add(new CommissionItem("Shopping Assistant", 6.00));
        data.add(new CommissionItem("Tech Support", 8.50));
        data.add(new CommissionItem("Delivery Service", 7.50));
        data.add(new CommissionItem("Photography", 12.00));

        // set adapter
        CommissionAdapter adapter = new CommissionAdapter(data);
        recyclerView.setAdapter(adapter);

        // calculate total commission
        double total = 0;
        for (CommissionItem item : data) {
            total += item.getAmount();
        }

        TextView totalText = view.findViewById(R.id.textTotalCommission);
        totalText.setText(String.valueOf(total));
    }
}
