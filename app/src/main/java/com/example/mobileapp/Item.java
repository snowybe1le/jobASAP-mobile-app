package com.example.mobileapp;

public class Item {
    private String JobApplicant1;
    private String JobApplicant2;
    private String JobApplicant3;
    private String Chat1;
    private String Chat2;
    private String Chat3;

    public Item(String JobApplicant1, String JobApplicant2, String JobApplicant3, String Chat1, String Chat2, String Chat3) {
        this.JobApplicant1 = JobApplicant1;
        this.JobApplicant2 = JobApplicant2;
        this.JobApplicant3 = JobApplicant3;
        this.Chat1 = Chat1;
        this.Chat2 = Chat2;
        this.Chat3 = Chat3;
    }

    public String getJobApplicant1() {
        return JobApplicant1;
    }


    public String getJobApplicant2() {
        return JobApplicant2;
    }

    public String getJobApplicant3() {
        return JobApplicant3;
    }

    public String getChat1() {
        return Chat1;
    }

    public String getChat2() {
        return Chat2;
    }

    public String getChat3() {
        return Chat3;
    }

    public void setDescription(String Chat1) {
        this.Chat1 = Chat1;
    }
}