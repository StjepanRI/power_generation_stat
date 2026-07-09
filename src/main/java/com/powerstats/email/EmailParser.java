package com.powerstats.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class EmailParser {

    /**
     * Parses a table from email body content
     * Extracts rows and columns from HTML or plain text table format
     */
    public List<String[]> parseEmailTable(String emailContent) {
        List<String[]> tableData = new ArrayList<>();

        if (emailContent == null || emailContent.trim().isEmpty()) {
            log.warn("Email content is empty");
            return tableData;
        }

        // Try HTML table parsing first
        if (emailContent.contains("<table")) {
            tableData = parseHtmlTable(emailContent);
        } else {
            // Fall back to plain text table parsing
            tableData = parsePlainTextTable(emailContent);
        }

        log.info("Parsed {} rows from email content", tableData.size());
        return tableData;
    }

    /**
     * Parses HTML table format
     */
    private List<String[]> parseHtmlTable(String htmlContent) {
        List<String[]> tableData = new ArrayList<>();

        // Extract table rows
        Pattern rowPattern = Pattern.compile("<tr[^>]*>(.*?)</tr>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher rowMatcher = rowPattern.matcher(htmlContent);

        while (rowMatcher.find()) {
            String rowContent = rowMatcher.group(1);
            List<String> cells = new ArrayList<>();

            // Extract cells (td or th)
            Pattern cellPattern = Pattern.compile("<t[dh][^>]*>(.*?)</t[dh]>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher cellMatcher = cellPattern.matcher(rowContent);

            while (cellMatcher.find()) {
                String cellContent = cellMatcher.group(1)
                        .replaceAll("<[^>]*>", "") // Remove HTML tags
                        .trim();
                cells.add(cellContent);
            }

            if (!cells.isEmpty()) {
                tableData.add(cells.toArray(new String[0]));
            }
        }

        return tableData;
    }

    /**
     * Parses plain text table format (pipe-delimited or space-separated)
     */
    private List<String[]> parsePlainTextTable(String textContent) {
        List<String[]> tableData = new ArrayList<>();
        String[] lines = textContent.split("\n");

        for (String line : lines) {
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] cells;
            if (line.contains("|")) {
                // Pipe-delimited
                cells = line.split("\\|")
                        = line.split("\\|");
            } else if (line.contains("\t")) {
                // Tab-delimited
                cells = line.split("\t");
            } else {
                // Space-separated (be cautious)
                cells = line.trim().split("\\s+");
            }

            // Clean up cells
            for (int i = 0; i < cells.length; i++) {
                cells[i] = cells[i].trim();
            }

            tableData.add(cells);
        }

        return tableData;
    }

    /**
     * Extracts numeric values from cell strings
     */
    public Double extractNumericValue(String cellValue) {
        if (cellValue == null || cellValue.trim().isEmpty()) {
            return null;
        }

        try {
            // Remove common suffixes (kWh, MW, %, etc.)
            String cleanValue = cellValue.replaceAll("[^0-9.,\\-]", "").trim();
            cleanValue = cleanValue.replace(",", ".");
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            log.warn("Could not parse numeric value from: {}", cellValue);
            return null;
        }
    }
}
