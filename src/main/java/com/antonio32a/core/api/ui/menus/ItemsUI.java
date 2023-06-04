package com.antonio32a.core.api.ui.menus;

import com.antonio32a.core.api.item.ItemRegistry;
import com.antonio32a.core.api.ui.items.UIBackItem;
import com.antonio32a.core.api.ui.items.UIForwardItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public final class ItemsUI extends PlayerUI {
    private final Gui gui = PagedGui.items()
        .setStructure(
            "x x x x x x x x x",
            "x x x x x x x x x",
            "x x x x x x x x x",
            "x x x x x x x x x",
            "< . . . . . . . >"
        )
        .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
        .addIngredient('<', new UIBackItem())
        .addIngredient('>', new UIForwardItem())
        .setContent(
            ItemRegistry.INSTANCE.getAll()
                .stream()
                .map(gameItem -> {
                    ItemStack builtItem = gameItem.build(player);
                    return (Item) new SimpleItem(
                        new ItemWrapper(builtItem),
                        event -> event.getPlayer().getInventory().addItem(builtItem));
                }).toList()
        ).build();

    private final Window window = Window.single()
        .setViewer(player)
        .setTitle("Items")
        .setGui(gui)
        .build();

    public ItemsUI(Player player) {
        super(player);
    }

    @Override
    public void open() {
        window.open();
    }
}
