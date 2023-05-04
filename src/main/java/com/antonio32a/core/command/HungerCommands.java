package com.antonio32a.core.command;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.antonio32a.core.api.stat.HungerController;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class HungerCommands {
    private static final HungerController hungerController = HungerController.INSTANCE;

    @CommandMethod("hunger toggle")
    @CommandPermission("core.command.hunger.toggle")
    public void toggleHunger(@NotNull Player player) {
        if (!hungerController.isDisabled(player)) {
            hungerController.disable(player);
            player.sendMessage(Component.text("Hunger disabled."));
            return;
        }

        hungerController.enable(player);
        player.sendMessage(Component.text("Hunger enabled."));
    }
}
