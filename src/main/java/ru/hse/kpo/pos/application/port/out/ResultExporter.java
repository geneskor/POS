package ru.hse.kpo.pos.application.port.out;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ResultExporter {
    String export(List<ResultRow> rows);

    record ResultRow(UUID studentId, String studentName, BigDecimal finalScore) {}
}
