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
import org.jetbrains.annotations.Nullable;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.utils.Colorize;
import ru.loper.suncore.utils.items.ItemBuilder;

import java.util.*;

@Getter
public abstract class Menu implements InventoryHolder {
    protected final List<Button> buttons = new ArrayList<>();
    protected final Map<Integer, ItemStack> items = new HashMap<>();
    private final SunCore plugin = SunCore.getInstance();

    @Setter private Inventory inventory;
    @Setter private Menu parent;
    private Player opener;

    public Menu() {
    }

    public Menu(Menu parent) {
        this.parent = parent;
    }

    public void show(@NotNull Player player) {
        opener = player;
        inventory = Bukkit.createInventory(this, getSize(), Colorize.parse(Optional.ofNullable(getTitle()).orElse(" ")));
        populateInventory();
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.openInventory(inventory), 1L);
    }

    private void populateInventory() {
        inventory.clear();
        buttons.clear();
        items.clear();

        getItemsAndButtons();

        for (Button button : buttons) {
            for (int slot : button.getSlots()) {
                inventory.setItem(slot, button.getItemStack());
            }
        }

        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            inventory.setItem(entry.getKey(), inventory.getItem(entry.getKey()) == null ? entry.getValue() : inventory.getItem(entry.getKey()));
        }
    }

    public void onClick(@NotNull InventoryClickEvent event) {
        for (Button button : buttons) {
            if (button.getSlots().contains(event.getSlot())) {
                button.onClick(event);
                break;
            }
        }
        event.setCancelled(true);
    }

    public void onClose(@NotNull InventoryCloseEvent event) {
        // Переписывайте под себя через @Override
    }

    public void removeButton(int slot) {
        inventory.clear(slot);
        buttons.removeIf(btn -> btn.getSlots().contains(slot));
    }

    public boolean isSlotOccupied(int slot) {
        return items.containsKey(slot) || buttons.stream().anyMatch(button -> button.getSlots().contains(slot));
    }

    public void addDecorItems(@NotNull Material material, @NotNull List<Integer> slots) {
        ItemStack decor = new ItemBuilder(material)
                .name(" ")
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .build();
        for (int slot : slots) {
            items.put(slot, decor);
        }
    }

    public void addDecorItems(@NotNull Material material, @NotNull Integer... slots) {
        addDecorItems(material, Arrays.asList(slots));
    }

    public void addReturnButton(int slot, @NotNull ItemBuilder builder) {
        if (parent == null) return;

        buttons.add(new Button(builder.build(), slot) {
            @Override
            public void onClick(@NotNull InventoryClickEvent event) {
                parent.show((Player) event.getWhoClicked());
            }
        });
    }

    @Nullable
    public Player getViewer() {
        List<Player> viewers = inventory.getViewers().stream()
                .filter(viewer -> viewer instanceof Player)
                .map(viewer -> (Player) viewer)
                .toList();
        return viewers.isEmpty() ? null : viewers.get(0);
    }

    protected Menu getInstance() {
        return this;
    }

    public abstract @Nullable String getTitle();
    public abstract int getSize();
    public abstract void getItemsAndButtons();

}
