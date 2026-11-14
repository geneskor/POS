package ru.hse.kpo.pos.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class EvaluationFactory {
    private final Rubricator rubricator;

    public EvaluationFactory(Rubricator rubricator) {
        this.rubricator = Objects.requireNonNull(rubricator, "Рубрикатор обязателен");
    }

    public Evaluation create(UUID studentId, UUID criterionId, Score score) {
        Criterion criterion = rubricator.getCriterion(criterionId);
        return new Evaluation(studentId, criterion, score);
    }
}
