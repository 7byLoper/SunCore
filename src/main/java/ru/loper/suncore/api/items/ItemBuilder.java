package ru.loper.suncore.api.items;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.utils.Colorize;

import java.util.*;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack item;

    public ItemBuilder(ItemStack item) {
        if (item == null) {
            this.item = new ItemStack(Material.AIR);
        } else {
            this.item = item.clone();
        }
    }

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
    }

    public static ItemBuilder fromConfig(ConfigurationSection section) {
        String material = section.getString("material", "AIR");
        String name = section.getString("display_name", "");
        boolean glow = section.getBoolean("glow");
        boolean hide_attributes = section.getBoolean("hide_attributes");
        boolean hide_enchantments = section.getBoolean("hide_enchantments");
        boolean unbreakable = section.getBoolean("unbreakable");

        List<String> lore = section.getStringList("lore");
        List<String> attributes = section.getStringList("attributes");
        List<String> enchantments = section.getStringList("enchantments");

        ItemBuilder builder = material.startsWith("basehead-") ?
                new ItemBuilder(SkullUtils.getCustomSkull(material)) :
                new ItemBuilder(Material.valueOf(material));

        builder.name(name)
                .lore(lore)
                .glow(glow);

        if (hide_attributes) builder.hideAttributes();
        if (hide_enchantments) builder.hideEnchants();
        if (section.contains("model_data")) builder.model(section.getInt("model_data"));
        if (!attributes.isEmpty()) builder.attributes(attributes);
        if (!enchantments.isEmpty()) builder.enchantments(enchantments);
        if (unbreakable) {
            ItemMeta meta = builder.meta();
            if (meta != null) {
                meta.setUnbreakable(true);
                builder.meta(meta);
            }
        }

        return builder;
    }

    public ItemBuilder enchantments(List<String> enchantments){
        for (String line : enchantments) {
            try {
                String[] parts = line.split(":");
                if (parts.length != 2) continue;

                String enchantName = parts[0].toLowerCase();
                int level = Integer.parseInt(parts[1]);

                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
                if (enchantment != null) {
                    enchantment(enchantment, level);
                } else {
                    SunCore.getInstance().getLogger().warning("Unknown enchantment: " + enchantName);
                }
            } catch (Exception e) {
                SunCore.getInstance().getLogger().warning("Invalid enchantment format: " + line);
            }
        }
        return this;
    }

    public ItemBuilder attributes(List<String> attributeStrings) {
        return addAttributes(parseAttributes(attributeStrings));
    }

    private ItemBuilder addAttributes(List<AttributeData> attributes) {
        if (attributes == null || attributes.isEmpty()) return this;

        ItemMeta meta = meta();
        if (meta == null) return this;

        for (AttributeData data : attributes) {
            AttributeModifier modifier = new AttributeModifier(
                    UUID.randomUUID(),
                    "custom_attribute_" + data.attribute.name(),
                    data.value,
                    AttributeModifier.Operation.ADD_NUMBER,
                    data.slot
            );
            meta.addAttributeModifier(data.attribute, modifier);
        }

        meta(meta);
        return this;
    }

    private List<AttributeData> parseAttributes(List<String> attributeStrings) {
        return attributeStrings.stream()
                .map(this::parseAttributeString)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private AttributeData parseAttributeString(String str) {
        try {
            String[] parts = str.split(":");
            if (parts.length != 3) return null;

            EquipmentSlot slot = parseSlot(parts[0]);
            Attribute attribute = parseAttribute(parts[1]);
            double value = Double.parseDouble(parts[2]);

            if (slot != null && attribute != null) {
                return new AttributeData(slot, attribute, value);
            }
        } catch (Exception e) {
            SunCore.getInstance().getLogger().warning("Invalid attribute format: " + str);
        }
        return null;
    }

    private EquipmentSlot parseSlot(String slotStr) {
        return switch (slotStr.toLowerCase()) {
            case "hand", "mainhand" -> EquipmentSlot.HAND;
            case "offhand" -> EquipmentSlot.OFF_HAND;
            case "head" -> EquipmentSlot.HEAD;
            case "chest" -> EquipmentSlot.CHEST;
            case "legs" -> EquipmentSlot.LEGS;
            case "feet" -> EquipmentSlot.FEET;
            default -> null;
        };
    }

    private Attribute parseAttribute(String attrStr) {
        return switch (attrStr.toLowerCase()) {
            case "generic_max_health" -> Attribute.GENERIC_MAX_HEALTH;
            case "generic_follow_range" -> Attribute.GENERIC_FOLLOW_RANGE;
            case "generic_knockback_resistance" -> Attribute.GENERIC_KNOCKBACK_RESISTANCE;
            case "generic_movement_speed" -> Attribute.GENERIC_MOVEMENT_SPEED;
            case "generic_attack_damage" -> Attribute.GENERIC_ATTACK_DAMAGE;
            case "generic_attack_speed" -> Attribute.GENERIC_ATTACK_SPEED;
            case "generic_armor" -> Attribute.GENERIC_ARMOR;
            case "generic_armor_toughness" -> Attribute.GENERIC_ARMOR_TOUGHNESS;
            case "generic_luck" -> Attribute.GENERIC_LUCK;
            default -> null;
        };
    }

    private record AttributeData(EquipmentSlot slot, Attribute attribute, double value) { }

    public ItemMeta meta() {
        return item.getItemMeta();
    }

    public ItemBuilder meta(ItemMeta meta) {
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder model(int model) {
        ItemMeta meta = meta();
        if (meta == null) return this;
        meta.setCustomModelData(model);
        return meta(meta);
    }

    public ItemBuilder flags(ItemFlag... itemFlags) {
        ItemMeta meta = meta();
        if (meta == null) return this;
        meta.addItemFlags(itemFlags);
        return meta(meta);
    }

    public ItemBuilder flags(String... itemFlags) {
        if (itemFlags == null || itemFlags.length == 0) return this;
        ItemMeta meta = meta();
        if (meta == null) return this;
        for (String flagName : itemFlags) {
            try {
                meta.addItemFlags(ItemFlag.valueOf(flagName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                SunCore.getInstance().getLogger().warning("Unknown item flag: " + flagName);
            }
        }
        return meta(meta);
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        if (enchantment == null) return this;
        ItemMeta meta = meta();
        if (meta == null) return this;
        meta.addEnchant(enchantment, level, true);
        return meta(meta);
    }

    public String name() {
        return meta().hasDisplayName() ? meta().getDisplayName() : "";
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = meta();
        if (meta == null) return this;
        meta.setDisplayName(Colorize.parse(name));
        return meta(meta);
    }

    public List<String> lore() {
        return meta().getLore() == null ? new ArrayList<>() : meta().getLore();
    }

    public ItemBuilder lore(List<String> lore) {
        ItemMeta meta = meta();
        if (meta == null) return this;
        meta.setLore(Colorize.parse(lore));
        return meta(meta);
    }

    public ItemBuilder addLore(String... lore) {
        ItemMeta meta = meta();
        if (meta == null) return this;
        List<String> old = lore();
        Collections.addAll(old, lore);
        meta.setLore(old);
        return meta(meta);
    }

    public ItemBuilder addLoreAbove(String... lore) {
        ItemMeta meta = meta();
        if (meta == null) return this;
        List<String> old = lore();
        List<String> toAdd = Arrays.asList(lore);
        Collections.reverse(toAdd);
        old.addAll(0, toAdd);
        meta.setLore(old);
        return meta(meta);
    }

    public ItemBuilder color(Color color) {
        ItemMeta meta = meta();
        if (!(meta instanceof LeatherArmorMeta) || color == null) return this;
        ((LeatherArmorMeta) meta).setColor(color);
        return meta(meta);
    }

    public ItemBuilder hideAttributes() {
        return flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_POTION_EFFECTS);
    }

    public ItemBuilder hideEnchants() {
        return flags(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public int amount() {
        return item.getAmount();
    }

    public ItemBuilder glow(boolean isGlow) {
        ItemMeta meta = meta();
        if (meta == null || !isGlow) return this;
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta(meta);
        return flags(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemStack build() {
        return item.clone();
    }

    public ItemBuilder save(ConfigurationSection section) {
        section.set("material", item.getType().name());
        section.set("amount", item.getAmount());

        ItemMeta meta = meta();
        if (meta != null) {
            section.set("display_name", meta.getDisplayName());
            section.set("lore", meta.getLore());

            if (meta.hasCustomModelData()) {
                section.set("model_data", meta.getCustomModelData());
            }

            if (!meta.getItemFlags().isEmpty()) {
                section.set("flags", meta.getItemFlags().stream()
                        .map(ItemFlag::name)
                        .collect(Collectors.toList()));
            }

            if (!meta.getEnchants().isEmpty()) {
                ConfigurationSection enchantsSection = section.createSection("enchants");
                meta.getEnchants().forEach((enchant, level) ->
                        enchantsSection.set(enchant.getKey().getKey(), level));
            }

            if (meta instanceof LeatherArmorMeta leatherMeta) {
                section.set("color", leatherMeta.getColor().asRGB());
            }

            if (meta.hasEnchant(Enchantment.DURABILITY) &&
                    meta.getEnchantLevel(Enchantment.DURABILITY) == 1 &&
                    meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
                section.set("glow", true);
            }

            section.set("hide_attributes", meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES));
            section.set("hide_enchantments", meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS));
        }

        return this;
    }
}