package com.antonio32a.core.api.ui.menus;

import com.antonio32a.core.api.actionbar.ActionBarComponent;
import com.antonio32a.core.api.actionbar.ActionBarController;
import com.antonio32a.core.api.ui.items.UIBackItem;
import com.antonio32a.core.api.ui.items.UIForwardItem;
import com.antonio32a.core.api.ui.items.UITabItem;
import com.antonio32a.core.util.Formatting;
import com.antonio32a.core.util.GlobalModel;
import com.antonio32a.core.util.GlobalUnicode;
import com.antonio32a.core.util.Spacing;
import com.google.common.collect.Streams;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

/**
 * Allows players to configure their action bar component positions.
 */
@Slf4j
public class ActionBarSettingsUI extends PlayerUI {
    private static final Component TEXTURE = Formatting.formatUITexture(GlobalUnicode.ACTIONBAR_SETTINGS.toString());
    private final Window window;
    private final List<ActionBarComponent> components = ActionBarController.INSTANCE.getComponentsForPlayer(player);

    private final PagedGui<Item> pagesGui = PagedGui.items()
        .setStructure(
            "< . x x x x x . >"
        )
        .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
        .addIngredient('<', new UIBackItem())
        .addIngredient('>', new UIForwardItem())
        .build();

    public ActionBarSettingsUI(Player player) {
        super(player);
        this.window = Window.single()
            .setViewer(player)
            .setTitle(renderTitle(0))
            .setGui(mainGui)
            .build();
        updatePages();
    }

    private final TabGui mainGui = TabGui.normal()
        .setStructure(
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". . . . . . . . .",
            ". x x x x x x x .",
            ". . . . . . . . ."
        )
        .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
        .setTabs(
            components.stream()
                .map(component -> buildSingleActionBarGui(player, component))
                .toList()
        )
        .addTabChangeHandler(this::handleTabChange)
        .addModifier(gui -> gui.fillRectangle(0, 1, pagesGui, true))
        .build();

    @Override
    public void open() {
        window.open();
    }

    private void handleTabChange(int oldTab, int newTab) {
        window.changeTitle(renderTitle(newTab));
        // We have to re-render the pages every single time a tab is changed otherwise for some
        // reason it won't update the pages correctly, (this causes them to not be enchanted).
        updatePages();
    }

    private void updatePages() {
        pagesGui.setContent(
            Streams.mapWithIndex(
                components.stream(),
                (component, index) -> createPageItem(component, (int) index)
            ).toList()
        );
    }

    @NotNull
    private Item createPageItem(@NotNull ActionBarComponent component, int index) {
        ItemStack stack = new ItemBuilder(Material.MOJANG_BANNER_PATTERN)
            .addLoreLines(
                Formatting.parseUI(
                    "<gray>Click to edit the</gray> <white><name></white>",
                    Placeholder.unparsed("name", component.getName())
                ),
                Formatting.parseUI("<gray>action bar component.</gray>")
            )
            .setItemFlags(List.of(ItemFlag.HIDE_ITEM_SPECIFICS))
            .setDisplayName(component.getName())
            .get();

        UITabItem item = new UITabItem(index, stack);
        item.setGui(mainGui);
        return item;
    }

    @NotNull
    private AdventureComponentWrapper renderTitle(int index) {
        ActionBarComponent actionBarComponent = components.get(index);
        Component title = Component.text(actionBarComponent.getName()).color(NamedTextColor.DARK_GRAY);
        int titleWidth = Formatting.calculateWidth(title);

        Component result = TEXTURE
            .append(Component.text(Spacing.calculateSpacing(-GlobalUnicode.ACTIONBAR_SETTINGS.getActualWidth())))
            .append(Component.text(Spacing.calculateSpacing(Formatting.GUI_WIDTH / 2 + -titleWidth / 2)))
            .append(title)
            .append(Component.text(Spacing.calculateSpacing(Formatting.GUI_WIDTH / 2 + -titleWidth / 2)));
        return new AdventureComponentWrapper(result);
    }

    @NotNull
    private Gui buildSingleActionBarGui(@NotNull Player player, @Nullable ActionBarComponent component) {
        if (component == null) {
            return Gui.normal()
                .setStructure(". . . . . . .")
                .build();
        }

        return Gui.normal()
            .setStructure(". . < o > . .")
            .addIngredient(
                '<',
                new SimpleItem(
                    new ItemBuilder(GlobalModel.ARROW_LEFT.createStack())
                        .setDisplayName(Formatting.parseUI("<green>Move Left</green>"))
                        .addLoreLines(
                            Formatting.parseUI("<gray>Click to move the component left.</gray>"),
                            Formatting.parseUI("<gray>Shift-click to move 10 positions left.</gray>")
                        ),
                    event -> {
                        int toMove = event.getClickType().isShiftClick() ? 10 : 1;
                        component.updatePosition(player, component.getFor(player).getPosition() - toMove);
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);
                    }
                )
            )
            .addIngredient(
                '>',
                new SimpleItem(
                    new ItemBuilder(GlobalModel.ARROW_RIGHT.createStack())
                        .setDisplayName(Formatting.parseUI("<green>Move Right</green>"))
                        .addLoreLines(
                            Formatting.parseUI("<gray>Click to move the component right.</gray>"),
                            Formatting.parseUI("<gray>Shift-click to move 10 positions right.</gray>")
                        ),
                    event -> {
                        int toMove = event.getClickType().isShiftClick() ? 10 : 1;
                        component.updatePosition(player, component.getFor(player).getPosition() + toMove);
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }
                )
            )
            .addIngredient(
                'o',
                new SimpleItem(
                    new ItemBuilder(GlobalModel.CHECKMARK_FALSE.createStack())
                        .setDisplayName(Formatting.parseUI("<green>Reset Position</green>"))
                        .addLoreLines(
                            Formatting.parseUI("<gray>Click to reset the position of the component.</gray>")
                        ),
                    event -> {
                        component.updatePosition(player, component.getDefaultPosition());
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                    }
                )
            ).build();
    }
}
