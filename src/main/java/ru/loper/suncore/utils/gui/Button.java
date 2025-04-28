package ru.loper.suncore.utils.gui;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import ru.loper.suncore.utils.items.ItemBuilder;

import java.util.Arrays;
import java.util.List;

@Getter
public abstract class Button {
    private final List<Integer> slots;
    private final ItemStack itemStack;

    public Button(ItemStack itemStack, Integer... slots) {
        this.slots = Arrays.asList(slots);
        this.itemStack = new ItemBuilder(itemStack)
                .hideAttributes()
                .build();
    }

    public abstract void onClick(InventoryClickEvent event);
}
