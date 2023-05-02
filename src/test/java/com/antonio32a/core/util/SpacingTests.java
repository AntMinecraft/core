package com.antonio32a.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpacingTests {
    @Test
    void testZero() {
        assertEquals("", Spacing.calculateSpacing(0));
    }

    @Test
    void testNegativeBasic() {
        assertEquals(String.valueOf(Spacing.NEGATIVE_1), Spacing.calculateSpacing(-1));
        assertEquals(String.valueOf(Spacing.NEGATIVE_2), Spacing.calculateSpacing(-2));
        assertEquals(String.valueOf(Spacing.NEGATIVE_4), Spacing.calculateSpacing(-4));
    }

    @Test
    void testPositiveBasic() {
        assertEquals(String.valueOf(Spacing.POSITIVE_1), Spacing.calculateSpacing(1));
        assertEquals(String.valueOf(Spacing.POSITIVE_2), Spacing.calculateSpacing(2));
        assertEquals(String.valueOf(Spacing.POSITIVE_4), Spacing.calculateSpacing(4));
    }

    @Test
    void testNegativeComplex() {
        assertEquals(String.valueOf(Spacing.NEGATIVE_2) + Spacing.NEGATIVE_1, Spacing.calculateSpacing(-3));

        assertEquals(
            new String(new char[]{Spacing.NEGATIVE_16, Spacing.NEGATIVE_8, Spacing.NEGATIVE_2, Spacing.NEGATIVE_1}),
            Spacing.calculateSpacing(-27)
        );

        assertEquals(
            new String(new char[]{Spacing.NEGATIVE_128, Spacing.NEGATIVE_32, Spacing.NEGATIVE_8, Spacing.NEGATIVE_1}),
            Spacing.calculateSpacing(-169)
        );
    }

    @Test
    void testPositiveComplex() {
        assertEquals(String.valueOf(Spacing.POSITIVE_2) + Spacing.POSITIVE_1, Spacing.calculateSpacing(3));

        assertEquals(
            new String(new char[]{Spacing.POSITIVE_16, Spacing.POSITIVE_8, Spacing.POSITIVE_2, Spacing.POSITIVE_1}),
            Spacing.calculateSpacing(27)
        );

        assertEquals(
            new String(new char[]{Spacing.POSITIVE_128, Spacing.POSITIVE_32, Spacing.POSITIVE_8, Spacing.POSITIVE_1}),
            Spacing.calculateSpacing(169)
        );
    }
}
