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

public final class UIForwardItem extends PageItem {
    public UIForwardItem() {
        super(true);
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        Component lore;
        if (gui.hasNextPage()) {
            lore = Formatting.parse(
                "<gray>Go to page</gray> <yellow><nextPage></yellow><gray>/</gray><yellow><maxPages></yellow",
                Placeholder.unparsed("nextPage", String.valueOf(gui.getCurrentPage() + 2)),
                Placeholder.unparsed("maxPages", String.valueOf(gui.getPageAmount()))
            );
        } else {
            lore = Formatting.parse("<red>There are no more pages</red>");
        }

        return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
            .setDisplayName(new AdventureComponentWrapper(Formatting.parse("<green>Next Page</green>")))
            .addLoreLines(new AdventureComponentWrapper(lore));
    }
}
