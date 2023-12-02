package com.example.donna_app;

public class StudentRecord {
    private String dateRecorded;
    private String remarks;
    private int studentId;
    private int violationId;
    private String violationName; // New property
    private int guidanceId;
    private String status;
    private String guidanceName; // New property
    private String studentName; // New property

    public StudentRecord(String dateRecorded, String remarks, String status,  String violationName, String guidanceName, String studentName) {
        this.dateRecorded = dateRecorded;
        this.remarks = remarks;
        this.status = status;
        this.violationName = violationName;
        this.guidanceName = guidanceName;
        this.studentName = studentName;

    }



    public String getDateRecorded() {
        return dateRecorded;
    }

    public String getRemarks() {
        return remarks;
    }
    public String getStatus() {
        return status;
    }

    public String getViolationName() {
        return violationName;
    }
    public String getGuidanceName() {
        return guidanceName;
    }
    public String getStudentName() {
        return studentName;
    }


}
