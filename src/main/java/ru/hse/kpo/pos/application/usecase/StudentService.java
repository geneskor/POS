package ru.hse.kpo.pos.application.usecase;

import ru.hse.kpo.pos.application.port.out.StudentRepository;
import ru.hse.kpo.pos.domain.model.Student;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = Objects.requireNonNull(studentRepository, "Репозиторий студентов обязателен");
    }

    public Student addStudent(String name) {
        Student student = new Student(UUID.randomUUID(), name);
        studentRepository.save(student);
        return student;
    }

    public Student renameStudent(UUID studentId, String newName) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Студент не найден"));
        student.rename(newName);
        studentRepository.save(student);
        return student;
    }

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }
}
