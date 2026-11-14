package ru.hse.kpo.pos.application.usecase;

import ru.hse.kpo.pos.application.port.out.ResultExporter;
import ru.hse.kpo.pos.application.port.out.StudentRepository;
import ru.hse.kpo.pos.domain.model.Student;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ExportResultsUseCase {
    private final StudentRepository studentRepository;
    private final CalculateFinalScoreUseCase finalScoreUseCase;
    private final ResultExporter resultExporter;

    public ExportResultsUseCase(StudentRepository studentRepository, CalculateFinalScoreUseCase finalScoreUseCase, ResultExporter resultExporter) {
        this.studentRepository = Objects.requireNonNull(studentRepository);
        this.finalScoreUseCase = Objects.requireNonNull(finalScoreUseCase);
        this.resultExporter = Objects.requireNonNull(resultExporter);
    }

    public String execute() {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) throw new IllegalStateException("Нет студентов для экспорта");

        List<ResultExporter.ResultRow> rows = new ArrayList<>();

        for (Student student : students) {
            rows.add(new ResultExporter.ResultRow(student.getId(), student.getName(), finalScoreUseCase.execute(student.getId())));
        }

        return resultExporter.export(rows);
    }
}
