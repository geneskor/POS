package ru.hse.kpo.pos.domain.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class Rubricator {
    private final Map<UUID, Criterion> criteria = new LinkedHashMap<>();

    public void addCriterion(Criterion criterion) {
        Objects.requireNonNull(criterion, "Критерий обязателен");

        if (criteria.containsKey(criterion.getId())) {
            throw new IllegalArgumentException("Критерий с таким ID уже существует");
        }

        criteria.put(criterion.getId(), criterion);
    }

    public Criterion getCriterion(UUID criterionId) {
        Objects.requireNonNull(criterionId, "ID критерия обязателен");

        Criterion criterion = criteria.get(criterionId);
        if (criterion == null) {
            throw new IllegalArgumentException("Критерий отсутствует в рубрикаторе");
        }

        return criterion;
    }

    public List<Criterion> getCriteria() {
        return List.copyOf(criteria.values());
    }
}
