package ru.hse.kpo.pos.domain.model;

public record Score(int value) {
    public Score {
        if (value < 0) {
            throw new IllegalArgumentException("Балл не может быть отрицательным");
        }
    }
}
