package com.antonio32a.core.api.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class Team {
    protected final String id;
    protected final Component name;
    @Nullable protected final Component prefix;
    @Nullable protected final Component suffix;
    protected final TextColor color;
    protected final Set<UUID> players = new HashSet<>();
}
