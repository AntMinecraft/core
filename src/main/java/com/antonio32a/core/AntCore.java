package com.antonio32a.core;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.antonio32a.core.command.HungerCommands;
import com.antonio32a.core.command.ProfileCommands;
import com.antonio32a.core.api.stat.HealthController;
import com.antonio32a.core.api.stat.HungerController;
import com.antonio32a.core.listener.PacketListener;
import com.antonio32a.core.listener.PlayerProfileListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Slf4j
public final class AntCore extends JavaPlugin {
    @Getter private static AntCore instance;
    @Getter private AnnotationParser<Player> commandParser;
    private PaperCommandManager<Player> commandManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        try {
            commandManager = new PaperCommandManager<>(this,
                CommandExecutionCoordinator.simpleCoordinator(),
                Player.class::cast,
                player -> player);
        } catch (Exception exception) {
            log.error("Failed to initialize command manager", exception);
        }

        commandParser = new AnnotationParser<>(
            commandManager,
            Player.class,
            params -> SimpleCommandMeta.empty()
        );

        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        commandParser.parse(new ProfileCommands());
        commandParser.parse(new HungerCommands());
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerProfileListener(), this);
        Bukkit.getPluginManager().registerEvents(new PacketListener(), this);
        Bukkit.getPluginManager().registerEvents(HungerController.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(HealthController.INSTANCE, this);
    }
}
