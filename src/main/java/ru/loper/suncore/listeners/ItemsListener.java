package ru.loper.suncore.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.config.CoreConfigManager;
import ru.loper.suncore.utils.Colorize;

@RequiredArgsConstructor
public class ItemsListener implements Listener {
    private final CoreConfigManager coreConfigManager;

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(SunCore.getPlaceKey(), PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(ItemSpawnEvent event) {
        Item item = event.getEntity();
        if (item.isCustomNameVisible() || !coreConfigManager.isItemsTagHider()) return;
        item.setCustomName(Colorize.parse("&cЛучший тгк @byLoper"));
    }
}
