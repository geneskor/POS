package ru.hse.kpo.pos.domain.service;

import ru.hse.kpo.pos.domain.model.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class FinalScoreService {
    private final FinalScoreStrategy strategy;

    public FinalScoreService(FinalScoreStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "Стратегия расчёта обязательна");
    }

    public BigDecimal calculate(Rubricator rubricator, List<Evaluation> evaluations) {
        Objects.requireNonNull(rubricator, "Рубрикатор обязателен");
        Objects.requireNonNull(evaluations, "Список оценок обязателен");

        if (rubricator.getCriteria().isEmpty()) {
            throw new IllegalStateException("Рубрикатор не содержит критериев");
        }

        Map<UUID, Integer> scores = new HashMap<>();
        UUID studentId = null;

        for (Evaluation evaluation : evaluations) {
            Objects.requireNonNull(evaluation, "Оценка не может быть null");

            UUID currentStudentId = Objects.requireNonNull(evaluation.getStudentId(), "ID студента обязателен");
            if (studentId == null) {
                studentId = currentStudentId;
            } else if (!studentId.equals(currentStudentId)) {
                throw new IllegalArgumentException("Оценки относятся к разным студентам");
            }
            Criterion currentCriterion = rubricator.getCriterion(evaluation.getCriterionId());

            if (evaluation.getScore().value() > currentCriterion.getMaxScore()) {
                throw new IllegalArgumentException("Балл превышает максимум критерия рубрикатора");
            }

            if (scores.putIfAbsent(evaluation.getCriterionId(), evaluation.getScore().value()) != null) {
                throw new IllegalArgumentException("Повторная оценка по одному критерию");
            }
        }

        return strategy.calculate(rubricator.getCriteria(), Map.copyOf(scores));
    }
}
