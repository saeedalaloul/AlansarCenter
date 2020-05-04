package com.alansar.center.Mohafez.Model;

public class DailyReport {
    private String id, UID, idGroup, suratStart, suratEnd, StatusStudent, statusHefeaz, evaluationStudent, Notes, dayOfWeek;
    private int ayaStart, ayaEnd, day, month, year;

    public DailyReport() {
    }

    public DailyReport(String id, String UID, String idGroup, String suratStart, String suratEnd, String statusStudent, String statusHefeaz, String evaluationStudent, int day, int month, int year, String notes, String dayOfWeek, int ayaStart, int ayaEnd) {
        this.id = id;
        this.UID = UID;
        this.suratStart = suratStart;
        this.idGroup = idGroup;
        this.suratEnd = suratEnd;
        StatusStudent = statusStudent;
        this.statusHefeaz = statusHefeaz;
        this.evaluationStudent = evaluationStudent;
        this.day = day;
        this.month = month;
        this.year = year;
        Notes = notes;
        this.dayOfWeek = dayOfWeek;
        this.ayaStart = ayaStart;
        this.ayaEnd = ayaEnd;
    }

    //////////////////// No Hafez
    public DailyReport(String id, String UID, String idGroup, String statusStudent, String statusHefeaz, int day, int month, int year, String notes, String dayOfWeek) {
        this.id = id;
        this.UID = UID;
        StatusStudent = statusStudent;
        this.statusHefeaz = statusHefeaz;
        this.idGroup = idGroup;
        this.day = day;
        this.month = month;
        this.year = year;
        Notes = notes;
        this.dayOfWeek = dayOfWeek;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
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

    public String getStatusStudent() {
        return StatusStudent;
    }

    public void setStatusStudent(String statusStudent) {
        StatusStudent = statusStudent;
    }

    public String getStatusHefeaz() {
        return statusHefeaz;
    }

    public void setStatusHefeaz(String statusHefeaz) {
        this.statusHefeaz = statusHefeaz;
    }

    public String getEvaluationStudent() {
        return evaluationStudent;
    }

    public void setEvaluationStudent(String evaluationStudent) {
        this.evaluationStudent = evaluationStudent;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
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

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(String idGroup) {
        this.idGroup = idGroup;
    }
}
