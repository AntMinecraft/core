package com.antonio32a.core;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.antonio32a.core.api.actionbar.ActionBarController;
import com.antonio32a.core.api.stat.HealthController;
import com.antonio32a.core.api.stat.HungerController;
import com.antonio32a.core.command.HungerCommands;
import com.antonio32a.core.command.ItemCommands;
import com.antonio32a.core.command.ProfileCommands;
import com.antonio32a.core.listener.ItemListener;
import com.antonio32a.core.listener.PacketListener;
import com.antonio32a.core.listener.PlayerProfileListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Slf4j
public final class AntCore extends JavaPlugin {
    @Getter private static AntCore instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        registerCommands();
        registerListeners();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerProfileListener(), this);
        Bukkit.getPluginManager().registerEvents(new PacketListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemListener(), this);
        Bukkit.getPluginManager().registerEvents(HungerController.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(HealthController.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(ActionBarController.INSTANCE, this);
    }

    private void registerCommands() {
        AnnotationParser<Player> commandParser;
        try {
            commandParser = setupCommandParser();
        } catch (Exception exception) {
            log.error("Failed to initialize command parser, commands will not be registered!", exception);
            return;
        }

        commandParser.parse(new ProfileCommands());
        commandParser.parse(new HungerCommands());
        commandParser.parse(new ItemCommands());
    }

    @NotNull
    private AnnotationParser<Player> setupCommandParser() throws Exception {
        PaperCommandManager<Player> commandManager = new PaperCommandManager<>(
            this,
            CommandExecutionCoordinator.simpleCoordinator(),
            Player.class::cast,
            player -> player
        );

        return new AnnotationParser<>(
            commandManager,
            Player.class,
            params -> SimpleCommandMeta.empty()
        );
    }
}
