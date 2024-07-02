package org.chrisbarbati.databasecontroller.utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class ExcelWriter {

    private static final String FILE_NAME = "BatchTest.xlsx";

    public void writeData(int batchSize, long processingTime) {
        Workbook workbook;
        Sheet sheet;
        File file = new File(FILE_NAME);

        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
                fis.close();
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Batch Data");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Batch Size");
                headerRow.createCell(1).setCellValue("Processing Time");
            }

            int lastRowNum = sheet.getLastRowNum();
            Row row = sheet.createRow(lastRowNum + 1);
            row.createCell(0).setCellValue(batchSize);
            row.createCell(1).setCellValue(processingTime);

            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}