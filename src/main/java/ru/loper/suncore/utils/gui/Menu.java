package ru.loper.suncore.utils.gui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.utils.Colorize;
import ru.loper.suncore.utils.items.ItemBuilder;

import java.util.*;

@Getter
public abstract class Menu implements InventoryHolder {
    protected final List<Button> buttons = new ArrayList<>();
    protected final Map<Integer, ItemStack> items = new HashMap<>();
    private final Menu instance;
    private final SunCore plugin = SunCore.getInstance();
    @Setter
    private Inventory inventory;
    @Setter
    private Menu parent;
    private Player opener;

    public Menu() {
        instance = this;
    }

    public Menu(Menu parent) {
        this();
        this.parent = parent;
    }

    public void onClick(InventoryClickEvent e) {
        for (Button button : buttons) {
            if (button.getSlots().contains(e.getSlot())) {
                button.onClick(e);
                break;
            }
        }
        e.setCancelled(true);
    }

    public void removeButton(int slot) {
        inventory.clear(slot);
        Button button = buttons.stream().filter(btn -> btn.getSlots().contains(slot)).findFirst().orElse(null);
        if (button == null) return;
        buttons.remove(button);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void addReturnButton(int slot, ItemBuilder builder) {
        if (parent == null) return;
        Button button = new Button(builder.build(), slot) {
            @Override
            public void onClick(InventoryClickEvent event) {
                parent.show((Player) event.getWhoClicked());
            }
        };
        buttons.add(button);
    }

    public void show(Player player) {
        opener = player;
        inventory = Bukkit.createInventory(this, getSize(), Colorize.parse(getTitle() == null ? " " : getTitle()));
        setItemsAndButtons();
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.openInventory(inventory), 1L);
    }

    private void clearItemsAndButtons() {
        inventory.clear();
        buttons.clear();
        items.clear();
    }


    private void setItemsAndButtons() {
        clearItemsAndButtons();
        getItemsAndButtons();
        buttons.forEach(button -> button.getSlots().forEach(slot -> inventory.setItem(slot, button.getItemStack())));
        items.keySet().forEach(slot -> {
            if (inventory.getItem(slot) == null) inventory.setItem(slot, items.get(slot));
        });
    }

    public void addDecorItems(Material material, List<Integer> slots) {
        ItemStack decor = new ItemBuilder(material)
                .name(" ")
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .build();
        slots.forEach(slot -> items.put(slot, decor));
    }

    public void addDecorItems(Material material, Integer... slots) {
        addDecorItems(material, Arrays.stream(slots).toList());
    }

    public Player getViewer() {
        try {
            return (Player) inventory.getViewers().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public abstract String getTitle();

    public abstract int getSize();

    public abstract void getItemsAndButtons();

    public void onClose(InventoryCloseEvent e){

    }

}
