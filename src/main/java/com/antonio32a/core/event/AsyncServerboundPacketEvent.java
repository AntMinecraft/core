package com.antonio32a.core.event;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired asynchronously on the netty thread when a packet is received from the client.
 * You can modify the packet or cancel the event to prevent the packet from being received.
 * This event will also be fired as soon @{@link org.bukkit.event.player.PlayerLoginEvent} is called.
 */
public final class AsyncServerboundPacketEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter private final Player player;
    @Getter @Setter private Packet<ServerGamePacketListener> packet;
    @Getter @Setter private boolean cancelled;

    public AsyncServerboundPacketEvent(Player player, Packet<ServerGamePacketListener> packet) {
        super(true);
        this.player = player;
        this.packet = packet;
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
