package com.antonio32a.core.api.item;

import com.antonio32a.core.api.item.event.GameItemInteractEvent;
import com.antonio32a.core.api.item.tags.ItemTag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// This inspection doesn't make sense since we only want the getter for the id
@SuppressWarnings("LombokGetterMayBeUsed")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class GameItem implements Listener {
    public static final NamespacedKey ID_KEY = new NamespacedKey("ant", "item.id");
    private static final Component DEFAULT_STYLE = Component.text("")
        .style(Style.style(TextDecoration.ITALIC.withState(false)))
        .color(TextColor.color(Color.WHITE.asRGB()));

    @Getter protected final String id;
    protected final Material material;
    protected final Component name;
    protected List<ItemTag> tags = new ArrayList<>();
    @Nullable protected Integer customModelData;
    protected List<Component> description = new ArrayList<>();
    protected ItemRarity rarity = ItemRarity.COMMON;
    protected boolean unbreakable = true;

    /**
     * Called when the item is interacted with.
     *
     * @param event GameItemInteractEvent
     */
    public void onInteract(GameItemInteractEvent event) {
        // Override this if you want to handle the event
    }

    /**
     * Creates the entire item with all the attributes.
     * Overriding this is not recommending unless you know what you're doing.
     *
     * @param player The player to build the item for.
     * @return The built item stack.
     */
    @NotNull
    public ItemStack build(@NotNull Player player) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> {
            applyDisplayName(meta, player);
            applyLore(meta, player);
            applyCustomModelData(meta, player);
            applyUnbreakable(meta, player);
            applyItemFlags(meta, player);
            applyId(meta, player);
            applyAdditionalMeta(meta, player);
        });

        return item;
    }

    /**
     * Applies the display name of the item.
     *
     * @param meta   The current item meta.
     * @param player The player who to build the item for.
     */
    protected void applyDisplayName(@NotNull ItemMeta meta, @NotNull Player player) {
        meta.displayName(DEFAULT_STYLE.append(name));
    }

    /**
     * Applies the lore of the item.
     *
     * @param meta   The current item meta.
     * @param player The player who to build the item for.
     */
    protected void applyLore(@NotNull ItemMeta meta, @NotNull Player player) {
        List<Component> lines = new ArrayList<>();
        if (!tags.isEmpty()) {
            lines.addAll(tags.stream().map(ItemTag::render).toList());
            lines.add(Component.empty());
        }

        if (!description.isEmpty()) {
            lines.addAll(description);
            lines.add(Component.empty());
        }

        lines.add(rarity.getName());

        meta.lore(
            lines.stream()
                .map(DEFAULT_STYLE::append)
                .toList()
        );
    }

    /**
     * Applies the custom model data of the item.
     *
     * @param meta   The current item meta.
     * @param player The player who to build the item for.
     */
    protected void applyCustomModelData(@NotNull ItemMeta meta, @NotNull Player player) {
        if (customModelData != null) {
            meta.setCustomModelData(customModelData);
        }
    }

    /**
     * Applies the unbreakable attribute of the item.
     *
     * @param meta   The current item meta.
     * @param player The player who to build the item for.
     */
    protected void applyUnbreakable(@NotNull ItemMeta meta, @NotNull Player player) {
        meta.setUnbreakable(unbreakable);
    }

    /**
     * Applies the item flags of the item.
     *
     * @param meta   The current item meta.
     * @param player The player who to build the item for.
     */
    protected void applyItemFlags(@NotNull ItemMeta meta, @NotNull Player player) {
        meta.addItemFlags(
            ItemFlag.HIDE_ENCHANTS,
            ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE,
            ItemFlag.HIDE_DESTROYS,
            ItemFlag.HIDE_PLACED_ON,
            ItemFlag.HIDE_DYE,
            ItemFlag.HIDE_ITEM_SPECIFICS
        );
    }

    /**
     * Applies the id of the item.
     * You probably shouldn't override this unless you know what you're doing.
     *
     * @param meta   The current item meta.
     * @param player The player who to build the item for.
     */
    protected void applyId(@NotNull ItemMeta meta, @NotNull Player player) {
        meta.getPersistentDataContainer().set(ID_KEY, PersistentDataType.STRING, id);
    }

    /**
     * Applies additional meta to the item.
     * Override this if you wish to set e.g. the potion type or enchantments.
     *
     * @param meta   The current item meta.
     * @param player The player who to build the item for.
     */
    protected void applyAdditionalMeta(@NotNull ItemMeta meta, @NotNull Player player) {
        // Override this if you wish to set e.g. the potion type or enchantments.
    }
}
