package ru.hse.kpo.pos.infrastructure.persistence;

import ru.hse.kpo.pos.application.port.out.EvaluationRepository;
import ru.hse.kpo.pos.domain.model.Evaluation;
import java.util.*;

public final class InMemoryEvaluationRepository implements EvaluationRepository {
    private final Map<UUID, Evaluation> evaluations = new LinkedHashMap<>();

    @Override
    public void save(Evaluation evaluation) {
        if (evaluation == null) {
            throw new IllegalArgumentException("Оценка не может быть null");
        }

        for (Evaluation existing : evaluations.values()) {
            boolean sameStudent = existing.getStudentId().equals(evaluation.getStudentId());
            boolean sameCriterion = existing.getCriterionId().equals(evaluation.getCriterionId());
            boolean differentEvaluation = !existing.getId().equals(evaluation.getId());

            if (sameStudent && sameCriterion && differentEvaluation) {
                throw new IllegalArgumentException(
                    "Оценка по этому критерию для студента уже существует"
                );
            }
        }

        evaluations.put(evaluation.getId(), evaluation);
    }

    @Override
    public Optional<Evaluation> findByStudentAndCriterion(UUID studentId, UUID criterionId) {
        return evaluations.values().stream()
            .filter(e -> e.getStudentId().equals(studentId) && e.getCriterionId().equals(criterionId))
            .findFirst();
    }

    @Override
    public boolean deleteByStudentAndCriterion(UUID studentId, UUID criterionId) {
        return evaluations.values().removeIf(e -> e.getStudentId().equals(studentId) && e.getCriterionId().equals(criterionId));
    }

    @Override
    public List<Evaluation> findByStudent(UUID studentId) {
        return evaluations.values().stream().filter(e -> e.getStudentId().equals(studentId)).toList();
    }
}
