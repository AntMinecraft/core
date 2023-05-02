package com.antonio32a.core.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class Spacing {
    public static final char NEGATIVE_MAX = '\uEA00';
    public static final char NEGATIVE_1 = '\uEA01';
    public static final char NEGATIVE_2 = '\uEA02';
    public static final char NEGATIVE_4 = '\uEA04';
    public static final char NEGATIVE_8 = '\uEA08';
    public static final char NEGATIVE_16 = '\uEA10';
    public static final char NEGATIVE_32 = '\uEA20';
    public static final char NEGATIVE_64 = '\uEA40';
    public static final char NEGATIVE_128 = '\uEA80';
    public static final Map<Integer, Character> NEGATIVE_MAP = Map.of(
        1, NEGATIVE_1,
        2, NEGATIVE_2,
        4, NEGATIVE_4,
        8, NEGATIVE_8,
        16, NEGATIVE_16,
        32, NEGATIVE_32,
        64, NEGATIVE_64,
        128, NEGATIVE_128
    );

    public static final char POSITIVE_MAX = '\uEB00';
    public static final char POSITIVE_1 = '\uEB01';
    public static final char POSITIVE_2 = '\uEB02';
    public static final char POSITIVE_4 = '\uEB04';
    public static final char POSITIVE_8 = '\uEB08';
    public static final char POSITIVE_16 = '\uEB10';
    public static final char POSITIVE_32 = '\uEB20';
    public static final char POSITIVE_64 = '\uEB40';
    public static final char POSITIVE_128 = '\uEB80';
    public static final Map<Integer, Character> POSITIVE_MAP = Map.of(
        1, POSITIVE_1,
        2, POSITIVE_2,
        4, POSITIVE_4,
        8, POSITIVE_8,
        16, POSITIVE_16,
        32, POSITIVE_32,
        64, POSITIVE_64,
        128, POSITIVE_128
    );

    private Spacing() {}

    /**
     * Calculate the spacing for the given amount.
     *
     * @param spacing The amount of spacing. Specify a negative number for left spacing and a positive number for right spacing.
     * @return The spacing characters as a string.
     */
    @NotNull
    public static String calculateSpacing(int spacing) {
        if (spacing == 0) {
            return "";
        }

        Map<Integer, Character> characterMap;
        if (spacing > 0) {
            characterMap = POSITIVE_MAP;
        } else {
            characterMap = NEGATIVE_MAP;
        }

        int absoluteSpacing = Math.abs(spacing);
        @Nullable Map.Entry<Integer, Character> entry = characterMap.entrySet().stream()
            .filter(e -> e.getKey() <= absoluteSpacing)
            .max(Map.Entry.comparingByKey())
            .orElse(null);

        if (entry == null) return "";
        int remaining = absoluteSpacing - entry.getKey();
        if (remaining == 0) return entry.getValue().toString();
        return entry.getValue() + calculateSpacing(remaining * (spacing > 0 ? 1 : -1));
    }
}
