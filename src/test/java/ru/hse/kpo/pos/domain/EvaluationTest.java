package ru.hse.kpo.pos.domain;

import org.junit.jupiter.api.Test;
import ru.hse.kpo.pos.domain.model.*;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class EvaluationTest {
    private final Rubricator rubricator = new Rubricator();
    private final Criterion criterion = new Criterion(UUID.randomUUID(), "Домашнее задание", 10, BigDecimal.ONE);

    @Test
    void scoreCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Score(-1));
    }

    @Test
    void scoreCannotExceedMaximum() {
        rubricator.addCriterion(criterion);
        assertThrows(IllegalArgumentException.class, () -> new EvaluationFactory(rubricator).create(UUID.randomUUID(), criterion.getId(), new Score(11)));
    }

    @Test
    void criterionMustExistInRubricator() {
        assertThrows(IllegalArgumentException.class, () -> new EvaluationFactory(rubricator).create(UUID.randomUUID(), criterion.getId(), new Score(5)));
    }

    @Test
    void scoreCanBeChanged() {
        rubricator.addCriterion(criterion);
        Evaluation evaluation = new EvaluationFactory(rubricator).create(UUID.randomUUID(), criterion.getId(), new Score(5));

        evaluation.changeScore(new Score(8));

        assertEquals(8, evaluation.getScore().value());
    }

    @Test
    void changedScoreCannotExceedMaximum() {
        rubricator.addCriterion(criterion);
        Evaluation evaluation = new EvaluationFactory(rubricator).create(UUID.randomUUID(), criterion.getId(), new Score(5));

        assertThrows(IllegalArgumentException.class, () -> evaluation.changeScore(new Score(12)));
        assertEquals(5, evaluation.getScore().value());
    }
}
