package ru.hse.kpo.pos.domain.service;

import ru.hse.kpo.pos.domain.model.Criterion;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FinalScoreStrategy {
    BigDecimal calculate(List<Criterion> criteria, Map<UUID, Integer> scores);
}
