package com.antonio32a.core.api.item.tags;

import com.antonio32a.core.util.Formatting;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class TestTag implements ItemTag {
    private final int test;

    @NotNull
    @Override
    public Component render() {
        return Formatting.parse(
            "<green>Test:</green> <red><test></red>",
            Placeholder.unparsed("test", String.valueOf(test))
        );
    }
}
