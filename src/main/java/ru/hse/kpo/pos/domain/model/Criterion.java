package ru.hse.kpo.pos.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class Criterion {
    private final UUID id;
    private final String name;
    private final int maxScore;
    private final BigDecimal weight;

    public Criterion(UUID id, String name, int maxScore, BigDecimal weight) {
        this.id = Objects.requireNonNull(id, "ID критерия обязателен");

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название критерия не может быть пустым");
        }

        if (maxScore <= 0) {
            throw new IllegalArgumentException("Максимальный балл должен быть положительным");
        }

        if (weight == null || weight.signum() <= 0) {
            throw new IllegalArgumentException("Вес критерия должен быть положительным");
        }

        this.name = name.trim();
        this.maxScore = maxScore;
        this.weight = weight;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}
