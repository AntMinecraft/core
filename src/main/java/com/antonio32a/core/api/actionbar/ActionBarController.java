package com.antonio32a.core.api.actionbar;

import com.antonio32a.core.AntCore;
import com.antonio32a.core.util.Spacing;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Slf4j
public final class ActionBarController implements Listener {
    public static final ActionBarController INSTANCE = new ActionBarController();
    private final List<ActionBarComponent> components = new ArrayList<>();
    private final Map<UUID, Component> actionBarCache = new HashMap<>();
    private final Map<UUID, Long> lastRendered = new HashMap<>();

    public ActionBarController() {
        Bukkit.getScheduler().runTaskTimer(AntCore.getInstance(), this::onTick, 0L, 1L);
    }

    /**
     * Registers a new component in the action bar.
     *
     * @param component The component to register.
     */
    public void registerComponent(@NotNull ActionBarComponent component) {
        components.add(component);
    }

    /**
     * Unregisters a component from the action bar.
     * This will remove the component from all viewers.
     * You should call this method when the component is no longer needed.
     *
     * @param component The component to unregister.
     */
    public void unregisterComponent(@NotNull ActionBarComponent component) {
        components.remove(component);
        component.clearViewers();
    }

    /**
     * Forcefully re-renders the action bar for a player.
     * This will rerender the action bar even if it is not dirty.
     * This is usually used for when the player's profile is updated.
     *
     * @param player The player to rerender the action bar for.
     */
    public void rerenderForPlayer(@NotNull Player player) {
        actionBarCache.put(player.getUniqueId(), renderActionBar(player));
        player.sendActionBar(actionBarCache.get(player.getUniqueId()));
    }

    /**
     * Gets the components which the player is currently a viewer of.
     *
     * @param player The player to get the components for.
     * @return The components.
     */
    @NotNull
    public List<ActionBarComponent> getComponentsForPlayer(@NotNull Player player) {
        return components.stream()
            .filter(component -> component.getViewers().contains(player))
            .toList();
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        actionBarCache.remove(event.getPlayer().getUniqueId());
        lastRendered.remove(event.getPlayer().getUniqueId());
        for (ActionBarComponent component : components) {
            component.removeViewer(event.getPlayer());
        }
    }

    private void onTick() {
        List<Player> allViewers = components.stream()
            .map(ActionBarComponent::getViewers)
            .flatMap(Collection::stream)
            .distinct()
            .toList();

        for (Player player : allViewers) {
            boolean isDirty = components.stream()
                .filter(component -> component.getViewers().contains(player))
                .anyMatch(component -> component.getFor(player).isDirty());

            if (isDirty) {
                rerenderForPlayer(player);
                lastRendered.put(player.getUniqueId(), System.currentTimeMillis());
            } else {
                long lastRender = lastRendered.getOrDefault(player.getUniqueId(), 0L);
                if (System.currentTimeMillis() - lastRender >= 1000) {
                    player.sendActionBar(actionBarCache.get(player.getUniqueId()));
                    lastRendered.put(player.getUniqueId(), System.currentTimeMillis());
                }
            }
        }
    }

    @NotNull
    private Component renderActionBar(@NotNull Player player) {
        List<ActionBarComponent> componentsToRender = getComponentsForPlayer(player);
        if (componentsToRender.isEmpty()) {
            return Component.empty();
        }

        Component result = Component.empty();
        int cursorPosition = 0;

        for (ActionBarComponent component : componentsToRender) {
            ActionBarComponent.PlayerComponent playerComponent = component.getFor(player);
            int spacing = playerComponent.getPosition() - cursorPosition;
            result = result.append(Component.text(Spacing.calculateSpacing(spacing)));
            cursorPosition += spacing;

            if (component.isCentered()) {
                result = result.append(Component.text(Spacing.calculateSpacing(-playerComponent.getWidth() / 2)));
                cursorPosition -= playerComponent.getWidth() / 2;
            }

            result = result.append(playerComponent.getComponent());
            cursorPosition += playerComponent.getWidth();
            playerComponent.setDirty(false);
        }

        if (cursorPosition != 0) {
            result = result.append(Component.text(Spacing.calculateSpacing(-cursorPosition)));
        }
        return result;
    }
}
