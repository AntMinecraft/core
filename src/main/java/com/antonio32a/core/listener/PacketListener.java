package com.antonio32a.core.listener;

import com.antonio32a.core.event.AsyncClientboundPacketEvent;
import com.antonio32a.core.event.AsyncServerboundPacketEvent;
import com.antonio32a.core.util.Packets;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

@Slf4j
public final class PacketListener implements Listener {

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
        Packets.ensureChannel(player).thenAccept(channel ->
            channel.pipeline().addBefore(
                "packet_handler",
                "ant_packet_handler",
                handler
            )
        );
    }
}
