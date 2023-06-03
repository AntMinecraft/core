package com.antonio32a.core.api.ui.items;

import com.antonio32a.core.util.Formatting;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public final class UIBackItem extends PageItem {
    public UIBackItem() {
        super(false);
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        Component lore;
        if (gui.hasPreviousPage()) {
            lore = Formatting.parse(
                "<gray>Go to page</gray> <yellow><next_page></yellow><gray>/</gray><yellow><max_pages></yellow>",
                Placeholder.unparsed("next_page", String.valueOf(gui.getCurrentPage())),
                Placeholder.unparsed("max_pages", String.valueOf(gui.getPageAmount()))
            );
        } else {
            lore = Formatting.parse("<red>You can't go further back</red>");
        }

        return new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
            .setDisplayName(new AdventureComponentWrapper(Formatting.parse("<green>Previous Page</green>")))
            .addLoreLines(new AdventureComponentWrapper(lore));
    }
}
