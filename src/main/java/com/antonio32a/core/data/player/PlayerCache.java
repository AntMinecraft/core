package com.antonio32a.core.data.player;

import com.antonio32a.privateapi.data.PlayerProfile;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class PlayerCache {
    public static final PlayerCache INSTANCE = new PlayerCache();

    public static final long CACHE_TIME = TimeUnit.MINUTES.toMillis(5);
    private static final PlayerClient client = PlayerClient.INSTANCE;
    private final ConcurrentHashMap<UUID, CompletableFuture<PlayerProfile>> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Long> lastAccessed = new ConcurrentHashMap<>();

    /**
     * Attempts to get a player by UUID from cache or fetches it.
     *
     * @param uuid The UUID of the player.
     * @return A CompletableFuture that will be completed with the player profile or null if the profile does not exist.
     */
    @NotNull
    public CompletableFuture<@Nullable PlayerProfile> getOrFetch(@NotNull UUID uuid) {
        lastAccessed.put(uuid, System.currentTimeMillis());
        return cache.computeIfAbsent(uuid, ignored -> client.getPlayer(uuid));
    }

    /**
     * Attempts to get a player by name from cache or fetches it.
     *
     * @param name The name of the player.
     * @return A CompletableFuture that will be completed with the player profile or null if the profile does not exist.
     */
    @NotNull
    public CompletableFuture<@Nullable PlayerProfile> getOrFetchByName(@NotNull String name) {
        @Nullable Player bukkitPlayer = Bukkit.getPlayer(name);
        if (bukkitPlayer != null) {
            return getOrFetch(bukkitPlayer.getUniqueId());
        }

        // Maybe we should cache this too?
        return client.getPlayerByName(name);
    }


    /**
     * Updates a player profile. This will both update the cache and send a request.
     * This is automatically called when a player leaves.
     * You do not need to call this manually unless you know what you're doing.
     *
     * @param profile The profile to update.
     * @return A CompletableFuture that will be completed when the request is sent.
     */
    @NotNull
    public CompletableFuture<Void> update(@NotNull PlayerProfile profile) {
        profile.setLastUpdated(System.currentTimeMillis());
        cache.put(UUID.fromString(profile.getId()), CompletableFuture.completedFuture(profile));
        return PlayerClient.INSTANCE.updatePlayer(profile);
    }

    /**
     * Invalidates old profiles from the cache.
     */
    public void invalidateOldProfiles() {
        Iterator<Map.Entry<UUID, CompletableFuture<PlayerProfile>>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, CompletableFuture<PlayerProfile>> entry = iterator.next();
            long lastUpdated = lastAccessed.getOrDefault(entry.getKey(), 0L);
            boolean isOnline = Bukkit.getPlayer(entry.getKey()) != null;

            // Don't invalidate if the player is online
            if (System.currentTimeMillis() - lastUpdated > CACHE_TIME && !isOnline) {
                iterator.remove();
                lastAccessed.remove(entry.getKey());
                log.info("Invalidated profile for {} because it's old.", entry.getKey());
            }
        }
    }
}
