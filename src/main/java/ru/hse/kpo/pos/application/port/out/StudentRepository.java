package ru.hse.kpo.pos.application.port.out;

import ru.hse.kpo.pos.domain.model.Student;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRepository {
    void save(Student student);
    Optional<Student> findById(UUID id);
    List<Student> findAll();
}
