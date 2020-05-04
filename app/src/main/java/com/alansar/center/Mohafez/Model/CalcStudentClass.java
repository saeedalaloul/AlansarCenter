package com.alansar.center.Mohafez.Model;

import java.util.Calendar;

public class CalcStudentClass {
    public static String calcStudentClass(int Year) {
        Calendar calendar = Calendar.getInstance();
        int result = calendar.get(Calendar.YEAR) - Year;
        if (result == 7) {
            return "الأول";
        } else if (result == 8) {
            return "الثاني";
        } else if (result == 9) {
            return "الثالث";
        } else if (result == 10) {
            return "الرابع";
        } else if (result == 11) {
            return "الخامس";
        } else if (result == 12) {
            return "السادس";
        } else if (result == 13) {
            return "السابع";
        } else if (result == 14) {
            return "الثامن";
        } else if (result == 15) {
            return "التاسع";
        } else if (result == 16) {
            return "العاشر";
        } else if (result == 17) {
            return "الحادي عشر";
        } else if (result == 18) {
            return "الثاني عشر";
        } else if (result == 19) {
            return "جامعي";
        } else if (result == 20) {
            return "جامعي";
        } else if (result == 21) {
            return "جامعي";
        } else if (result == 22) {
            return "جامعي";
        }
        return "";
    }
}
