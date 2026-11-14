package ru.hse.kpo.pos.application.port.out;

import ru.hse.kpo.pos.domain.model.Evaluation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluationRepository {
    void save(Evaluation evaluation);
    Optional<Evaluation> findByStudentAndCriterion(UUID studentId, UUID criterionId);
    List<Evaluation> findByStudent(UUID studentId);
    boolean deleteByStudentAndCriterion(UUID studentId, UUID criterionId);
}
