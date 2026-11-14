package ru.hse.kpo.pos.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RubricatorBuilder {
    private final List<Criterion> criteria = new ArrayList<>();

    public RubricatorBuilder addCriterion(String name, int maxScore, BigDecimal weight) {
        criteria.add(new Criterion(UUID.randomUUID(), name, maxScore, weight));
        return this;
    }

    public Rubricator build() {
        if (criteria.isEmpty()) throw new IllegalStateException("Нельзя собрать пустой рубрикатор");

        Rubricator rubricator = new Rubricator();
        for (Criterion criterion : criteria) rubricator.addCriterion(criterion);
        return rubricator;
    }
}
