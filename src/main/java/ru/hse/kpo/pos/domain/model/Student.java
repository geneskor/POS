package ru.hse.kpo.pos.domain.model;

import java.util.Objects;
import java.util.UUID;

public final class Student {
    private final UUID id;
    private String name;

    public Student(UUID id, String name) {
        this.id = Objects.requireNonNull(id, "ID студента обязателен");
        rename(name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Имя студента не может быть пустым");
        }

        this.name = newName.trim();
    }
}
