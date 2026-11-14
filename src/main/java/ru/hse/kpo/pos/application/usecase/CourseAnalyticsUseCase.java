package ru.hse.kpo.pos.application.usecase;

import ru.hse.kpo.pos.application.port.out.StudentRepository;
import ru.hse.kpo.pos.domain.model.Student;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public final class CourseAnalyticsUseCase {
    private final StudentRepository studentRepository;
    private final CalculateFinalScoreUseCase finalScoreUseCase;

    public CourseAnalyticsUseCase(StudentRepository studentRepository, CalculateFinalScoreUseCase finalScoreUseCase) {
        this.studentRepository = Objects.requireNonNull(studentRepository);
        this.finalScoreUseCase = Objects.requireNonNull(finalScoreUseCase);
    }

    public Statistics execute() {
        List<Student> students = studentRepository.findAll();
        if (students.isEmpty()) throw new IllegalStateException("Нет студентов для анализа");

        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal minimum = null;
        BigDecimal maximum = null;

        for (Student student : students) {
            BigDecimal score = finalScoreUseCase.execute(student.getId());
            sum = sum.add(score);
            minimum = minimum == null ? score : minimum.min(score);
            maximum = maximum == null ? score : maximum.max(score);
        }

        BigDecimal average = sum.divide(BigDecimal.valueOf(students.size()), 2, RoundingMode.HALF_UP);
        return new Statistics(students.size(), average, minimum, maximum);
    }

    public record Statistics(int studentCount, BigDecimal average, BigDecimal minimum, BigDecimal maximum) {}
}
