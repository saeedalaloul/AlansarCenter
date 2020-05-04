package com.alansar.center.Mohafez.Model;

public class Report {
    private int moth, year;

    public Report() {
    }

    public Report(int moth, int year) {
        this.moth = moth;
        this.year = year;
    }

    public int getMoth() {
        return moth;
    }

    public void setMoth(int moth) {
        this.moth = moth;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
