package edu.cmu.smartphone.telemedicine.entities;

public class Doctor {
    private String userID;
    private String department;
    private String title;
    
    public String getUserID() {
        return userID;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
}

