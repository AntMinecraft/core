package com.antonio32a.core.controller;

import com.antonio32a.core.util.Packets;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Allows disabling stats and the corresponding bars for players.
 * Unlike HealthController, this must NOT be enabled in {@link PlayerLoginEvent} (otherwise it won't apply the effect).
 * <p>
 * If you wish to implement your own you can extend this class and then register the corresponding events,
 * e.g. {@link EntityDamageEvent} for health.
 * Hiding the actual bar is usually done by applying a potion effect (e.g. hunger) to the player.
 * If you don't wish to apply a potion effect you can just pass in null.
 */
public abstract class StatController implements Listener {
    protected final Set<UUID> disabledPlayers;
    @Nullable protected final MobEffectInstance effectInstance;
    @Nullable private final MobEffect effect;
    private final List<PotionEffectType> potionEffectsToDisable;

    protected StatController(@Nullable MobEffect effect, List<PotionEffectType> potionEffectsToDisable) {
        this.effect = effect;
        this.potionEffectsToDisable = potionEffectsToDisable;
        this.disabledPlayers = new HashSet<>();

        if (this.effect != null) {
            this.effectInstance = new MobEffectInstance(
                effect,
                -1, // infinite
                0,
                false,
                false,
                false
            );
        } else {
            this.effectInstance = null;
        }
    }

    /**
     * Disable the stat and the corresponding bar for a player.
     * This disables the entire stat mechanic, not just the bar.
     * This will also remove stat related effects (e.g. absorption in case of health) from the player.
     *
     * @param player The player to disable the stat for.
     */
    public void disable(@NotNull Player player) {
        if (disabledPlayers.contains(player.getUniqueId())) return;

        potionEffectsToDisable.forEach(player::removePotionEffect);
        addPotionEffect(player);
        disabledPlayers.add(player.getUniqueId());
    }

    /**
     * Enable the stat and the corresponding bar for a player.
     * This is the default state.
     *
     * @param player The player to enable the stat for.
     */
    public void enable(@NotNull Player player) {
        if (!disabledPlayers.contains(player.getUniqueId())) return;
        removePotionEffect(player);
        disabledPlayers.remove(player.getUniqueId());
    }

    /**
     * Check if the stat and the corresponding bar is disabled for a player.
     *
     * @param player The player to check.
     * @return Whether the stat is disabled for the player.
     */
    public boolean isDisabled(@NotNull Player player) {
        return disabledPlayers.contains(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        if (disabledPlayers.contains(event.getPlayer().getUniqueId())) {
            addPotionEffect(event.getPlayer());
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        disabledPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!disabledPlayers.contains(player.getUniqueId())) return;

        @Nullable PotionEffect oldEffect = event.getOldEffect();
        @Nullable PotionEffect newEffect = event.getNewEffect();
        boolean isDisabledEffect = potionEffectsToDisable.stream().anyMatch(type ->
            (oldEffect != null && oldEffect.getType().equals(type))
                || (newEffect != null && newEffect.getType().equals(type))
        );

        if (!isDisabledEffect) return;
        event.setCancelled(true);
    }

    private void addPotionEffect(@NotNull Player player) {
        if (effectInstance == null) return;
        Packets.sendPacket(
            player,
            new ClientboundUpdateMobEffectPacket(
                player.getEntityId(),
                effectInstance
            )
        );
    }

    private void removePotionEffect(@NotNull Player player) {
        if (effect == null) return;
        Packets.sendPacket(
            player,
            new ClientboundRemoveMobEffectPacket(
                player.getEntityId(),
                effect
            )
        );
    }
}
