package com.antonio32a.core.api.item.tags;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface ItemTag {
    @NotNull
    Component render();
}
