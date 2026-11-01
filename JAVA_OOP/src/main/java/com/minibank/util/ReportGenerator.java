package com.minibank.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public final class ReportGenerator {
    private ReportGenerator() { }
    public static String generate(Path logsDir, Path reportFile) throws IOException {
        long deposits = 0, withdrawals = 0; StringBuilder details = new StringBuilder();
        if (Files.isDirectory(logsDir)) try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir, "*.log")) {
            for (Path file : stream) {
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                details.append(file.getFileName()).append(" size=").append(attrs.size()).append(" modified=").append(attrs.lastModifiedTime()).append(System.lineSeparator());
                try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) { String line; while ((line = reader.readLine()) != null) { String[] parts = line.split("\\|"); if (parts.length >= 3) try { long amount = Long.parseLong(parts[2]); if (parts[1].equals("DEPOSIT")) deposits = Math.addExact(deposits, amount); else if (parts[1].equals("WITHDRAW")) withdrawals = Math.addExact(withdrawals, amount); } catch (NumberFormatException ignored) { } } }
            }
        }
        String report = "MiniBank end-of-day report" + System.lineSeparator() + "Total deposits: ₹" + deposits + System.lineSeparator() + "Total withdrawals: ₹" + withdrawals + System.lineSeparator() + details;
        if (reportFile.getParent() != null) Files.createDirectories(reportFile.getParent()); Files.writeString(reportFile, report, StandardCharsets.UTF_8); return report;
    }
}
