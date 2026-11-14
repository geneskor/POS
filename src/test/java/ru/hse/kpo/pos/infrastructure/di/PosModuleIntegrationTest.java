package ru.hse.kpo.pos.infrastructure.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;
import ru.hse.kpo.pos.application.PosFacade;
import ru.hse.kpo.pos.application.port.out.*;
import ru.hse.kpo.pos.domain.model.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PosModuleIntegrationTest {
    @Test
    void repositoriesAndRubricatorAreSingletons() {
        Injector injector = Guice.createInjector(new PosModule());

        assertSame(injector.getInstance(StudentRepository.class), injector.getInstance(StudentRepository.class));
        assertSame(injector.getInstance(EvaluationRepository.class), injector.getInstance(EvaluationRepository.class));
        assertSame(injector.getInstance(Rubricator.class), injector.getInstance(Rubricator.class));
    }

    @Test
    void facadeWorksWithInjectedDependencies() {
        Injector injector = Guice.createInjector(new PosModule());
        PosFacade facade = injector.getInstance(PosFacade.class);

        Student student = facade.addStudent("Анна");
        Criterion criterion = facade.addCriterion("Экзамен", 10, BigDecimal.ONE);
        facade.setScore(student.getId(), criterion.getId(), 7);

        assertEquals(new BigDecimal("70.00"), facade.getStudentResult(student.getId()).finalScore());
        assertEquals(1, injector.getInstance(StudentRepository.class).findAll().size());
        assertEquals(1, injector.getInstance(EvaluationRepository.class).findByStudent(student.getId()).size());
    }
}
