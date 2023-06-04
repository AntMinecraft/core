package com.antonio32a.core.api.ui.menus;

import org.bukkit.entity.Player;

public abstract class PlayerUI {
    protected final Player player;

    protected PlayerUI(Player player) {
        this.player = player;
    }

    public abstract void open();
}
