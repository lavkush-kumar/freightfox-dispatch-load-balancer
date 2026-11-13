package com.example.dispatch.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorTest {
    @Test void samePointHasZeroDistance() { assertEquals(0.0, DistanceCalculator.haversineKm(12.0, 77.0, 12.0, 77.0), 1e-9); }
    @Test void calculatesKnownApproximateDistance() { assertEquals(111.2, DistanceCalculator.haversineKm(0, 0, 0, 1), 0.2); }
    @Test void rejectsInvalidCoordinates() { assertThrows(IllegalArgumentException.class, () -> DistanceCalculator.haversineKm(91, 0, 0, 0)); }
}
