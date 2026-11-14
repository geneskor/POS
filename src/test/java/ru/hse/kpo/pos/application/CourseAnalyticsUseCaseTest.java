package ru.hse.kpo.pos.application;

import org.junit.jupiter.api.Test;
import ru.hse.kpo.pos.application.port.out.*;
import ru.hse.kpo.pos.application.usecase.*;
import ru.hse.kpo.pos.domain.model.*;
import ru.hse.kpo.pos.domain.service.*;
import ru.hse.kpo.pos.infrastructure.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CourseAnalyticsUseCaseTest {
    private final StudentRepository students = new InMemoryStudentRepository();
    private final EvaluationRepository evaluations = new InMemoryEvaluationRepository();
    private final Rubricator rubricator = new Rubricator();

    private CourseAnalyticsUseCase createUseCase() {
        var calculator = new FinalScoreService(new AllCriteriaWeightedStrategy());
        var finalScore = new CalculateFinalScoreUseCase(students, evaluations, rubricator, calculator);
        return new CourseAnalyticsUseCase(students, finalScore);
    }

    private void addStudentWithScore(String name, int score, Criterion criterion) {
        Student student = new Student(UUID.randomUUID(), name);
        students.save(student);
        evaluations.save(new EvaluationFactory(rubricator).create(student.getId(), criterion.getId(), new Score(score)));
    }

    @Test
    void calculatesCourseStatistics() {
        Criterion criterion = new Criterion(UUID.randomUUID(), "Экзамен", 10, BigDecimal.ONE);
        rubricator.addCriterion(criterion);
        addStudentWithScore("Иван", 8, criterion);
        addStudentWithScore("Анна", 6, criterion);

        var result = createUseCase().execute();

        assertEquals(2, result.studentCount());
        assertEquals(new BigDecimal("70.00"), result.average());
        assertEquals(new BigDecimal("60.00"), result.minimum());
        assertEquals(new BigDecimal("80.00"), result.maximum());
    }

    @Test
    void missingGradesCountAsZero() {
        Criterion criterion = new Criterion(UUID.randomUUID(), "Экзамен", 10, BigDecimal.ONE);
        rubricator.addCriterion(criterion);
        addStudentWithScore("Иван", 8, criterion);
        students.save(new Student(UUID.randomUUID(), "Анна"));

        var result = createUseCase().execute();

        assertEquals(new BigDecimal("40.00"), result.average());
        assertEquals(new BigDecimal("0.00"), result.minimum());
    }

    @Test
    void rejectsCourseWithoutStudents() {
        assertThrows(IllegalStateException.class, () -> createUseCase().execute());
    }
}
