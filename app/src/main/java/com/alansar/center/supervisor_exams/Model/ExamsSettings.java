package com.alansar.center.supervisor_exams.Model;

public class ExamsSettings {
    private boolean isUpdateExam, isDefaultData;
    private int maxQuestionsExam, minQuestionsExam, numberQuestionsPart, numberOrdersExamDay;

    public ExamsSettings() {
    }

    public ExamsSettings(boolean isUpdateExam, boolean isDefaultData, int maxQuestionsExam, int minQuestionsExam, int numberQuestionsPart, int numberOrdersExamDay) {
        this.isUpdateExam = isUpdateExam;
        this.isDefaultData = isDefaultData;
        this.maxQuestionsExam = maxQuestionsExam;
        this.minQuestionsExam = minQuestionsExam;
        this.numberQuestionsPart = numberQuestionsPart;
        this.numberOrdersExamDay = numberOrdersExamDay;
    }

    public ExamsSettings(boolean isDefaultData) {
        this.isDefaultData = isDefaultData;
    }

    public boolean isUpdateExam() {
        return isUpdateExam;
    }

    public void setUpdateExam(boolean updateExam) {
        isUpdateExam = updateExam;
    }

    public boolean isDefaultData() {
        return isDefaultData;
    }

    public void setDefaultData(boolean defaultData) {
        isDefaultData = defaultData;
    }

    public int getMaxQuestionsExam() {
        return maxQuestionsExam;
    }

    public void setMaxQuestionsExam(int maxQuestionsExam) {
        this.maxQuestionsExam = maxQuestionsExam;
    }

    public int getMinQuestionsExam() {
        return minQuestionsExam;
    }

    public void setMinQuestionsExam(int minQuestionsExam) {
        this.minQuestionsExam = minQuestionsExam;
    }

    public int getNumberQuestionsPart() {
        return numberQuestionsPart;
    }

    public void setNumberQuestionsPart(int numberQuestionsPart) {
        this.numberQuestionsPart = numberQuestionsPart;
    }

    public int getNumberOrdersExamDay() {
        return numberOrdersExamDay;
    }

    public void setNumberOrdersExamDay(int numberOrdersExamDay) {
        this.numberOrdersExamDay = numberOrdersExamDay;
    }
}
