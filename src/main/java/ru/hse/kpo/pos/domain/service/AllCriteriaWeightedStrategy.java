package ru.hse.kpo.pos.domain.service;

import ru.hse.kpo.pos.domain.model.Criterion;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AllCriteriaWeightedStrategy implements FinalScoreStrategy {
    @Override
    public BigDecimal calculate(List<Criterion> criteria, Map<UUID, Integer> scores) {
        return WeightedScoreCalculator.calculate(criteria, scores, false);
    }
}
