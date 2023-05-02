package com.antonio32a.core.controller;

import net.minecraft.world.effect.MobEffects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Allows disabling hunger and the hunger bar for players.
 * The stat is automatically re-enabled when the player reconnects.
 * <p>
 * We do this by applying a hunger effect to the player.
 * We set the hunger effect bar texture to a transparent texture, so this hides the bar.
 * <p>
 * This also prevents the player from losing hunger and disables the hunger and saturation effects.
 */
public final class HungerController extends StatController {
    public static final HungerController INSTANCE = new HungerController();

    public HungerController() {
        super(MobEffects.HUNGER, List.of(PotionEffectType.HUNGER, PotionEffectType.SATURATION));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHungerUpdate(FoodLevelChangeEvent event) {
        if (disabledPlayers.contains(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
