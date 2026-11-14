package ru.hse.kpo.pos.domain;

import org.junit.jupiter.api.Test;
import ru.hse.kpo.pos.application.CriterionInput;
import ru.hse.kpo.pos.application.usecase.RubricatorService;
import ru.hse.kpo.pos.domain.model.*;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RubricatorBuilderTest {
    @Test
    void buildsRubricatorWithMultipleCriteria() {
        Rubricator rubricator = new RubricatorBuilder()
            .addCriterion("Домашнее задание", 10, new BigDecimal("2"))
            .addCriterion("Экзамен", 20, BigDecimal.ONE)
            .build();

        assertEquals(2, rubricator.getCriteria().size());
        assertEquals("Домашнее задание", rubricator.getCriteria().get(0).getName());
        assertEquals("Экзамен", rubricator.getCriteria().get(1).getName());
    }

    @Test
    void invalidBatchDoesNotChangeRubricator() {
        Rubricator rubricator = new Rubricator();
        RubricatorService service = new RubricatorService(rubricator);

        List<CriterionInput> inputs = List.of(
            new CriterionInput("Домашнее задание", 10, BigDecimal.ONE),
            new CriterionInput("Экзамен", -5, BigDecimal.ONE)
        );

        assertThrows(IllegalArgumentException.class, () -> service.addCriteria(inputs));
        assertTrue(rubricator.getCriteria().isEmpty());
    }

    @Test
    void addsBatchToExistingRubricator() {
        Rubricator rubricator = new Rubricator();
        RubricatorService service = new RubricatorService(rubricator);

        service.addCriterion("Посещение", 10, BigDecimal.ONE);
        service.addCriteria(List.of(
            new CriterionInput("Домашнее задание", 10, new BigDecimal("2")),
            new CriterionInput("Экзамен", 20, BigDecimal.ONE)
        ));

        assertEquals(3, rubricator.getCriteria().size());
    }
}
