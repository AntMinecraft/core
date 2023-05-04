package com.antonio32a.core.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

public final class Formatting {
    private static final MiniMessage miniMessage = MiniMessage.builder()
        .strict(true)
        .tags(TagResolver.standard())
        .build();

    private Formatting() {}

    /**
     * Parses a string into a component
     *
     * @param text The string to parse
     * @return The parsed component
     */
    @NotNull
    public static Component parse(@NotNull String text) {
        return miniMessage.deserialize(text);
    }

    /**
     * Parses a string into a component with resolvers
     *
     * @param text      The string to parse
     * @param resolvers The resolvers to use
     * @return The parsed component
     */
    @NotNull
    public static Component parse(@NotNull String text, @NotNull TagResolver... resolvers) {
        return miniMessage.deserialize(text, resolvers);
    }
}
