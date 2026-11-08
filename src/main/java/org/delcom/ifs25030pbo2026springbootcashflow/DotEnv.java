package org.delcom.ifs25030pbo2026springbootcashflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Memuat file .env ke System properties sebelum Spring Boot start.
 * Nilai yang sudah diset lewat environment / -D tidak ditimpa.
 */
public final class DotEnv {

    private DotEnv() {
    }

    /** Memuat file ".env" dari working directory (jika ada). */
    public static void load() {
        load(Path.of(".env"));
    }

    /** Memuat file env dari path tertentu (jika ada). */
    public static void load(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(path);
            for (String raw : lines) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int idx = line.indexOf('=');
                if (idx <= 0) {
                    continue;
                }
                String key = line.substring(0, idx).trim();
                String value = stripQuotes(line.substring(idx + 1).trim());
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Gagal membaca file " + path, e);
        }
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}