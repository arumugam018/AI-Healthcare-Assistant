package com.healthcare.model;

public class MedicineReminder {
    private String id;
    private String medicineName;
    private String dosage;
    private String time;
    private String frequency;
    
    public MedicineReminder() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
}
 
