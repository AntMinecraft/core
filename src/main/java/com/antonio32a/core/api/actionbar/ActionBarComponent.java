package com.antonio32a.core.api.actionbar;

import com.antonio32a.core.api.player.PlayerCache;
import com.antonio32a.core.util.Formatting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents a component in the player's action bar.
 */
@Slf4j
@RequiredArgsConstructor
public final class ActionBarComponent {
    @Getter private final Plugin plugin;
    /**
     * The name of the component.
     * Used in the settings UI.
     */
    @Getter private final String name;
    /**
     * The default X (left to right) position of the component.
     * Starts from 0 which is the center of the action bar.
     * Players can change this value using the ActionBarSettingsUI.
     */
    @Getter private final int defaultPosition;
    /**
     * Should the component be centered around the position.
     */
    @Getter private final boolean centered;
    @Getter private final Map<UUID, PlayerComponent> componentMap = new HashMap<>();

    /**
     * Registers the component.
     * Shorthand for {@link ActionBarController#registerComponent(ActionBarComponent)}.
     *
     * @return The component.
     */
    @NotNull
    public ActionBarComponent register() {
        ActionBarController.INSTANCE.registerComponent(this);
        return this;
    }

    /**
     * Unregisters the component.
     * Shorthand for {@link ActionBarController#unregisterComponent(ActionBarComponent)}.
     *
     * @return The component.
     */
    @NotNull
    public ActionBarComponent unregister() {
        ActionBarController.INSTANCE.unregisterComponent(this);
        return this;
    }

    /**
     * Removes a viewer from the component.
     * This will remove the component from the viewer's action bar.
     *
     * @param player The viewer to remove.
     */
    public void removeViewer(@NotNull Player player) {
        componentMap.remove(player.getUniqueId());
        ActionBarController.INSTANCE.rerenderForPlayer(player);
    }

    /**
     * Clears all viewers of the component.
     */
    public void clearViewers() {
        componentMap.clear();
        for (Player viewer : getViewers()) {
            ActionBarController.INSTANCE.rerenderForPlayer(viewer);
        }
    }

    /**
     * Gets the PlayerComponent instance for a player.
     *
     * @param player The player to get the component for.
     * @return The PlayerComponent instance.
     */
    @NotNull
    public PlayerComponent getFor(@NotNull Player player) {
        @Nullable PlayerComponent playerComponent = componentMap.get(player.getUniqueId());
        if (playerComponent == null) {
            throw new IllegalStateException("Player " + player.getName() + " is not viewing this component");
        }
        return playerComponent;
    }

    /**
     * Updates the position of the component and saves it to the player's profile.
     *
     * @param player      The player to update the position for.
     * @param newPosition The new Position
     */
    public void updatePosition(@NotNull Player player, int newPosition) {
        getFor(player).setPosition(newPosition);
        ActionBarController.INSTANCE.rerenderForPlayer(player);
        PlayerCache.INSTANCE.getOrFetch(player.getUniqueId()).whenComplete((profile, throwable) -> {
            if (throwable != null || profile == null) {
                log.error("Failed to fetch profile for player " + player.getName(), throwable);
                return;
            }

            profile.getActionBarComponentPositions().put(formatPositionKey(), newPosition);
        });
    }

    /**
     * Gets the viewers of the component.
     *
     * @return The viewers.
     */
    @NotNull
    public List<Player> getViewers() {
        return componentMap.keySet()
            .stream()
            .map(Bukkit::getPlayer)
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * Updates the action bar component for a player.
     *
     * @param player    The player to update the actionbar for.
     * @param component The new component.
     */
    public void update(@NotNull Player player, @NotNull Component component) {
        int width = Formatting.calculateWidth(component);
        PlayerComponent playerComponent = componentMap.getOrDefault(
            player.getUniqueId(),
            new PlayerComponent(component, width, defaultPosition, true, false)
        );

        playerComponent.setComponent(component);
        playerComponent.setWidth(width);
        playerComponent.setDirty(true);
        if (!playerComponent.isPositionLoaded()) {
            PlayerCache.INSTANCE.getOrFetch(player.getUniqueId()).whenComplete((profile, throwable) -> {
                if (throwable != null || profile == null) {
                    log.error("Failed to fetch profile for player " + player.getName(), throwable);
                    return;
                }

                Integer position = profile.getActionBarComponentPositions().getOrDefault(
                    formatPositionKey(),
                    defaultPosition
                );
                playerComponent.setPosition(position);
                ActionBarController.INSTANCE.rerenderForPlayer(player);
            });
            playerComponent.setPositionLoaded(true);
        }

        componentMap.put(player.getUniqueId(), playerComponent);
    }

    @NotNull
    private String formatPositionKey() {
        return plugin.getName() + "-" + name;
    }

    @Data
    @AllArgsConstructor
    public static class PlayerComponent {
        private Component component;
        private int width;
        private int position;
        private boolean positionLoaded;
        private boolean dirty;
    }
}
