package com.antonio32a.core.listener;

import com.antonio32a.core.api.item.GameItem;
import com.antonio32a.core.api.item.ItemRegistry;
import com.antonio32a.core.api.item.event.GameItemInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public final class ItemListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    private void onInteract(PlayerInteractEvent event) {
        @Nullable ItemStack item = event.getItem();
        if (item == null) return;
        @Nullable ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null) return;

        String id = itemMeta.getPersistentDataContainer().get(GameItem.ID_KEY, PersistentDataType.STRING);
        if (id == null) return;
        GameItem gameItem = ItemRegistry.INSTANCE.getById(id);
        if (gameItem == null) return;

        GameItemInteractEvent gameItemInteractEvent = new GameItemInteractEvent(
            event.getPlayer(),
            event.getAction(),
            item,
            gameItem,
            event.getClickedBlock(),
            event.getBlockFace(),
            event.getHand(),
            event.getClickedBlock() != null ? event.getClickedBlock().getLocation() : null
        );

        gameItemInteractEvent.callEvent();
        gameItem.onInteract(gameItemInteractEvent);

        if (gameItemInteractEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }
}
