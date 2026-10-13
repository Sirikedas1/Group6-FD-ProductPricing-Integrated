package com.banklab.calculator.report;

import com.banklab.calculator.dto.FdCalculationResponse;
import com.banklab.calculator.dto.ReportResult;
import com.banklab.calculator.exception.CalculationException;
import com.banklab.calculator.model.TermUnit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Lab L11: "Report generation through Python script (executed via Spring Boot)".
 *
 * For every calculation the user wants to keep, Java starts the Python script
 * scripts/generate_fd_report.py, which appends one row to a CSV file:
 *   Username,Principal,Term,BaseRate,CategoryAddOn,FinalRate,Maturity
 * Each user gets their own file: reports/fd_report_<username>.csv
 */
@Service
public class FdReportService {

    private final String pythonCommand;
    private final String scriptPath;
    private final String outputDir;
    private final ObjectMapper objectMapper;

    public FdReportService(@Value("${calculator.report.python-command:python}") String pythonCommand,
                           @Value("${calculator.report.script-path:scripts/generate_fd_report.py}") String scriptPath,
                           @Value("${calculator.report.output-dir:reports}") String outputDir,
                           ObjectMapper objectMapper) {
        this.pythonCommand = pythonCommand;
        this.scriptPath = scriptPath;
        this.outputDir = outputDir;
        this.objectMapper = objectMapper;
    }

    /** Runs the Python script to append this calculation to the user's CSV report. */
    public ReportResult appendToReport(String username, FdCalculationResponse calc) {
        Path script = Paths.get(scriptPath).toAbsolutePath();
        if (!Files.exists(script)) {
            throw CalculationException.internal("REPORT_SCRIPT_MISSING",
                    "Python report script not found at " + script, List.of());
        }
        Path output = reportFileFor(username);
        String termCode = calc.termValue() + TermUnit.valueOf(calc.termUnit()).shortCode();

        List<String> command = List.of(
                pythonCommand, script.toString(),
                "--output", output.toString(),
                "--username", username,
                "--principal", calc.principal().toPlainString(),
                "--term", termCode,
                "--base-rate", calc.baseRate().toPlainString(),
                "--addon", calc.appliedAddOn().toPlainString(),
                "--final-rate", calc.effectiveRate().toPlainString(),
                "--maturity", calc.maturityAmount().toPlainString());

        String processOutput;
        int exitCode;
        try {
            Files.createDirectories(output.getParent());
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            processOutput = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            if (!process.waitFor(30, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                throw CalculationException.internal("REPORT_TIMEOUT", "Python report script took too long.", List.of());
            }
            exitCode = process.exitValue();
        } catch (IOException e) {
            throw CalculationException.internal("PYTHON_NOT_FOUND",
                    "Could not start Python using the command '" + pythonCommand + "'. "
                            + "Check calculator.report.python-command in application.properties "
                            + "(use python on Windows, python3 on Mac).",
                    List.of(e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw CalculationException.internal("REPORT_INTERRUPTED", "Report generation was interrupted.", List.of());
        }

        if (exitCode != 0) {
            throw CalculationException.internal("REPORT_FAILED",
                    "The Python report script failed (exit code " + exitCode + ").", List.of(processOutput));
        }
        try {
            // The script prints one JSON line such as {"status":"ok","file":"...","row":"...","rowsInFile":3}
            String lastLine = processOutput.substring(processOutput.lastIndexOf('\n') + 1);
            JsonNode json = objectMapper.readTree(lastLine);
            return new ReportResult(output.getFileName().toString(),
                    json.path("row").asText(),
                    json.path("rowsInFile").asInt());
        } catch (IOException e) {
            throw CalculationException.internal("REPORT_OUTPUT_UNREADABLE",
                    "The Python script ran but its output could not be read.", List.of(processOutput));
        }
    }

    /** Location of a user's CSV report, e.g. reports/fd_report_alice123.csv */
    public Path reportFileFor(String username) {
        String safeName = username.replaceAll("[^A-Za-z0-9_-]", "_");
        return Paths.get(outputDir, "fd_report_" + safeName + ".csv").toAbsolutePath();
    }
}
