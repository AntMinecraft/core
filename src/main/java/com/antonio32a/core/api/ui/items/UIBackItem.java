package com.antonio32a.core.api.ui.items;

import com.antonio32a.core.util.Formatting;
import com.antonio32a.core.util.GlobalModel;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public class UIBackItem extends PageItem {
    public UIBackItem() {
        super(false);
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        GlobalModel model;
        AdventureComponentWrapper lore;

        if (gui.hasPreviousPage()) {
            model = GlobalModel.ARROW_LEFT;
            lore = Formatting.parseUI(
                "<gray>Go to page</gray> <yellow><next_page></yellow>",
                Placeholder.unparsed("next_page", String.valueOf(gui.getCurrentPage()))
            );
        } else {
            model = GlobalModel.ARROW_LEFT_DISABLED;
            lore = Formatting.parseUI("<red>You can't go further back</red>");
        }

        return new ItemBuilder(model.getMaterial())
            .setCustomModelData(model.getCustomModelData())
            .setDisplayName(Formatting.parseUI("<green>Previous Page</green>"))
            .addLoreLines(lore);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        super.handleClick(clickType, player, event);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
    }
}
