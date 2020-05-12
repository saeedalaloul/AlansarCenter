package com.alansar.center.Models;

import android.os.Environment;
import android.util.Log;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
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
import java.util.Calendar;
import java.util.List;

class ReportDesignDatabase {
    static File file;

    ReportDesignDatabase(XSSFWorkbook wb, ArrayList<ReportDatabase> reports, List<String> Columns, int type) {
        Calendar calendar = Calendar.getInstance();
        String ReportName = "";
        String SheetName = "";
        if (reports.size() > 0) {
            if (type == 0) {
                ReportName = "قاعدة بيانات المركز محدثة لعام ";
                SheetName = "قاعدة بيانات المركز محدثة لعام ";
            } else if (type == 1) {
                ReportName = "قاعدة بيانات " + reports.get(0).getStage() + " محدثة لعام ";
                SheetName = "قاعدة بيانات " + reports.get(0).getStage() + " محدثة لعام ";
            } else if (type == 2) {
                ReportName = "قاعدة بيانات لحلقة المحفظ " + reports.get(0).getMohafezName() + " محدثة لعام ";
                SheetName = "قاعدة بيانات لحلقة المحفظ " + reports.get(0).getMohafezName() + " محدثة لعام ";
            }
            int year = calendar.get(Calendar.YEAR);
            ReportName += year + "م";
            SheetName += year + "م";
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "مركز الأنصار");
            file = new File(folder, ReportName + ".xlsx");
            XSSFSheet sheet = wb.createSheet("" + SheetName); //Creating a sheet;
            XSSFRow rowA = sheet.createRow(0);

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


            for (String s : Columns) {
                switch (s) {
                    case "اسم الطالب رباعي":
                        int LastCellNumA1 = rowA.getLastCellNum();
                        XSSFCell cellA1 = rowA.createCell(LastCellNumA1);
                        cellA1.setCellValue("اسم الطالب رباعي");
                        sheet.setColumnWidth(LastCellNumA1, (25 * 320));
                        cellA1.setCellStyle(styleA1);
                        break;
                    case "الصف":
                        int LastCellNumA2 = rowA.getLastCellNum();
                        XSSFCell cellA2 = rowA.createCell(LastCellNumA2);
                        cellA2.setCellValue("الصف");
                        sheet.setColumnWidth(LastCellNumA2, (25 * 200));
                        cellA2.setCellStyle(styleA1);
                        break;
                    case "الحفظ الكلي":
                        int LastCellNumA3 = rowA.getLastCellNum();
                        XSSFCell cellA3 = rowA.createCell(LastCellNumA3);
                        cellA3.setCellValue("الحفظ الكلي");
                        sheet.setColumnWidth(LastCellNumA3, (25 * 80));
                        cellA3.setCellStyle(styleA1);
                        break;
                    case "المرحلة":
                        int LastCellNumA4 = rowA.getLastCellNum();
                        XSSFCell cellA4 = rowA.createCell(LastCellNumA4);
                        cellA4.setCellValue("المرحلة");
                        sheet.setColumnWidth(LastCellNumA4, (25 * 180));
                        cellA4.setCellStyle(styleA1);
                        break;
                    case "جوال ولي الأمر":
                        int LastCellNumA5 = rowA.getLastCellNum();
                        XSSFCell cellA5 = rowA.createCell(LastCellNumA5);
                        cellA5.setCellValue("جوال ولي الأمر");
                        sheet.setColumnWidth(LastCellNumA5, (25 * 150));
                        cellA5.setCellStyle(styleA1);
                        break;
                    case "سنة الميلاد":
                        int LastCellNumA6 = rowA.getLastCellNum();
                        XSSFCell cellA6 = rowA.createCell(LastCellNumA6);
                        cellA6.setCellValue("سنة الميلاد");
                        sheet.setColumnWidth(LastCellNumA6, (25 * 100));
                        cellA6.setCellStyle(styleA1);
                        break;
                    case "تاريخ الميلاد":
                        int LastCellNumA7 = rowA.getLastCellNum();
                        XSSFCell cellA7 = rowA.createCell(LastCellNumA7);
                        cellA7.setCellValue("تاريخ الميلاد");
                        sheet.setColumnWidth(LastCellNumA7, (25 * 150));
                        cellA7.setCellStyle(styleA1);
                        break;
                    case "رقم هوية الطالب":
                        int LastCellNumA8 = rowA.getLastCellNum();
                        XSSFCell cellA8 = rowA.createCell(LastCellNumA8);
                        cellA8.setCellValue("رقم هوية الطالب");
                        sheet.setColumnWidth(LastCellNumA8, (25 * 150));
                        cellA8.setCellStyle(styleA1);
                        break;
                    case "اسم المحفظ":
                        int LastCellNumA9 = rowA.getLastCellNum();
                        XSSFCell cellA9 = rowA.createCell(LastCellNumA9);
                        cellA9.setCellValue("اسم المحفظ");
                        sheet.setColumnWidth(LastCellNumA9, (25 * 300));
                        cellA9.setCellStyle(styleA1);
                        break;
                    case "العمر":
                        int LastCellNumA10 = rowA.getLastCellNum();
                        XSSFCell cellA10 = rowA.createCell(LastCellNumA10);
                        cellA10.setCellValue("العمر");
                        sheet.setColumnWidth(LastCellNumA10, (25 * 80));
                        cellA10.setCellStyle(styleA1);
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
                        case "الصف":
                            XSSFCell cellF3 = rowF.createCell(rowF.getLastCellNum());
                            cellF3.setCellValue(reports.get(i).getStudentClass());
                            cellF3.setCellStyle(styleA2);
                            break;
                        case "الحفظ الكلي":
                            XSSFCell cellF4 = rowF.createCell(rowF.getLastCellNum());
                            cellF4.setCellValue(reports.get(i).getTotalConservation());
                            cellF4.setCellStyle(styleA2);
                            break;
                        case "المرحلة":
                            XSSFCell cellF5 = rowF.createCell(rowF.getLastCellNum());
                            cellF5.setCellValue(reports.get(i).getStage());
                            cellF5.setCellStyle(styleA2);
                            break;
                        case "جوال ولي الأمر":
                            XSSFCell cellF6 = rowF.createCell(rowF.getLastCellNum());
                            cellF6.setCellValue(reports.get(i).getPhoneNumber());
                            cellF6.setCellStyle(styleA2);
                            break;
                        case "سنة الميلاد":
                            XSSFCell cellF7 = rowF.createCell(rowF.getLastCellNum());
                            cellF7.setCellValue(reports.get(i).getYearOfBirth());
                            cellF7.setCellStyle(styleA2);
                            break;
                        case "تاريخ الميلاد":
                            XSSFCell cellF8 = rowF.createCell(rowF.getLastCellNum());
                            cellF8.setCellValue(reports.get(i).getDateOfBirth());
                            cellF8.setCellStyle(styleA2);
                            break;
                        case "رقم هوية الطالب":
                            XSSFCell cellF9 = rowF.createCell(rowF.getLastCellNum());
                            cellF9.setCellValue(reports.get(i).getIdentificationNumber());
                            cellF9.setCellStyle(styleA2);
                            break;
                        case "اسم المحفظ":
                            XSSFCell cellF10 = rowF.createCell(rowF.getLastCellNum());
                            cellF10.setCellValue(reports.get(i).getMohafezName());
                            cellF10.setCellStyle(styleA2);
                            break;
                        case "العمر":
                            XSSFCell cellF11 = rowF.createCell(rowF.getLastCellNum());
                            cellF11.setCellValue(reports.get(i).getAge());
                            cellF11.setCellStyle(styleA2);
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
