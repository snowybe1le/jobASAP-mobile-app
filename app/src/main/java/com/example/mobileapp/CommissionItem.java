package com.example.mobileapp;

public class CommissionItem {
    private String jobName;
    private double amount;

    public CommissionItem(String jobName, double amount) {
        this.jobName = jobName;
        this.amount = amount;
    }

    public String getJobName() {
        return jobName;
    }

    public double getAmount() {
        return amount;
    }
}
