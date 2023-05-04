package com.antonio32a.core.api.map.exceptions;

public class MapPointsConfigParseException extends RuntimeException {
    public MapPointsConfigParseException(Throwable cause) {
        super("Failed to parse map points config.", cause);
    }
}
