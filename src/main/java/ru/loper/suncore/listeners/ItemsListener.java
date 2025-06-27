package ru.loper.suncore.listeners;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import ru.loper.suncore.SunCore;

public class ItemsListener implements Listener {
    @Getter
    private static final NamespacedKey PLACED_KEY = new NamespacedKey(SunCore.getInstance(), "placed");

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack itemStack = event.getItemInHand();
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().getPersistentDataContainer().has(PLACED_KEY, PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }
}
