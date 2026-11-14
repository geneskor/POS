package ru.hse.kpo.pos.application.usecase;

import ru.hse.kpo.pos.application.port.out.EvaluationRepository;
import ru.hse.kpo.pos.application.port.out.StudentRepository;
import ru.hse.kpo.pos.domain.model.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final StudentRepository studentRepository;
    private final EvaluationFactory evaluationFactory;

    public EvaluationService(EvaluationRepository evaluationRepository, StudentRepository studentRepository, EvaluationFactory evaluationFactory) {
        this.evaluationRepository = Objects.requireNonNull(evaluationRepository);
        this.studentRepository = Objects.requireNonNull(studentRepository);
        this.evaluationFactory = Objects.requireNonNull(evaluationFactory);
    }

    public Evaluation setScore(UUID studentId, UUID criterionId, int value) {
        studentRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Студент не найден"));
        Score score = new Score(value);
        Evaluation evaluation = evaluationRepository.findByStudentAndCriterion(studentId, criterionId).orElse(null);

        if (evaluation == null) {
            evaluation = evaluationFactory.create(studentId, criterionId, score);
        } else {
            evaluation.changeScore(score);
        }

        evaluationRepository.save(evaluation);
        return evaluation;
    }

    public boolean cancelScore(UUID studentId, UUID criterionId) {
        studentRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Студент не найден"));
        return evaluationRepository.deleteByStudentAndCriterion(studentId, criterionId);
    }

    public List<Evaluation> getStudentEvaluations(UUID studentId) {
        studentRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Студент не найден"));
        return evaluationRepository.findByStudent(studentId);
    }
}
