package com.antonio32a.core.api.item.event;

import com.antonio32a.core.api.item.GameItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GameItemInteractEvent extends PlayerInteractEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    @Getter private final GameItem gameItem;
    @Getter @Setter private boolean cancelled;

    public GameItemInteractEvent(
        @NotNull Player who,
        @NotNull Action action,
        @NotNull ItemStack item,
        @NotNull GameItem gameItem,
        @Nullable Block clickedBlock,
        @NotNull BlockFace clickedFace,
        @Nullable EquipmentSlot hand,
        @Nullable Location interactionPoint
    ) {
        super(who, action, item, clickedBlock, clickedFace, hand, interactionPoint);
        this.gameItem = gameItem;
    }


    @SuppressWarnings("unused") // Needed for custom events
    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return getHandlerList();
    }
}
