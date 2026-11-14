package ru.hse.kpo.pos.application;

import org.junit.jupiter.api.Test;
import ru.hse.kpo.pos.application.port.out.StudentRepository;
import ru.hse.kpo.pos.application.usecase.StudentService;
import ru.hse.kpo.pos.domain.model.Student;
import ru.hse.kpo.pos.infrastructure.persistence.InMemoryStudentRepository;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {
    private final StudentRepository repository = new InMemoryStudentRepository();
    private final StudentService service = new StudentService(repository);

    @Test
    void renamesStudent() {
        Student student = service.addStudent("Иван Иванов");

        service.renameStudent(student.getId(), "Пётр Петров");

        assertEquals("Пётр Петров", repository.findById(student.getId()).orElseThrow().getName());
    }

    @Test
    void rejectsEmptyName() {
        Student student = service.addStudent("Иван Иванов");

        assertThrows(IllegalArgumentException.class, () -> service.renameStudent(student.getId(), " "));
        assertEquals("Иван Иванов", student.getName());
    }

    @Test
    void rejectsUnknownStudent() {
        assertThrows(IllegalArgumentException.class, () -> service.renameStudent(UUID.randomUUID(), "Пётр Петров"));
    }
}
