package com.alansar.center.supervisor_exams.Model;

import java.util.HashMap;

public class Exam {
    private String id, idStudent, idMohafez, idTester, ExamPart, Notes, stage, dateRejection, date;
    private int statusAcceptance, day, month, year;
    private HashMap<String, Double> marksExamQuestions;
    private HashMap<String, String> signsExamQuestions;
    private HashMap<String, Boolean> IsSeenExam;

    public Exam() {
    }

    public Exam(String id, String idStudent, String idMohafez, String ExamPart, int statusAcceptance, String stage, int day, int month, int year) {
        this.id = id;
        this.idStudent = idStudent;
        this.idMohafez = idMohafez;
        this.ExamPart = ExamPart;
        this.statusAcceptance = statusAcceptance;
        this.stage = stage;
        this.day = day;
        this.month = month;
        this.year = year;
        IsSeenExam = new HashMap<>();
        IsSeenExam.put("isSeenMohafez", false);
        IsSeenExam.put("isSeenMoshref", false);
        IsSeenExam.put("isSeenMoshrefExam", false);
        IsSeenExam.put("isSeenTester", false);
        IsSeenExam.put("isSeenEdare", false);
    }

    public Exam(String id, String idStudent, String idMohafez,
                String idTester, String examPart, String notes,
                String stage, String dateRejection, String date,
                int statusAcceptance, int day, int month, int year,
                HashMap<String, Double> marksExamQuestions, HashMap<String, String> signsExamQuestions,
                HashMap<String, Boolean> isSeenExam) {
        this.id = id;
        this.idStudent = idStudent;
        this.idMohafez = idMohafez;
        this.idTester = idTester;
        ExamPart = examPart;
        Notes = notes;
        this.stage = stage;
        this.dateRejection = dateRejection;
        this.date = date;
        this.statusAcceptance = statusAcceptance;
        this.day = day;
        this.month = month;
        this.year = year;
        this.marksExamQuestions = marksExamQuestions;
        this.signsExamQuestions = signsExamQuestions;
        IsSeenExam = isSeenExam;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public String getIdMohafez() {
        return idMohafez;
    }

    public void setIdMohafez(String idMohafez) {
        this.idMohafez = idMohafez;
    }

    public String getIdTester() {
        return idTester;
    }

    public void setIdTester(String idTester) {
        this.idTester = idTester;
    }

    public String getNotes() {
        return Notes;
    }

    public void setNotes(String notes) {
        Notes = notes;
    }

    public int getStatusAcceptance() {
        return statusAcceptance;
    }

    public void setStatusAcceptance(int statusAcceptance) {
        this.statusAcceptance = statusAcceptance;
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

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getExamPart() {
        return ExamPart;
    }

    public void setExamPart(String examPart) {
        ExamPart = examPart;
    }

    public HashMap<String, Boolean> getIsSeenExam() {
        return IsSeenExam;
    }

    public void setIsSeenExam(HashMap<String, Boolean> isSeenExam) {
        IsSeenExam = isSeenExam;
    }

    public String getDateRejection() {
        return dateRejection;
    }

    public void setDateRejection(String dateRejection) {
        this.dateRejection = dateRejection;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public HashMap<String, Double> getMarksExamQuestions() {
        return marksExamQuestions;
    }

    public void setMarksExamQuestions(HashMap<String, Double> marksExamQuestions) {
        this.marksExamQuestions = marksExamQuestions;
    }

    public HashMap<String, String> getSignsExamQuestions() {
        return signsExamQuestions;
    }

    public void setSignsExamQuestions(HashMap<String, String> signsExamQuestions) {
        this.signsExamQuestions = signsExamQuestions;
    }
}
