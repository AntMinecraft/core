package com.antonio32a.core.util;

import io.netty.channel.Channel;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.TextFilter;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.inventivetalent.reflection.accessor.FieldAccessor;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Packets {
    private static final NMSClassResolver RESOLVER = new NMSClassResolver();
    private static final Class<?> SERVER_PLAYER = RESOLVER.resolveSilent("ServerPlayer", "server.level.ServerPlayer");
    private static final Class<?> TEXT_FILTER = RESOLVER.resolveSilent("TextFilter", "server.network.TextFilter");
    private static final FieldResolver PLAYER_FIELD_RESOLVER = new FieldResolver(SERVER_PLAYER);
    private static final FieldAccessor TEXT_FILTER_ACCESSOR = PLAYER_FIELD_RESOLVER.resolveByFirstTypeAccessor(TEXT_FILTER);

    private Packets() {}

    /**
     * Sends a packet to a player.
     *
     * @param player The player to send the packet to.
     * @param packet The packet to send.
     */
    public static void sendPacket(@NotNull Player player, @NotNull Packet<ClientGamePacketListener> packet) {
        ensureConnection(player).thenAccept(connection -> connection.send(packet));
    }

    /**
     * Gets the player's connection channel once it's set.
     * We want to inject very soon, so we can modify packets like the login packet.
     * The player's connection is set when they join the server, but only after PlayerLoginEvent is called.
     * So just calling handle.connection.connection won't work because it'll return null.
     * The textFilter field is set just after the channel is set, so we can replace the textFilter field with our own
     * which will call the original textFilter's join method and then complete the future with our channel.
     *
     * @param player The player to get the connection for.
     * @return A future which will complete with the player's connection.
     */
    @NotNull
    public static CompletableFuture<Connection> ensureConnection(@NotNull Player player) {
        ServerPlayer handle = ((CraftPlayer) player).getHandle();
        @Nullable ServerGamePacketListenerImpl connection = handle.connection;
        // noinspection ConstantConditions - Intellij is just wrong here, probably because connection is initialized late
        if (connection != null) {
            return CompletableFuture.completedFuture(connection.connection);
        }

        CompletableFuture<Connection> future = new CompletableFuture<>();
        net.minecraft.server.network.TextFilter textFilter = TEXT_FILTER_ACCESSOR.get(handle);

        TEXT_FILTER_ACCESSOR.set(handle, new TextFilter() {
            @Override
            public void join() {
                textFilter.join();
                future.complete(handle.connection.connection);
            }

            @Override
            public void leave() {
                textFilter.leave();
            }

            @Override
            @NotNull
            public CompletableFuture<FilteredText> processStreamMessage(@NotNull String text) {
                return textFilter.processStreamMessage(text);
            }

            @Override
            @NotNull
            public CompletableFuture<List<FilteredText>> processMessageBundle(@NotNull List<String> texts) {
                return textFilter.processMessageBundle(texts);
            }
        });

        return future;
    }

    /**
     * Gets the player's connection channel once it's set.
     *
     * @param player The player to get the channel for.
     * @return A future which will complete with the player's channel.
     * @see #ensureConnection(Player) for explanation why this is needed.
     */
    @NotNull
    public static CompletableFuture<Channel> ensureChannel(@NotNull Player player) {
        return ensureConnection(player).thenApply(connection -> connection.channel);
    }
}
