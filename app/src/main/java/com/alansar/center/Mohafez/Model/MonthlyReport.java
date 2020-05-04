package com.alansar.center.Mohafez.Model;

public class MonthlyReport {
    private String id, StudentName, StudentClass, suratStart, suratEnd, Notes;
    private int TotalConservation, ayaStart, ayaEnd, DaysAbsence;
    private double SafahatAlhafz, SafahatAlmurajaea;

    public MonthlyReport(String id, String studentName,
                         String studentClass, String suratStart,
                         String suratEnd, String notes, int totalConservation,
                         int ayaStart, int ayaEnd, int daysAbsence,
                         double safahatAlhafz, double safahatAlmurajaea) {
        this.id = id;
        StudentName = studentName;
        StudentClass = studentClass;
        this.suratStart = suratStart;
        this.suratEnd = suratEnd;
        Notes = notes;
        TotalConservation = totalConservation;
        this.ayaStart = ayaStart;
        this.ayaEnd = ayaEnd;
        DaysAbsence = daysAbsence;
        SafahatAlhafz = safahatAlhafz;
        SafahatAlmurajaea = safahatAlmurajaea;
    }

    public MonthlyReport() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getStudentClass() {
        return StudentClass;
    }

    public void setStudentClass(String studentClass) {
        StudentClass = studentClass;
    }

    public String getSuratStart() {
        return suratStart;
    }

    public void setSuratStart(String suratStart) {
        this.suratStart = suratStart;
    }

    public String getSuratEnd() {
        return suratEnd;
    }

    public void setSuratEnd(String suratEnd) {
        this.suratEnd = suratEnd;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public int getTotalConservation() {
        return TotalConservation;
    }

    public void setTotalConservation(int totalConservation) {
        TotalConservation = totalConservation;
    }

    public int getAyaStart() {
        return ayaStart;
    }

    public void setAyaStart(int ayaStart) {
        this.ayaStart = ayaStart;
    }

    public int getAyaEnd() {
        return ayaEnd;
    }

    public void setAyaEnd(int ayaEnd) {
        this.ayaEnd = ayaEnd;
    }

    public int getDaysAbsence() {
        return DaysAbsence;
    }

    public void setDaysAbsence(int daysAbsence) {
        DaysAbsence = daysAbsence;
    }

    public double getSafahatAlhafz() {
        return SafahatAlhafz;
    }

    public void setSafahatAlhafz(double safahatAlhafz) {
        SafahatAlhafz = safahatAlhafz;
    }

    public double getSafahatAlmurajaea() {
        return SafahatAlmurajaea;
    }

    public void setSafahatAlmurajaea(double safahatAlmurajaea) {
        SafahatAlmurajaea = safahatAlmurajaea;
    }
}
