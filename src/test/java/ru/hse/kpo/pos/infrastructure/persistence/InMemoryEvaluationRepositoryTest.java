package ru.hse.kpo.pos.infrastructure.persistence;

import org.junit.jupiter.api.Test;
import ru.hse.kpo.pos.domain.model.*;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryEvaluationRepositoryTest {
    private final Rubricator rubricator = new Rubricator();
    private final InMemoryEvaluationRepository repository = new InMemoryEvaluationRepository();

    private Criterion addCriterion() {
        Criterion criterion = new Criterion(UUID.randomUUID(), "Экзамен", 10, BigDecimal.ONE);
        rubricator.addCriterion(criterion);
        return criterion;
    }

    private Evaluation createEvaluation(UUID studentId, Criterion criterion, int score) {
        return new EvaluationFactory(rubricator).create(studentId, criterion.getId(), new Score(score));
    }

    @Test
    void rejectsDuplicateForSameStudentAndCriterion() {
        Criterion criterion = addCriterion();
        UUID studentId = UUID.randomUUID();

        Evaluation first = createEvaluation(studentId, criterion, 8);
        Evaluation duplicate = createEvaluation(studentId, criterion, 5);

        repository.save(first);

        assertThrows(IllegalArgumentException.class, () -> repository.save(duplicate));
        assertEquals(1, repository.findByStudent(studentId).size());
        assertEquals(8, repository.findByStudent(studentId).get(0).getScore().value());
    }

    @Test
    void allowsUpdatingExistingEvaluation() {
        Criterion criterion = addCriterion();
        UUID studentId = UUID.randomUUID();
        Evaluation evaluation = createEvaluation(studentId, criterion, 8);

        repository.save(evaluation);
        evaluation.changeScore(new Score(6));
        repository.save(evaluation);

        assertEquals(1, repository.findByStudent(studentId).size());
        assertEquals(6, repository.findByStudent(studentId).get(0).getScore().value());
    }

    @Test
    void allowsDifferentStudentsToUseSameCriterion() {
        Criterion criterion = addCriterion();
        UUID firstStudent = UUID.randomUUID();
        UUID secondStudent = UUID.randomUUID();

        repository.save(createEvaluation(firstStudent, criterion, 8));
        repository.save(createEvaluation(secondStudent, criterion, 6));

        assertEquals(1, repository.findByStudent(firstStudent).size());
        assertEquals(1, repository.findByStudent(secondStudent).size());
    }
}
