package ru.hse.kpo.pos.application.usecase;

import ru.hse.kpo.pos.application.CriterionInput;
import ru.hse.kpo.pos.domain.model.Criterion;
import ru.hse.kpo.pos.domain.model.RubricatorBuilder;
import ru.hse.kpo.pos.domain.model.Rubricator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class RubricatorService {
    private final Rubricator rubricator;

    public RubricatorService(Rubricator rubricator) {
        this.rubricator = Objects.requireNonNull(rubricator, "Рубрикатор обязателен");
    }

    public Criterion addCriterion(String name, int maxScore, BigDecimal weight) {
        Criterion criterion = new Criterion(UUID.randomUUID(), name, maxScore, weight);
        rubricator.addCriterion(criterion);
        return criterion;
    }

    public List<Criterion> addCriteria(List<CriterionInput> inputs) {
        if (inputs == null || inputs.isEmpty()) throw new IllegalArgumentException("Список критериев не может быть пустым");

        RubricatorBuilder builder = new RubricatorBuilder();
        for (CriterionInput input : inputs) {
            Objects.requireNonNull(input, "Данные критерия обязательны");
            builder.addCriterion(input.name(), input.maxScore(), input.weight());
        }

        List<Criterion> newCriteria = builder.build().getCriteria();
        for (Criterion criterion : newCriteria) rubricator.addCriterion(criterion);
        return newCriteria;
    }

    public List<Criterion> getCriteria() {
        return rubricator.getCriteria();
    }
}
