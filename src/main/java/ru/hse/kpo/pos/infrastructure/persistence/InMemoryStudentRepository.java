package ru.hse.kpo.pos.infrastructure.persistence;

import ru.hse.kpo.pos.application.port.out.StudentRepository;
import ru.hse.kpo.pos.domain.model.Student;
import java.util.*;

public final class InMemoryStudentRepository implements StudentRepository {
    private final Map<UUID, Student> students = new LinkedHashMap<>();

    @Override
    public void save(Student student) {
        Objects.requireNonNull(student, "Студент обязателен");
        students.put(student.getId(), student);
    }

    @Override
    public Optional<Student> findById(UUID id) {
        return Optional.ofNullable(students.get(id));
    }

    @Override
    public List<Student> findAll() {
        return List.copyOf(students.values());
    }
}
