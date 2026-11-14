package ru.hse.kpo.pos.infrastructure.export;

import ru.hse.kpo.pos.application.port.out.ResultExporter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class CsvResultExporter implements ResultExporter {
    private final Path path;

    public CsvResultExporter(Path path) {
        this.path = Objects.requireNonNull(path);
    }

    @Override
    public String export(List<ResultRow> rows) {
        StringBuilder csv = new StringBuilder("ID студента;Студент;Итог (%)\n");

        for (ResultRow row : rows) {
            csv.append(row.studentId()).append(';');
            csv.append(escape(row.studentName())).append(';');
            csv.append(row.finalScore().toPlainString()).append('\n');
        }

        try {
            Files.writeString(path, csv.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка экспорта: " + e.getMessage(), e);
        }

        return path.toAbsolutePath().toString();
    }

    private String escape(String value) {
        if (!value.isEmpty() && "=+-@\t\r".indexOf(value.charAt(0)) >= 0) value = "'" + value;
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
