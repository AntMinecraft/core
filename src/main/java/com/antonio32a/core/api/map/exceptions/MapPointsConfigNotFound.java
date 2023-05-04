package com.antonio32a.core.api.map.exceptions;

import java.nio.file.Path;

public class MapPointsConfigNotFound extends RuntimeException {
    public MapPointsConfigNotFound(Path location) {
        super("Map points config at location " + location + " not found.");
    }
}
