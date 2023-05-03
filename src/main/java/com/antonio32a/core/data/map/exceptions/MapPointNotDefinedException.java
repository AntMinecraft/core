package com.antonio32a.core.data.map.exceptions;

public class MapPointNotDefinedException extends RuntimeException {
    public MapPointNotDefinedException(String name) {
        super("Map point " + name + " is not defined.");
    }
}
