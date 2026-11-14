package ru.hse.kpo.pos.application;

import java.math.BigDecimal;

public record CriterionInput(String name, int maxScore, BigDecimal weight) {}
