package ru.hse.kpo.pos.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class Evaluation {
    private final UUID id;
    private final UUID studentId;
    private final Criterion criterion;
    private Score score;

    Evaluation(UUID studentId, Criterion criterion, Score score) {
        this.id = UUID.randomUUID();
        this.studentId = Objects.requireNonNull(studentId, "ID студента обязателен");
        this.criterion = Objects.requireNonNull(criterion, "Критерий обязателен");
        changeScore(score);
    }

    public UUID getId() {
        return id;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public UUID getCriterionId() {
        return criterion.getId();
    }

    public Score getScore() {
        return score;
    }

    public void changeScore(Score newScore) {
        Objects.requireNonNull(newScore, "Балл обязателен");

        if (newScore.value() > criterion.getMaxScore()) {
            throw new IllegalArgumentException("Балл превышает максимум критерия");
        }

        this.score = newScore;
    }
}
