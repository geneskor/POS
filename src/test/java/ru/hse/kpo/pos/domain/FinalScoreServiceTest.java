package ru.hse.kpo.pos.domain;

import org.junit.jupiter.api.Test;
import ru.hse.kpo.pos.domain.model.*;
import ru.hse.kpo.pos.domain.service.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class FinalScoreServiceTest {
    @Test
    void calculatesWeightedFinalScore() {
        Rubricator rubricator = new Rubricator();
        Criterion homework = new Criterion(UUID.randomUUID(), "Домашнее задание", 10, new BigDecimal("2"));
        Criterion exam = new Criterion(UUID.randomUUID(), "Экзамен", 20, BigDecimal.ONE);
        rubricator.addCriterion(homework);
        rubricator.addCriterion(exam);

        UUID studentId = UUID.randomUUID();
        Evaluation first = new EvaluationFactory(rubricator).create(studentId, homework.getId(), new Score(8));
        Evaluation second = new EvaluationFactory(rubricator).create(studentId, exam.getId(), new Score(10));

        BigDecimal result = new FinalScoreService(new AllCriteriaWeightedStrategy()).calculate(rubricator, List.of(first, second));

        assertEquals(new BigDecimal("70.00"), result);
    }

    @Test
    void missingCriteriaCountAsZero() {
        Rubricator rubricator = new Rubricator();
        Criterion homework = new Criterion(UUID.randomUUID(), "Домашнее задание", 10, new BigDecimal("2"));
        Criterion exam = new Criterion(UUID.randomUUID(), "Экзамен", 20, BigDecimal.ONE);
        rubricator.addCriterion(homework);
        rubricator.addCriterion(exam);

        Evaluation evaluation = new EvaluationFactory(rubricator).create(UUID.randomUUID(), homework.getId(), new Score(8));
        BigDecimal result = new FinalScoreService(new AllCriteriaWeightedStrategy()).calculate(rubricator, List.of(evaluation));

        assertEquals(new BigDecimal("53.33"), result);
    }

    @Test
    void gradedCriteriaStrategyIgnoresMissingScores() {
        Rubricator rubricator = new Rubricator();
        Criterion homework = new Criterion(UUID.randomUUID(), "Домашнее задание", 10, new BigDecimal("2"));
        Criterion exam = new Criterion(UUID.randomUUID(), "Экзамен", 20, BigDecimal.ONE);
        rubricator.addCriterion(homework);
        rubricator.addCriterion(exam);

        Evaluation evaluation = new EvaluationFactory(rubricator).create(UUID.randomUUID(), homework.getId(), new Score(8));
        BigDecimal result = new FinalScoreService(new GradedCriteriaWeightedStrategy()).calculate(rubricator, List.of(evaluation));

        assertEquals(new BigDecimal("80.00"), result);
    }

    @Test
    void rejectsScoreAboveCurrentRubricatorMaximum() {
        UUID criterionId = UUID.randomUUID();

        Rubricator original = new Rubricator();
        original.addCriterion(new Criterion(criterionId, "Экзамен", 20, BigDecimal.ONE));

        UUID studentId = UUID.randomUUID();
        Evaluation evaluation = new EvaluationFactory(original).create(studentId, criterionId, new Score(20));

        Rubricator current = new Rubricator();
        current.addCriterion(new Criterion(criterionId, "Экзамен", 10, BigDecimal.ONE));

        FinalScoreService service = new FinalScoreService(new AllCriteriaWeightedStrategy());

        assertThrows(IllegalArgumentException.class,
            () -> service.calculate(current, List.of(evaluation)));
    }

    @Test
    void rejectsEvaluationsOfDifferentStudents() {
        Rubricator rubricator = new Rubricator();

        Criterion homework = new Criterion(UUID.randomUUID(), "Домашнее задание", 10, BigDecimal.ONE);
        Criterion exam = new Criterion(UUID.randomUUID(), "Экзамен", 10, BigDecimal.ONE);

        rubricator.addCriterion(homework);
        rubricator.addCriterion(exam);

        EvaluationFactory factory = new EvaluationFactory(rubricator);

        Evaluation ivan = factory.create(UUID.randomUUID(), homework.getId(), new Score(10));
        Evaluation anna = factory.create(UUID.randomUUID(), exam.getId(), new Score(5));

        FinalScoreService service = new FinalScoreService(new AllCriteriaWeightedStrategy());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
            () -> service.calculate(rubricator, List.of(ivan, anna)));

        assertEquals("Оценки относятся к разным студентам", error.getMessage());
    }

    @Test
    void rejectsEmptyRubricator() {
        Rubricator rubricator = new Rubricator();

        assertThrows(IllegalStateException.class, () -> new FinalScoreService(new AllCriteriaWeightedStrategy()).calculate(rubricator, List.of()));
    }
}
