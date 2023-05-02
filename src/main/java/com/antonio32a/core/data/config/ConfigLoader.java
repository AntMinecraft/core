package com.antonio32a.core.data.config;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public final class ConfigLoader {
    public static final ConfigLoader INSTANCE = new ConfigLoader();

    @Getter private final Config config;

    ConfigLoader() {
        this.config = this.loadConfig();
    }

    @NotNull
    private Config loadConfig() {
        @Nullable String file = System.getenv("CONFIG_FILE");
        if (file == null) {
            throw new IllegalStateException("CONFIG_FILE environment variable not set");
        }

        Path path = Paths.get(file);
        if (!path.toFile().exists()) {
            throw new IllegalStateException("Config file " + path.toAbsolutePath() + " does not exist");
        }

        try {
            String text = Files.readString(path);
            return new Gson().fromJson(text, Config.class);
        } catch (IOException exception) {
            log.error("Error reading config file", exception);
            throw new IllegalStateException("Error reading config file");
        }
    }
}
