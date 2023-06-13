package com.antonio32a.core.event;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired asynchronously on the netty thread when a packet is sent from the server to the player.
 * You can modify the packet or cancel the event to prevent the packet from being sent.
 * This event will also be fired as soon @{@link org.bukkit.event.player.PlayerLoginEvent} is called,
 * so you can even manipulate packets such as {@link net.minecraft.network.protocol.game.ClientboundLoginPacket}.
 */
@Getter
public final class AsyncClientboundPacketEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    @Setter private Packet<ClientGamePacketListener> packet;
    @Setter private boolean cancelled;

    public AsyncClientboundPacketEvent(Player player, Packet<ClientGamePacketListener> packet) {
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
