package com.antonio32a.core.listener;

import com.antonio32a.core.event.AsyncClientboundPacketEvent;
import com.antonio32a.core.event.AsyncServerboundPacketEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.server.network.TextFilter;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.inventivetalent.reflection.accessor.FieldAccessor;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public final class PacketListener implements Listener {
    private static final NMSClassResolver RESOLVER = new NMSClassResolver();
    private static final Class<?> SERVER_PLAYER = RESOLVER.resolveSilent("ServerPlayer", "server.level.ServerPlayer");
    private static final Class<?> TEXT_FILTER = RESOLVER.resolveSilent("TextFilter", "server.network.TextFilter");
    private static final FieldResolver PLAYER_FIELD_RESOLVER = new FieldResolver(SERVER_PLAYER);
    private static final FieldAccessor TEXT_FILTER_ACCESSOR = PLAYER_FIELD_RESOLVER.resolveByFirstTypeAccessor(TEXT_FILTER);

    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.HIGH)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        injectPacketHandler(player, new ChannelDuplexHandler() {
            @Override
            public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
                if (!(msg instanceof Packet<?> rawPacket)) return;
                Packet<ServerGamePacketListener> packet = (Packet<ServerGamePacketListener>) rawPacket;

                try {
                    AsyncServerboundPacketEvent event = new AsyncServerboundPacketEvent(player, packet);
                    event.callEvent();
                    if (event.isCancelled()) {
                        return;
                    }

                    super.channelRead(ctx, event.getPacket());
                } catch (Exception exception) {
                    log.error("Error while handling serverbound packet event", exception);
                    super.channelRead(ctx, packet);
                }
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (!(msg instanceof Packet<?> rawPacket)) return;
                Packet<ClientGamePacketListener> packet = (Packet<ClientGamePacketListener>) rawPacket;

                try {
                    AsyncClientboundPacketEvent event = new AsyncClientboundPacketEvent(player, packet);
                    event.callEvent();
                    if (event.isCancelled()) {
                        return;
                    }

                    super.write(ctx, event.getPacket(), promise);
                } catch (Exception exception) {
                    log.error("Error while handling clientbound packet event", exception);
                    super.write(ctx, packet, promise);
                }
            }
        });
    }

    private void injectPacketHandler(@NotNull Player player, @NotNull ChannelDuplexHandler handler) {
        getChannelOnceReady(player).thenAccept(channel ->
            channel.pipeline().addBefore(
                "packet_handler",
                "ant_packet_handler",
                handler
            )
        );
    }

    /**
     * Gets the player's connection channel once it's set.
     * We want to inject very soon, so we can modify packets like the login packet.
     * The player's channel is set when they join the server, but only after PlayerLoginEvent is called.
     * So just calling handle.connection.connection.channel won't work because it'll return null.
     * The textFilter field is set just after the channel is set, so we can replace the textFilter field with our own
     * which will call the original textFilter's join method and then complete the future with our channel.
     *
     * @param player The player to get the channel for.
     * @return A future which will complete with the player's channel.
     */
    @NotNull
    private CompletableFuture<Channel> getChannelOnceReady(@NotNull Player player) {
        ServerPlayer handle = ((CraftPlayer) player).getHandle();
        CompletableFuture<Channel> future = new CompletableFuture<>();
        net.minecraft.server.network.TextFilter textFilter = TEXT_FILTER_ACCESSOR.get(handle);

        TEXT_FILTER_ACCESSOR.set(handle, new TextFilter() {
            @Override
            public void join() {
                textFilter.join();
                future.complete(handle.connection.connection.channel);
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
}
