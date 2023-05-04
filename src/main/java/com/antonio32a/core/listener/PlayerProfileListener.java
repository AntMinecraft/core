package com.antonio32a.core.listener;

import com.antonio32a.core.AntCore;
import com.antonio32a.core.api.player.PlayerCache;
import com.antonio32a.privateapi.data.PlayerProfile;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

@Slf4j
public final class PlayerProfileListener implements Listener {
    private static final PlayerCache playerCache = PlayerCache.INSTANCE;

    public PlayerProfileListener() {
        Bukkit.getScheduler().runTaskTimer(AntCore.getInstance(), this::invalidateOldProfiles, 0L, 20L);
    }

    @EventHandler
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent event) {
        log.info("Fetching player profile for {}...", event.getName());
        try {
            @Nullable PlayerProfile profile = playerCache.getOrFetch(event.getUniqueId()).get();
            if (profile == null) {
                log.info("Profile is null, creating a default one...");
                profile = PlayerProfile.createDefault(event.getUniqueId(), event.getName());
                playerCache.update(profile).get();
                log.info("Created default profile for {}!", event.getName());
            }

            log.info("Fetched player profile for {}!", event.getName());
        } catch (Exception exception) {
            log.error("Failed to fetch player profile for {}!", event.getName(), exception);
            event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                Component.text("Failed to fetch your profile. Please try again later.")
            );

            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerCache.getOrFetch(event.getPlayer().getUniqueId()).thenAccept(profile -> {
            if (profile == null) {
                log.error("Profile is null for {}. Not updating data.", event.getPlayer().getName());
                return;
            }

            playerCache.update(profile).thenRun(() ->
                log.info("Updated player profile for {} because they left.", event.getPlayer().getName())
            );
        });
    }

    private void invalidateOldProfiles() {
        playerCache.invalidateOldProfiles();
    }
}
