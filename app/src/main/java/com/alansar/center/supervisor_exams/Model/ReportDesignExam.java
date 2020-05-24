package com.alansar.center.supervisor_exams.Model;

import android.os.Environment;
import android.util.Log;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

class ReportDesignExam {

    static File file;


     ReportDesignExam(XSSFWorkbook wb, ArrayList<ReportExam> reports, List<String> Columns, int type) {
        Calendar calendar = Calendar.getInstance();
        String ReportName = "";
        String SheetName = "";
        if (reports.size() > 0) {
            if (type == 0) {
                ReportName = "قاعدة بيانات الإختبارات محدثة لعام ";
                SheetName = "قاعدة بيانات الإختبارات محدثة لعام ";
            } else if (type == 1) {
                ReportName = "قاعدة بيانات " + reports.get(0).getStage() + " محدثة لعام ";
                SheetName = "قاعدة بيانات " + reports.get(0).getStage() + " محدثة لعام ";
            } else if (type == 2) {
                ReportName = "قاعدة بيانات الإختبارات لحلقة المحفظ " + reports.get(0).getMohafezName() + " محدثة لعام ";
                SheetName = "قاعدة بيانات الإختبارات لحلقة المحفظ " + reports.get(0).getMohafezName() + " محدثة لعام ";
            }
            int year = calendar.get(Calendar.YEAR);
            ReportName += year + "م";
            SheetName += year + "م";
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "مركز الأنصار");
            file = new File(folder, ReportName + ".xlsx");

            XSSFSheet sheet = wb.createSheet("" + SheetName); //Creating a sheet;
            XSSFRow rowA = sheet.createRow(0);
            XSSFRow rowB = sheet.createRow(1);

            sheet.setRightToLeft(true);
            XSSFCellStyle styleA1 = wb.createCellStyle();
            XSSFCellStyle styleA2 = wb.createCellStyle();

            XSSFFont headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor((short) 100);
            headerFont.setFontName("Simplified Arabic");
            headerFont.setFontHeight(12);


            styleA1.setFont(headerFont);
            styleA1.setAlignment(HorizontalAlignment.CENTER);
            styleA1.setVerticalAlignment(VerticalAlignment.CENTER);
            styleA1.setBorderBottom(BorderStyle.THIN);
            styleA1.setBorderTop(BorderStyle.THIN);
            styleA1.setBorderLeft(BorderStyle.THIN);
            styleA1.setBorderRight(BorderStyle.THIN);
            styleA1.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            styleA1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // styleA1.setWrapText(true);

            styleA2.setFont(headerFont);
            styleA2.setAlignment(HorizontalAlignment.CENTER);
            styleA2.setVerticalAlignment(VerticalAlignment.CENTER);
            styleA2.setBorderBottom(BorderStyle.THIN);
            styleA2.setBorderTop(BorderStyle.THIN);
            styleA2.setBorderLeft(BorderStyle.THIN);
            styleA2.setBorderRight(BorderStyle.THIN);

            // styleA2.setWrapText(true);

            XSSFCell cellA0 = rowA.createCell(0);
            cellA0.setCellValue("م");
            sheet.setColumnWidth(0, (25 * 53));
            cellA0.setCellStyle(styleA1);

            XSSFCell cellB0 = rowB.createCell(0);
            cellB0.setCellValue("م");
            sheet.setColumnWidth(0, (25 * 53));
            cellB0.setCellStyle(styleA1);
            CellRangeAddress CellRangeA1 = new CellRangeAddress(rowA.getRowNum(), rowB.getRowNum(), cellA0.getColumnIndex(), cellB0.getColumnIndex());
            sheet.addMergedRegion(CellRangeA1);

            int[] list = new int[reports.size()];
            for (int i = 0; i < reports.size(); i++) {
                list[i] = reports.get(i).getMarksExamQuestions().size();
            }

            for (String s : Columns) {
                switch (s) {
                    case "اسم الطالب رباعي":
                        int LastCellNumA1 = rowA.getLastCellNum();
                        XSSFCell cellA1 = rowA.createCell(LastCellNumA1);
                        cellA1.setCellValue("اسم الطالب رباعي");
                        sheet.setColumnWidth(LastCellNumA1, (25 * 320));
                        cellA1.setCellStyle(styleA1);

                        int LastCellNumB1 = rowB.getLastCellNum();
                        XSSFCell cellB1 = rowB.createCell(LastCellNumB1);
                        cellB1.setCellValue("اسم الطالب رباعي");
                        sheet.setColumnWidth(LastCellNumB1, (25 * 320));
                        cellB1.setCellStyle(styleA1);

                        CellRangeAddress CellRangeB1 = new CellRangeAddress(rowA.getRowNum(), rowB.getRowNum(), cellA1.getColumnIndex(), cellB1.getColumnIndex());
                        sheet.addMergedRegion(CellRangeB1);
                        break;
                    case "تاريخ الإختبار":
                        int LastCellNumA2 = rowA.getLastCellNum();
                        XSSFCell cellA2 = rowA.createCell(LastCellNumA2);
                        cellA2.setCellValue("تاريخ الإختبار");
                        sheet.setColumnWidth(LastCellNumA2, (25 * 200));
                        cellA2.setCellStyle(styleA1);

                        int LastCellNumB2 = rowB.getLastCellNum();
                        XSSFCell cellB2 = rowB.createCell(LastCellNumB2);
                        sheet.setColumnWidth(LastCellNumB2, (25 * 200));
                        cellB2.setCellStyle(styleA1);

                        CellRangeAddress CellRangeB2 = new CellRangeAddress(rowA.getRowNum(), rowB.getRowNum(), cellA2.getColumnIndex(), cellB2.getColumnIndex());
                        sheet.addMergedRegion(CellRangeB2);
                        break;
                    case "اسم المحفظ":
                        int LastCellNumA3 = rowA.getLastCellNum();
                        XSSFCell cellA3 = rowA.createCell(LastCellNumA3);
                        cellA3.setCellValue("اسم المحفظ");
                        sheet.setColumnWidth(LastCellNumA3, (25 * 320));
                        cellA3.setCellStyle(styleA1);

                        int LastCellNumB3 = rowB.getLastCellNum();
                        XSSFCell cellB3 = rowB.createCell(LastCellNumB3);
                        sheet.setColumnWidth(LastCellNumB3, (25 * 320));
                        cellB3.setCellStyle(styleA1);

                        CellRangeAddress CellRangeB3 = new CellRangeAddress(rowA.getRowNum(), rowB.getRowNum(), cellA3.getColumnIndex(), cellB3.getColumnIndex());
                        sheet.addMergedRegion(CellRangeB3);
                        break;
                    case "اسم المختبر":
                        int LastCellNumA4 = rowA.getLastCellNum();
                        XSSFCell cellA4 = rowA.createCell(LastCellNumA4);
                        cellA4.setCellValue("اسم المختبر");
                        sheet.setColumnWidth(LastCellNumA4, (25 * 320));
                        cellA4.setCellStyle(styleA1);

                        int LastCellNumB4 = rowB.getLastCellNum();
                        XSSFCell cellB4 = rowB.createCell(LastCellNumB4);
                        sheet.setColumnWidth(LastCellNumB4, (25 * 320));
                        cellB4.setCellStyle(styleA1);
                        CellRangeAddress CellRangeB4 = new CellRangeAddress(rowA.getRowNum(), rowB.getRowNum(), cellA4.getColumnIndex(), cellB4.getColumnIndex());
                        sheet.addMergedRegion(CellRangeB4);
                        break;
                    case "المرحلة":
                        int LastCellNumA5 = rowA.getLastCellNum();
                        XSSFCell cellA5 = rowA.createCell(LastCellNumA5);
                        cellA5.setCellValue("المرحلة");
                        sheet.setColumnWidth(LastCellNumA5, (25 * 150));
                        cellA5.setCellStyle(styleA1);

                        int LastCellNumB5 = rowB.getLastCellNum();
                        XSSFCell cellB5 = rowB.createCell(LastCellNumB5);
                        sheet.setColumnWidth(LastCellNumB5, (25 * 150));
                        cellB5.setCellStyle(styleA1);

                        CellRangeAddress CellRangeB5 = new CellRangeAddress(rowA.getRowNum(), rowB.getRowNum(), cellA5.getColumnIndex(), cellB5.getColumnIndex());
                        sheet.addMergedRegion(CellRangeB5);
                        break;
                    case "عدد أسئلة الإختبار":
                        int LastCellNumA6 = rowA.getLastCellNum();
                        XSSFCell cellA6 = rowA.createCell(LastCellNumA6);
                        cellA6.setCellValue("عدد أسئلة الإختبار");
                        sheet.setColumnWidth(LastCellNumA6, (25 * 150));
                        cellA6.setCellStyle(styleA1);

                        int LastCellNumB6 = rowB.getLastCellNum();
                        XSSFCell cellB6 = rowB.createCell(LastCellNumB6);
                        sheet.setColumnWidth(LastCellNumB6, (25 * 150));
                        cellB6.setCellStyle(styleA1);

                        CellRangeAddress CellRangeB6 = new CellRangeAddress(rowA.getRowNum(), rowB.getRowNum(), cellA6.getColumnIndex(), cellB6.getColumnIndex());
                        sheet.addMergedRegion(CellRangeB6);
                        break;
                    case "أسئلة الإختبار":
                        int indexStart = 0, indexEnd = 0;
                        for (int i = 0; i < Arrays.stream(list).max().getAsInt(); i++) {
                            int LastCellNumA7 = rowA.getLastCellNum();
                            XSSFCell cellA7 = rowA.createCell(LastCellNumA7);
                            cellA7.setCellValue("أسئلة الإختبار");
                            sheet.setColumnWidth(LastCellNumA7, (25 * 150));
                            cellA7.setCellStyle(styleA1);

                            int LastCellNumB7 = rowB.getLastCellNum();
                            XSSFCell cellB7 = rowB.createCell(LastCellNumB7);
                            cellB7.setCellValue("س" + (i + 1));
                            sheet.setColumnWidth(LastCellNumB7, (25 * 150));
                            cellB7.setCellStyle(styleA1);

                            if (i == 0) {
                                indexStart = LastCellNumA7;
                            }
                            if (i == Arrays.stream(list).max().getAsInt() - 1) {
                                indexEnd = LastCellNumA7;
                            }
                        }
                        if (indexStart != 0 && indexEnd != 0) {
                            CellRangeAddress CellRangeA2 = new CellRangeAddress(rowA.getRowNum(), rowA.getRowNum(), indexStart, indexEnd);
                            sheet.addMergedRegion(CellRangeA2);
                        }
                        break;
                    case "المعدل":
                        int LastCellNumA8 = rowA.getLastCellNum();
                        XSSFCell cellA8 = rowA.createCell(LastCellNumA8);
                        cellA8.setCellValue("المعدل");
                        sheet.setColumnWidth(LastCellNumA8, (25 * 100));
                        cellA8.setCellStyle(styleA1);

                        int LastCellNumB8 = rowB.getLastCellNum();
                        XSSFCell cellB8 = rowB.createCell(LastCellNumB8);
                        sheet.setColumnWidth(LastCellNumB8, (25 * 100));
                        cellB8.setCellStyle(styleA1);

                        CellRangeAddress CellRangeB8 = new CellRangeAddress(rowA.getRowNum(), rowB.getRowNum(), cellA8.getColumnIndex(), cellB8.getColumnIndex());
                        sheet.addMergedRegion(CellRangeB8);
                        break;
                    case "ملاحظات":
                        int LastCellNumA9 = rowA.getLastCellNum();
                        XSSFCell cellA9 = rowA.createCell(LastCellNumA9);
                        cellA9.setCellValue("ملاحظات");
                        sheet.setColumnWidth(LastCellNumA9, (25 * 306));
                        cellA9.setCellStyle(styleA1);

                        int LastCellNumB9 = rowB.getLastCellNum();
                        XSSFCell cellB9 = rowB.createCell(LastCellNumB9);
                        sheet.setColumnWidth(LastCellNumB9, (25 * 306));
                        cellB9.setCellStyle(styleA1);

                        CellRangeAddress CellRangeB9 = new CellRangeAddress(rowA.getRowNum(), rowB.getRowNum(), cellA9.getColumnIndex(), cellB9.getColumnIndex());
                        sheet.addMergedRegion(CellRangeB9);
                        break;
                }
            }

            for (int i = 0; i < reports.size(); i++) {
                int j = sheet.getLastRowNum() + 1;
                XSSFRow rowF = sheet.createRow(j);

                XSSFCell cellF1 = rowF.createCell(0);
                int ii = i + 1;
                cellF1.setCellValue(ii);
                cellF1.setCellStyle(styleA1);

                for (String s : Columns) {
                    switch (s) {
                        case "اسم الطالب رباعي":
                            XSSFCell cellF2 = rowF.createCell(rowF.getLastCellNum());
                            cellF2.setCellValue(reports.get(i).getStudentName());
                            cellF2.setCellStyle(styleA2);
                            break;
                        case "تاريخ الإختبار":
                            XSSFCell cellF3 = rowF.createCell(rowF.getLastCellNum());
                            cellF3.setCellValue(reports.get(i).getDateOfExam());
                            cellF3.setCellStyle(styleA2);
                            break;
                        case "اسم المحفظ":
                            XSSFCell cellF4 = rowF.createCell(rowF.getLastCellNum());
                            cellF4.setCellValue(reports.get(i).getMohafezName());
                            cellF4.setCellStyle(styleA2);
                            break;
                        case "اسم المختبر":
                            XSSFCell cellF5 = rowF.createCell(rowF.getLastCellNum());
                            cellF5.setCellValue(reports.get(i).getTesterName());
                            cellF5.setCellStyle(styleA2);
                            break;
                        case "المرحلة":
                            XSSFCell cellF6 = rowF.createCell(rowF.getLastCellNum());
                            cellF6.setCellValue(reports.get(i).getStage());
                            cellF6.setCellStyle(styleA2);
                            break;
                        case "عدد أسئلة الإختبار":
                            XSSFCell cellF7 = rowF.createCell(rowF.getLastCellNum());
                            cellF7.setCellValue(reports.get(i).getCountExamQuestions());
                            cellF7.setCellStyle(styleA2);
                            break;
                        case "أسئلة الإختبار":
                            for (int jj = 1; jj <= Arrays.stream(list).max().getAsInt(); jj++) {
                                XSSFCell cellF8 = rowF.createCell(rowF.getLastCellNum());
                                if (reports.get(i).getMarksExamQuestions().get(jj + "") != null) {
                                    cellF8.setCellValue(reports.get(i).getMarksExamQuestions().get(jj + ""));
                                }
                                cellF8.setCellStyle(styleA2);
                            }
                            break;
                        case "المعدل":
                            XSSFCell cellF9 = rowF.createCell(rowF.getLastCellNum());
                            cellF9.setCellValue(reports.get(i).getAverage());
                            cellF9.setCellStyle(styleA2);
                            break;
                        case "ملاحظات":
                            XSSFCell cellF10 = rowF.createCell(rowF.getLastCellNum());
                            cellF10.setCellValue(reports.get(i).getNotes());
                            cellF10.setCellStyle(styleA2);
                            break;
                    }
                }

            }


            try {
                if (!folder.exists()) {
                    boolean isCreated = folder.mkdirs();
                    Log.d("sss", "is Created file :" + isCreated);
                }
                FileOutputStream fileOut = new FileOutputStream(file);
                wb.write(fileOut);
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
