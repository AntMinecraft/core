package com.antonio32a.core.util;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Packets {
    private Packets() {}

    /**
     * Sends a packet to a player.
     * @param player The player to send the packet to.
     * @param packet The packet to send.
     */
    public static void sendPacket(@NotNull Player player, @NotNull Packet<ClientGamePacketListener> packet) {
        ServerPlayer handle = ((CraftPlayer) player).getHandle();
        @Nullable ServerGamePacketListenerImpl connection = handle.connection;
        // noinspection ConstantConditions - Intellij is just wrong here, probably because connection is initialized late
        if (connection == null) return;
        connection.send(packet);
    }
}
