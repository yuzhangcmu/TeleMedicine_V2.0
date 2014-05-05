package edu.cmu.smartphone.telemedicine.entities;

public class Patient {
    private String userID;
    private String Symptom;
    
    public String getUserID () {
        return userID;
    }
    
    public String getSymptom() {
        return Symptom;
    }
    
    public void setSymptom(String symptom) {
       this.Symptom = symptom;  
    }
}
