package com.antonio32a.core.api.map;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class MapPointsConfig {
    private final Map<String, List<MapPoint>> points;
}
