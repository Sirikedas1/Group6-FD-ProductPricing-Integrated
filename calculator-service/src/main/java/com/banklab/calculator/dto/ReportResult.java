package com.banklab.calculator.dto;

/**
 * Result of adding one calculation to the CSV report (Lab L11).
 * fileName   : e.g. fd_report_alice123.csv
 * csvRow     : the line that was written, e.g. alice123,100000,12M,6.5,2.0,8.5,108500
 * rowsInFile : how many data rows the file now has
 */
public record ReportResult(String fileName, String csvRow, int rowsInFile) {
}
