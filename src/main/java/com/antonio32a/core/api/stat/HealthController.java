package com.antonio32a.core.api.stat;

import com.antonio32a.core.event.AsyncClientboundPacketEvent;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Allows disabling health and the health bar for players.
 * This must be enabled in PlayerLoginEvent!
 * The stat is automatically re-enabled when the player reconnects.
 * <p>
 * Re-enabling the stat will not restore the player's health bar, it will only restore their health functionality.
 * (It's unfortunately impossible to restore the health bar without relogging)
 * <p>
 * This is done by telling the player that they're in hardcore, which changes
 * their health bar texture to one which we made transparent in the resource pack.
 * <p>
 * This also prevents players from loosing health (keeps the damage animation)
 * and disables the wither, poison, absorption and health boost effects.
 */
public final class HealthController extends StatController {
    public static final HealthController INSTANCE = new HealthController();

    public HealthController() {
        super(
            null, List.of(
                PotionEffectType.WITHER,
                PotionEffectType.POISON,
                PotionEffectType.ABSORPTION,
                PotionEffectType.HEALTH_BOOST
            )
        );
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onHealthRegen(EntityRegainHealthEvent event) {
        if (disabledPlayers.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onDamage(EntityDamageEvent event) {
        if (disabledPlayers.contains(event.getEntity().getUniqueId())) {
            event.setDamage(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onClientboundPacket(AsyncClientboundPacketEvent event) {
        if (!disabledPlayers.contains(event.getPlayer().getUniqueId())) return;
        if (!(event.getPacket() instanceof ClientboundLoginPacket packet)) return;

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(buf);

        // The hardcore boolean is the second variable in the packet.
        // The first variable in the packet is an integer, so we skip it
        // and then set the second byte to true.
        buf.setBoolean(Integer.SIZE / Byte.SIZE, true);
        event.setPacket(new ClientboundLoginPacket(buf));
    }
}
