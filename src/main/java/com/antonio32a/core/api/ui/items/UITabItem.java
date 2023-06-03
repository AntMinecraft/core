package com.antonio32a.core.api.ui.items;

import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.controlitem.TabItem;

public class UITabItem extends TabItem {
    private final int tab;
    private final ItemStack icon;

    public UITabItem(int tab, ItemStack icon) {
        super(tab);
        this.tab = tab;
        this.icon = icon;
    }

    @Override
    public ItemProvider getItemProvider(TabGui gui) {
        ItemStack stack = icon.clone();
        if (gui.getCurrentTab() == tab) {
            stack.editMeta(meta -> {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            });
        }

        return new ItemWrapper(stack);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        super.handleClick(clickType, player, event);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }
}
