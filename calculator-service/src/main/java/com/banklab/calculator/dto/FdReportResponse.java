package com.banklab.calculator.dto;

/**
 * Answer of POST /api/fd/report: the calculation plus what was written to the report.
 */
public record FdReportResponse(FdCalculationResponse calculation, ReportResult report) {
}
