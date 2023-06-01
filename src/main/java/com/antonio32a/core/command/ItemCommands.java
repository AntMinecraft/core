package com.antonio32a.core.command;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.antonio32a.core.api.item.ItemRegistry;
import com.antonio32a.core.api.ui.items.UIBackItem;
import com.antonio32a.core.api.ui.items.UIForwardItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

public final class ItemCommands {
    @CommandMethod("items")
    @CommandPermission("ant.command.items")
    public void items(@NotNull Player player) {
        Gui itemsGui = buildItemsGui(player);
        Window.single()
            .setViewer(player)
            .setTitle("Items")
            .setGui(itemsGui)
            .build()
            .open();
    }

    @NotNull
    private Gui buildItemsGui(@NotNull Player player) {
        List<Item> items = ItemRegistry.INSTANCE.getAll()
            .stream()
            .map(gameItem -> {
                ItemStack builtItem = gameItem.build(player);
                return (Item) new SimpleItem(
                    new ItemWrapper(builtItem),
                    event -> event.getPlayer().getInventory().addItem(builtItem));
            }).toList();

        Item border = new SimpleItem(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE));
        return PagedGui.items()
            .setStructure(
                "x x x x x x x x x",
                "x x x x x x x x x",
                "x x x x x x x x x",
                "x x x x x x x x x",
                "< x x x x x x x >"
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addIngredient('#', border)
            .addIngredient('<', new UIBackItem())
            .addIngredient('>', new UIForwardItem())
            .setContent(items)
            .build();
    }
}
