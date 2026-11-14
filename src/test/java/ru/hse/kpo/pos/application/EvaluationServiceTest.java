package ru.hse.kpo.pos.application;

import org.junit.jupiter.api.Test;
import ru.hse.kpo.pos.application.port.out.*;
import ru.hse.kpo.pos.application.usecase.EvaluationService;
import ru.hse.kpo.pos.domain.model.*;
import ru.hse.kpo.pos.domain.service.*;
import ru.hse.kpo.pos.infrastructure.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class EvaluationServiceTest {
    @Test
    void cancelledEvaluationDisappearsFromResults() {
        Student student = new Student(UUID.randomUUID(), "Иван Иванов");
        StudentRepository students = new InMemoryStudentRepository();
        students.save(student);

        Rubricator rubricator = new Rubricator();
        Criterion criterion = new Criterion(UUID.randomUUID(), "Экзамен", 10, BigDecimal.ONE);
        rubricator.addCriterion(criterion);

        EvaluationRepository evaluations = new InMemoryEvaluationRepository();
        EvaluationService service = new EvaluationService(evaluations, students, new EvaluationFactory(rubricator));

        service.setScore(student.getId(), criterion.getId(), 8);

        assertTrue(service.cancelScore(student.getId(), criterion.getId()));
        assertTrue(service.getStudentEvaluations(student.getId()).isEmpty());
        assertFalse(service.cancelScore(student.getId(), criterion.getId()));

        BigDecimal result = new FinalScoreService(new AllCriteriaWeightedStrategy()).calculate(rubricator, evaluations.findByStudent(student.getId()));
        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void cannotCancelEvaluationOfUnknownStudent() {
        StudentRepository students = new InMemoryStudentRepository();
        EvaluationRepository evaluations = new InMemoryEvaluationRepository();
        EvaluationService service = new EvaluationService(evaluations, students, new EvaluationFactory(new Rubricator()));

        assertThrows(IllegalArgumentException.class, () -> service.cancelScore(UUID.randomUUID(), UUID.randomUUID()));
    }
}
