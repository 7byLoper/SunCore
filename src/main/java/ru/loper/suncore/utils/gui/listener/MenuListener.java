package ru.loper.suncore.utils.gui.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import ru.loper.suncore.utils.gui.Menu;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory clicked = e.getClickedInventory();
        Inventory top = e.getView().getTopInventory();

        if (!(top.getHolder() instanceof Menu menu) || clicked == null) return;

        if (clicked.equals(top)) menu.onClick(e);
        else menu.onBottomInventoryClick(e);

    }


    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        if (inventory.getHolder() == null) return;
        if (inventory.getHolder() instanceof Menu menu) menu.onClose(e);
    }
}
