package com.minibank.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public final class TransactionLog {
    private TransactionLog() { }
    public static synchronized void append(Path file, String line) throws IOException { if (file.getParent() != null) Files.createDirectories(file.getParent()); Files.write(file, (line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND); }
}
