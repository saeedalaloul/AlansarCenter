package com.alansar.center.supervisor_exams.Model;

import java.util.HashMap;

public class ReportExam {
    private String studentName, partExam, dateOfExam, mohafezName, testerName, stage, notes;
    private HashMap<String, Double> marksExamQuestions;
    private double average;
    private int countExamQuestions;

    ReportExam(String studentName, String partExam, String dateOfExam, String mohafezName, String testerName, String stage, String notes, int countExamQuestions, HashMap<String, Double> marksExamQuestions, double average) {
        this.studentName = studentName;
        this.partExam = partExam;
        this.dateOfExam = dateOfExam;
        this.mohafezName = mohafezName;
        this.testerName = testerName;
        this.stage = stage;
        this.notes = notes;
        this.countExamQuestions = countExamQuestions;
        this.marksExamQuestions = marksExamQuestions;
        this.average = average;
    }

    int getCountExamQuestions() {
        return countExamQuestions;
    }

    public void setCountExamQuestions(int countExamQuestions) {
        this.countExamQuestions = countExamQuestions;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPartExam() {
        return partExam;
    }

    public void setPartExam(String partExam) {
        this.partExam = partExam;
    }

    public String getDateOfExam() {
        return dateOfExam;
    }

    public void setDateOfExam(String dateOfExam) {
        this.dateOfExam = dateOfExam;
    }

    public String getMohafezName() {
        return mohafezName;
    }

    public void setMohafezName(String mohafezName) {
        this.mohafezName = mohafezName;
    }

    public String getTesterName() {
        return testerName;
    }

    public void setTesterName(String testerName) {
        this.testerName = testerName;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public HashMap<String, Double> getMarksExamQuestions() {
        return marksExamQuestions;
    }

    public void setMarksExamQuestions(HashMap<String, Double> marksExamQuestions) {
        this.marksExamQuestions = marksExamQuestions;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
