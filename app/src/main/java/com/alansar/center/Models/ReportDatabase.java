package com.alansar.center.Models;

public class ReportDatabase {
    private String StudentName,StudentClass,Stage,DateOfBirth,MohafezName;
    private int TotalConservation,age,PhoneNumber,YearOfBirth,IdentificationNumber;

    public ReportDatabase() {
    }

    public ReportDatabase(String studentName, String studentClass, String stage, String dateOfBirth, String mohafezName, int totalConservation, int age, int phoneNumber, int yearOfBirth, int identificationNumber) {
        StudentName = studentName;
        StudentClass = studentClass;
        Stage = stage;
        DateOfBirth = dateOfBirth;
        MohafezName = mohafezName;
        TotalConservation = totalConservation;
        this.age = age;
        PhoneNumber = phoneNumber;
        YearOfBirth = yearOfBirth;
        IdentificationNumber = identificationNumber;
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

    public String getStage() {
        return Stage;
    }

    public void setStage(String stage) {
        Stage = stage;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getMohafezName() {
        return MohafezName;
    }

    public void setMohafezName(String mohafezName) {
        MohafezName = mohafezName;
    }

    public int getTotalConservation() {
        return TotalConservation;
    }

    public void setTotalConservation(int totalConservation) {
        TotalConservation = totalConservation;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public int getYearOfBirth() {
        return YearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        YearOfBirth = yearOfBirth;
    }

    public int getIdentificationNumber() {
        return IdentificationNumber;
    }

    public void setIdentificationNumber(int identificationNumber) {
        IdentificationNumber = identificationNumber;
    }
}
