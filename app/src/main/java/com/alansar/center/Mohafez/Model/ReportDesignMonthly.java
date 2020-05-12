package com.alansar.center.Mohafez.Model;

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

public class ReportDesignMonthly {
    public static File file;

    public ReportDesignMonthly(String nameMohafez, XSSFWorkbook wb, int month, int year, ArrayList<MonthlyReport> reports) {
        if (reports.size() > 0) {
            String ReportName = "تقرير الحلقة عن شهر " + month;
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "مركز الأنصار");
            file = new File(folder, ReportName + ".xlsx");
            XSSFSheet sheet = wb.createSheet("" + nameMohafez); //Creating a sheet;
            XSSFRow rowA = sheet.createRow(0);
            XSSFRow rowB = sheet.createRow(1);
            XSSFRow rowC = sheet.createRow(2);
            XSSFRow rowD = sheet.createRow(3);

            XSSFCell cellA1 = rowA.createCell(0);
            XSSFCell cellA2 = rowA.createCell(1);
            XSSFCell cellA3 = rowA.createCell(2);
            XSSFCell cellA4 = rowA.createCell(3);
            XSSFCell cellA5 = rowA.createCell(4);
            XSSFCell cellA6 = rowA.createCell(5);
            XSSFCell cellA7 = rowA.createCell(6);
            XSSFCell cellA8 = rowA.createCell(7);
            XSSFCell cellA9 = rowA.createCell(8);
            XSSFCell cellA10 = rowA.createCell(9);
            XSSFCell cellA11 = rowA.createCell(10);
            XSSFCell cellA12 = rowA.createCell(11);

            XSSFCell cellB1 = rowB.createCell(0);
            XSSFCell cellB2 = rowB.createCell(1);
            XSSFCell cellB3 = rowB.createCell(2);
            XSSFCell cellB4 = rowB.createCell(3);
            XSSFCell cellB5 = rowB.createCell(4);
            XSSFCell cellB6 = rowB.createCell(5);
            XSSFCell cellB7 = rowB.createCell(6);
            XSSFCell cellB8 = rowB.createCell(7);
            XSSFCell cellB9 = rowB.createCell(8);
            XSSFCell cellB10 = rowB.createCell(9);
            XSSFCell cellB11 = rowB.createCell(10);
            XSSFCell cellB12 = rowB.createCell(11);

            XSSFCell cellC1 = rowC.createCell(0);
            XSSFCell cellC2 = rowC.createCell(1);
            XSSFCell cellC3 = rowC.createCell(2);
            XSSFCell cellC4 = rowC.createCell(3);
            XSSFCell cellC5 = rowC.createCell(4);
            XSSFCell cellC6 = rowC.createCell(5);
            XSSFCell cellC7 = rowC.createCell(6);
            XSSFCell cellC8 = rowC.createCell(7);
            XSSFCell cellC9 = rowC.createCell(8);
            XSSFCell cellC10 = rowC.createCell(9);
            XSSFCell cellC11 = rowC.createCell(10);
            XSSFCell cellC12 = rowC.createCell(11);

            XSSFCell cellD1 = rowD.createCell(0);
            XSSFCell cellD2 = rowD.createCell(1);
            XSSFCell cellD3 = rowD.createCell(2);
            XSSFCell cellD4 = rowD.createCell(3);
            XSSFCell cellD5 = rowD.createCell(4);
            XSSFCell cellD6 = rowD.createCell(5);
            XSSFCell cellD7 = rowD.createCell(6);
            XSSFCell cellD8 = rowD.createCell(7);
            XSSFCell cellD9 = rowD.createCell(8);
            XSSFCell cellD10 = rowD.createCell(9);
            XSSFCell cellD11 = rowD.createCell(10);
            XSSFCell cellD12 = rowD.createCell(11);

            String val1 = "                                        تقرير  حلقات التحفيظ عن شهر:......";
            String val2 = month + "....... " + year + "م";
            String val3 = "                  انتساب الحلقة :..........دار القرآن الكريم والسنة…................";

            cellA1.setCellValue("اسم المركز:.............بسيسو................... منطقة:.....الشجاعية.........");
            cellA6.setCellValue("                                      اسم محفظ الحلقة:..........." + nameMohafez + "................... ");
            cellB1.setCellValue(val1 + val2 + val3);
            cellC1.setCellValue("م");
            cellC2.setCellValue("اسم الطالب رباعي");
            cellC3.setCellValue("الصف");
            cellC4.setCellValue("الحفظ الكلى");
            cellC9.setCellValue(" صفحات الحفظ");
            cellC10.setCellValue("صفحات المراجعة");
            cellC11.setCellValue("أيام الغياب");
            cellC12.setCellValue("الملاحظات والاختبارات");
            cellC5.setCellValue("بداية الحفظ");
            cellC7.setCellValue("نهاية الحفظ");
            cellD5.setCellValue("سورة");
            cellD6.setCellValue("آية");
            cellD7.setCellValue("سورة");
            cellD8.setCellValue("آية");

            sheet.setColumnWidth(0, (25 * 53));
            sheet.setColumnWidth(1, (26 * 257));
            sheet.setColumnWidth(2, (25 * 62));
            sheet.setColumnWidth(3, (25 * 81));
            sheet.setColumnWidth(4, (25 * 84));
            sheet.setColumnWidth(5, (25 * 51));
            sheet.setColumnWidth(6, (25 * 73));
            sheet.setColumnWidth(7, (25 * 47));
            sheet.setColumnWidth(8, (25 * 135));
            sheet.setColumnWidth(9, (25 * 135));
            sheet.setColumnWidth(10, (25 * 78));
            sheet.setColumnWidth(11, (25 * 306));

            CellRangeAddress CellRangeA1 = new CellRangeAddress(rowA.getRowNum(), rowA.getRowNum(), cellA1.getColumnIndex(), cellA5.getColumnIndex());
            CellRangeAddress CellRangeA2 = new CellRangeAddress(rowA.getRowNum(), rowA.getRowNum(), cellA6.getColumnIndex(), cellA12.getColumnIndex());
            CellRangeAddress CellRangeB1 = new CellRangeAddress(rowB.getRowNum(), rowB.getRowNum(), cellB1.getColumnIndex(), cellB12.getColumnIndex());
            CellRangeAddress CellRangeC1 = new CellRangeAddress(rowC.getRowNum(), rowD.getRowNum(), cellC1.getColumnIndex(), cellD1.getColumnIndex());
            CellRangeAddress CellRangeC2 = new CellRangeAddress(rowC.getRowNum(), rowD.getRowNum(), cellC2.getColumnIndex(), cellD2.getColumnIndex());
            CellRangeAddress CellRangeC3 = new CellRangeAddress(rowC.getRowNum(), rowD.getRowNum(), cellC3.getColumnIndex(), cellD3.getColumnIndex());
            CellRangeAddress CellRangeC4 = new CellRangeAddress(rowC.getRowNum(), rowD.getRowNum(), cellC4.getColumnIndex(), cellD4.getColumnIndex());
            CellRangeAddress CellRangeC5 = new CellRangeAddress(rowC.getRowNum(), rowC.getRowNum(), cellC5.getColumnIndex(), cellD6.getColumnIndex());
            CellRangeAddress CellRangeC6 = new CellRangeAddress(rowC.getRowNum(), rowC.getRowNum(), cellC7.getColumnIndex(), cellD8.getColumnIndex());
            CellRangeAddress CellRangeC7 = new CellRangeAddress(rowC.getRowNum(), rowD.getRowNum(), cellC9.getColumnIndex(), cellD9.getColumnIndex());
            CellRangeAddress CellRangeC8 = new CellRangeAddress(rowC.getRowNum(), rowD.getRowNum(), cellC10.getColumnIndex(), cellD10.getColumnIndex());
            CellRangeAddress CellRangeC9 = new CellRangeAddress(rowC.getRowNum(), rowD.getRowNum(), cellC11.getColumnIndex(), cellD11.getColumnIndex());
            CellRangeAddress CellRangeC10 = new CellRangeAddress(rowC.getRowNum(), rowD.getRowNum(), cellC12.getColumnIndex(), cellD12.getColumnIndex());

            sheet.addMergedRegion(CellRangeA1);
            sheet.addMergedRegion(CellRangeA2);
            sheet.addMergedRegion(CellRangeB1);
            sheet.addMergedRegion(CellRangeC1);
            sheet.addMergedRegion(CellRangeC2);
            sheet.addMergedRegion(CellRangeC3);
            sheet.addMergedRegion(CellRangeC4);
            sheet.addMergedRegion(CellRangeC5);
            sheet.addMergedRegion(CellRangeC6);
            sheet.addMergedRegion(CellRangeC7);
            sheet.addMergedRegion(CellRangeC8);
            sheet.addMergedRegion(CellRangeC9);
            sheet.addMergedRegion(CellRangeC10);
            sheet.setRightToLeft(true);

            XSSFCellStyle styleA1 = wb.createCellStyle();
            XSSFCellStyle styleA2 = wb.createCellStyle();
            XSSFCellStyle styleB1 = wb.createCellStyle();
            XSSFCellStyle styleC1 = wb.createCellStyle();
            XSSFCellStyle styleD1 = wb.createCellStyle();
            XSSFCellStyle styleD2 = wb.createCellStyle();

            XSSFFont headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor((short) 100);
            headerFont.setFontName("Simplified Arabic");
            headerFont.setFontHeight(12);


            styleA1.setFont(headerFont);
            styleA1.setAlignment(HorizontalAlignment.RIGHT);
            styleA1.setVerticalAlignment(VerticalAlignment.CENTER);
            // styleA1.setWrapText(true);

            styleA2.setFont(headerFont);
            styleA2.setAlignment(HorizontalAlignment.CENTER);
            styleA2.setVerticalAlignment(VerticalAlignment.CENTER);
            // styleA2.setWrapText(true);

            styleB1.setFont(headerFont);
            styleB1.setAlignment(HorizontalAlignment.CENTER);
            styleB1.setVerticalAlignment(VerticalAlignment.CENTER);
            // styleB1.setWrapText(true);

            styleC1.setFont(headerFont);
            styleC1.setAlignment(HorizontalAlignment.CENTER);
            styleC1.setVerticalAlignment(VerticalAlignment.CENTER);
            styleC1.setBorderBottom(BorderStyle.THIN);
            styleC1.setBorderTop(BorderStyle.THIN);
            styleC1.setBorderLeft(BorderStyle.THIN);
            styleC1.setBorderRight(BorderStyle.THIN);
            styleC1.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            styleC1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // styleC1.setWrapText(true);

            styleD1.setFont(headerFont);
            styleD1.setAlignment(HorizontalAlignment.CENTER);
            styleD1.setVerticalAlignment(VerticalAlignment.CENTER);
            styleD1.setBorderBottom(BorderStyle.THIN);
            styleD1.setBorderTop(BorderStyle.THIN);
            styleD1.setBorderLeft(BorderStyle.THIN);
            styleD1.setBorderRight(BorderStyle.THIN);
            styleD1.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            styleD1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // styleD1.setWrapText(true);

            styleD2.setFont(headerFont);
            styleD2.setAlignment(HorizontalAlignment.CENTER);
            styleD2.setVerticalAlignment(VerticalAlignment.CENTER);
            styleD2.setBorderBottom(BorderStyle.THIN);
            styleD2.setBorderTop(BorderStyle.THIN);
            styleD2.setBorderLeft(BorderStyle.THIN);
            styleD2.setBorderRight(BorderStyle.THIN);
            styleD2.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index);
            styleD2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // styleD2.setWrapText(true);


            cellA1.setCellStyle(styleA1);
            cellA2.setCellStyle(styleA1);
            cellA3.setCellStyle(styleA1);
            cellA4.setCellStyle(styleA1);
            cellA5.setCellStyle(styleA1);
            cellA6.setCellStyle(styleA2);
            cellA7.setCellStyle(styleA2);
            cellA8.setCellStyle(styleA2);
            cellA9.setCellStyle(styleA2);
            cellA10.setCellStyle(styleA2);
            cellA11.setCellStyle(styleA2);
            cellA12.setCellStyle(styleA2);

            cellB1.setCellStyle(styleB1);
            cellB2.setCellStyle(styleB1);
            cellB3.setCellStyle(styleB1);
            cellB4.setCellStyle(styleB1);
            cellB5.setCellStyle(styleB1);
            cellB6.setCellStyle(styleB1);
            cellB7.setCellStyle(styleB1);
            cellB8.setCellStyle(styleB1);
            cellB9.setCellStyle(styleB1);
            cellB10.setCellStyle(styleB1);
            cellB11.setCellStyle(styleB1);
            cellB12.setCellStyle(styleB1);

            cellC1.setCellStyle(styleC1);
            cellC2.setCellStyle(styleC1);
            cellC3.setCellStyle(styleC1);
            cellC4.setCellStyle(styleC1);
            cellC5.setCellStyle(styleC1);
            cellC6.setCellStyle(styleC1);
            cellC7.setCellStyle(styleC1);
            cellC8.setCellStyle(styleC1);
            cellC9.setCellStyle(styleC1);
            cellC10.setCellStyle(styleC1);
            cellC11.setCellStyle(styleC1);
            cellC12.setCellStyle(styleC1);


            cellD1.setCellStyle(styleD1);
            cellD2.setCellStyle(styleD1);
            cellD3.setCellStyle(styleD1);
            cellD4.setCellStyle(styleD1);
            cellD5.setCellStyle(styleD2);
            cellD6.setCellStyle(styleD2);
            cellD7.setCellStyle(styleD2);
            cellD8.setCellStyle(styleD2);
            cellD9.setCellStyle(styleD1);
            cellD10.setCellStyle(styleD1);
            cellD11.setCellStyle(styleD1);
            cellD12.setCellStyle(styleD1);

            XSSFCellStyle styleF1 = wb.createCellStyle();
            XSSFFont bodyFont = wb.createFont();
            bodyFont.setBold(true);
            bodyFont.setColor((short) 100);
            bodyFont.setFontName("Simplified Arabic");
            bodyFont.setFontHeight(12);
            styleF1.setFont(bodyFont);
            styleF1.setAlignment(HorizontalAlignment.CENTER);
            styleF1.setVerticalAlignment(VerticalAlignment.CENTER);
            styleF1.setBorderBottom(BorderStyle.THIN);
            styleF1.setBorderTop(BorderStyle.THIN);
            styleF1.setBorderLeft(BorderStyle.THIN);
            styleF1.setBorderRight(BorderStyle.THIN);

            XSSFCellStyle styleF2 = wb.createCellStyle();
            XSSFFont bodyFontNotes = wb.createFont();
            bodyFontNotes.setBold(true);
            bodyFontNotes.setColor((short) 100);
            bodyFontNotes.setFontName("Simplified Arabic");
            bodyFontNotes.setFontHeight(11);
            styleF2.setFont(bodyFontNotes);
            styleF2.setAlignment(HorizontalAlignment.CENTER);
            styleF2.setVerticalAlignment(VerticalAlignment.CENTER);
            styleF2.setBorderBottom(BorderStyle.THIN);
            styleF2.setBorderTop(BorderStyle.THIN);
            styleF2.setBorderLeft(BorderStyle.THIN);
            styleF2.setBorderRight(BorderStyle.THIN);
            styleF2.setWrapText(true);


            for (int i = 0; i < reports.size(); i++) {
                int j = sheet.getLastRowNum() + 1;
                XSSFRow rowF = sheet.createRow(j);
                XSSFCell cellF1 = rowF.createCell(0);
                XSSFCell cellF2 = rowF.createCell(1);
                XSSFCell cellF3 = rowF.createCell(2);
                XSSFCell cellF4 = rowF.createCell(3);
                XSSFCell cellF5 = rowF.createCell(4);
                XSSFCell cellF6 = rowF.createCell(5);
                XSSFCell cellF7 = rowF.createCell(6);
                XSSFCell cellF8 = rowF.createCell(7);
                XSSFCell cellF9 = rowF.createCell(8);
                XSSFCell cellF10 = rowF.createCell(9);
                XSSFCell cellF11 = rowF.createCell(10);
                XSSFCell cellF12 = rowF.createCell(11);
                int ii = i + 1;
                cellF1.setCellValue(ii);
                cellF2.setCellValue(reports.get(i).getStudentName());
                cellF3.setCellValue(reports.get(i).getStudentClass());
                cellF4.setCellValue(reports.get(i).getTotalConservation());
                cellF5.setCellValue(reports.get(i).getSuratStart());
                cellF6.setCellValue(reports.get(i).getAyaStart());
                cellF7.setCellValue(reports.get(i).getSuratEnd());
                cellF8.setCellValue(reports.get(i).getAyaEnd());
                cellF9.setCellValue(reports.get(i).getSafahatAlhafz());
                cellF10.setCellValue(reports.get(i).getSafahatAlmurajaea());
                cellF11.setCellValue(reports.get(i).getDaysAbsence());
                cellF12.setCellValue(reports.get(i).getNotes());

                cellF1.setCellStyle(styleD1);
                cellF2.setCellStyle(styleF1);
                cellF3.setCellStyle(styleF1);
                cellF4.setCellStyle(styleF1);
                cellF5.setCellStyle(styleF1);
                cellF6.setCellStyle(styleF1);
                cellF7.setCellStyle(styleF1);
                cellF8.setCellStyle(styleF1);
                cellF9.setCellStyle(styleF1);
                cellF10.setCellStyle(styleF1);
                cellF11.setCellStyle(styleF1);
                cellF12.setCellStyle(styleF2);
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
