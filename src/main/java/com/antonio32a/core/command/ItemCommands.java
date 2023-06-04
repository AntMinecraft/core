package com.antonio32a.core.command;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.antonio32a.core.api.ui.menus.ItemsUI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ItemCommands {
    @CommandMethod("items")
    @CommandPermission("ant.command.items")
    public void items(@NotNull Player player) {
        new ItemsUI(player).open();
    }
}
