package com.antonio32a.core.api.item;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ItemRegistry {
    public static final ItemRegistry INSTANCE = new ItemRegistry();
    private final List<GameItem> items = new ArrayList<>();

    /**
     * Registers a new item.
     *
     * @param item   The item to register.
     * @param plugin The plugin to register the item to.
     */
    public void register(@NotNull GameItem item, @NotNull Plugin plugin) {
        items.add(item);
        Bukkit.getPluginManager().registerEvents(item, plugin);
    }

    /**
     * Gets an item by its ID.
     *
     * @param id The ID of the item.
     * @return The item, or null if not found.
     */
    @Nullable
    public GameItem getById(@NotNull String id) {
        return items.stream()
            .filter(item -> item.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets an item instance by its class.
     *
     * @param clazz The class of the item.
     * @param <T>   The type of the item.
     * @return The item, or null if not found.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends GameItem> T getByClass(@NotNull Class<T> clazz) {
        return (T) items.stream()
            .filter(item -> item.getClass().equals(clazz))
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets an item by its stack.
     *
     * @param item The item stack.
     * @return The item, or null if not found or if it's not a GameItem.
     */
    @Nullable
    public GameItem getByStack(@NotNull ItemStack item) {
        @Nullable ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        @Nullable String id = meta.getPersistentDataContainer().get(GameItem.ID_KEY, PersistentDataType.STRING);
        if (id == null) return null;
        return getById(id);
    }

    /**
     * Gets all the registered items.
     *
     * @return The list of items.
     */
    @NotNull
    public List<GameItem> getAll() {
        return items;
    }
}
