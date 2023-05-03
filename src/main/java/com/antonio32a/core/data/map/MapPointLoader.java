package com.antonio32a.core.data.map;

import com.antonio32a.core.data.config.Config;
import com.antonio32a.core.data.config.ConfigLoader;
import com.antonio32a.core.data.map.exceptions.MapPointNotDefinedException;
import com.antonio32a.core.data.map.exceptions.MapPointsConfigNotFound;
import com.antonio32a.core.data.map.exceptions.MapPointsConfigParseException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class MapPointLoader {
    public static final MapPointLoader INSTANCE = new MapPointLoader();
    private final Config config = ConfigLoader.INSTANCE.getConfig();
    @Nullable private final Map<String, List<MapPoint>> pointMap = loadMapPoints();

    /**
     * Get a map point by name.
     *
     * @param name The name of the map point.
     * @return The map point, or null if it does not exist.
     */
    @Nullable
    public MapPoint get(@NotNull String name) {
        if (pointMap == null) return null;
        Optional<MapPoint> point = pointMap.get(name).stream().findFirst();
        return point.orElse(null);
    }

    /**
     * Get a map point by name, or throw an exception if it does not exist.
     *
     * @param name The name of the map point.
     * @return The map point.
     * @throws MapPointNotDefinedException If the map point does not exist or if map points weren't loaded.
     * @throws IllegalStateException If map points weren't loaded.
     */
    @NotNull
    public MapPoint getOrThrow(@NotNull String name) {
        if (pointMap == null) throw new IllegalStateException("Map points not loaded.");
        @Nullable MapPoint point = get(name);
        if (point == null) throw new MapPointNotDefinedException(name);
        return point;
    }

    /**
     * Get a list of map points by name.
     *
     * @param name The name of the map point.
     * @return The list of map points, or null if it does not exist or if the list is empty.
     */
    @Nullable
    public List<MapPoint> getMultiple(@NotNull String name) {
        if (pointMap == null) return null;
        List<MapPoint> points = pointMap.get(name);
        if (points.isEmpty()) return null;
        return points;
    }

    /**
     * Get a list of map points by name, or throw an exception if it does not exist.
     *
     * @param name The name of the map point.
     * @return The list of map points.
     * @throws MapPointNotDefinedException If the map point does not exist or if the list is empty.
     * @throws IllegalStateException If map points weren't loaded.
     */
    @NotNull
    public List<MapPoint> getMultipleOrThrow(@NotNull String name) {
        if (pointMap == null) throw new IllegalStateException("Map points not loaded.");
        @Nullable List<MapPoint> points = getMultiple(name);
        if (points == null || points.isEmpty()) throw new MapPointNotDefinedException(name);
        return points;
    }

    @Nullable
    private Map<String, List<MapPoint>> loadMapPoints() {
        @Nullable String rawPath = config.getMapPointsPath();
        if (rawPath == null) {
            log.warn("Map points path not set, skipping loading points.");
            return null;
        }

        Path path = Path.of(rawPath);
        if (!path.toFile().exists()) {
            throw new MapPointsConfigNotFound(path.toAbsolutePath());
        }

        try {
            String text = Files.readString(path);
            MapPointsConfig mapPointsConfig = new Gson().fromJson(text, MapPointsConfig.class);
            return mapPointsConfig.getPoints();
        } catch (IOException exception) {
            throw new MapPointsConfigParseException(exception);
        }
    }
}
