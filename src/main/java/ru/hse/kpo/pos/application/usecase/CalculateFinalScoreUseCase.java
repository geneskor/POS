package ru.hse.kpo.pos.application.usecase;

import ru.hse.kpo.pos.application.port.out.EvaluationRepository;
import ru.hse.kpo.pos.application.port.out.StudentRepository;
import ru.hse.kpo.pos.domain.model.Rubricator;
import ru.hse.kpo.pos.domain.service.FinalScoreService;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class CalculateFinalScoreUseCase {
    private final StudentRepository studentRepository;
    private final EvaluationRepository evaluationRepository;
    private final Rubricator rubricator;
    private final FinalScoreService finalScoreService;

    public CalculateFinalScoreUseCase(StudentRepository studentRepository, EvaluationRepository evaluationRepository, Rubricator rubricator, FinalScoreService finalScoreService) {
        this.studentRepository = Objects.requireNonNull(studentRepository);
        this.evaluationRepository = Objects.requireNonNull(evaluationRepository);
        this.rubricator = Objects.requireNonNull(rubricator);
        this.finalScoreService = Objects.requireNonNull(finalScoreService);
    }

    public BigDecimal execute(UUID studentId) {
        studentRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Студент не найден"));
        return finalScoreService.calculate(rubricator, evaluationRepository.findByStudent(studentId));
    }
}
