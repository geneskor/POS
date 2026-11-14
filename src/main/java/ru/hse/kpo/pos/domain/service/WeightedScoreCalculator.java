package ru.hse.kpo.pos.domain.service;

import ru.hse.kpo.pos.domain.model.Criterion;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

final class WeightedScoreCalculator {
    private WeightedScoreCalculator() {}

    static BigDecimal calculate(List<Criterion> criteria, Map<UUID, Integer> scores, boolean gradedOnly) {
        BigDecimal weightedSum = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (Criterion criterion : criteria) {
            if (gradedOnly && !scores.containsKey(criterion.getId())) continue;

            int score = scores.getOrDefault(criterion.getId(), 0);
            BigDecimal normalized = BigDecimal.valueOf(score).divide(BigDecimal.valueOf(criterion.getMaxScore()), MathContext.DECIMAL128);

            weightedSum = weightedSum.add(normalized.multiply(criterion.getWeight()));
            totalWeight = totalWeight.add(criterion.getWeight());
        }

        if (totalWeight.signum() == 0) return BigDecimal.ZERO.setScale(2);
        return weightedSum.multiply(BigDecimal.valueOf(100)).divide(totalWeight, 2, RoundingMode.HALF_UP);
    }
}
