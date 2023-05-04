package com.antonio32a.core.api.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public final class Config {
    private final String privateApiUrl;
    @Nullable private final String mapPointsPath;
}
