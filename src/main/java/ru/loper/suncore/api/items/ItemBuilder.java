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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import ru.loper.suncore.SunCore;
import ru.loper.suncore.utils.Colorize;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс-строитель для создания и модификации ItemStack с использованием fluent-интерфейса.
 * Предоставляет методы для настройки различных аспектов предметов в Minecraft.
 */
public class ItemBuilder {

    private final ItemStack item;

    /**
     * Конструктор ItemBuilder на основе существующего ItemStack.
     *
     * @param item ItemStack для модификации (создаётся клон, чтобы не изменять оригинал)
     */
    public ItemBuilder(ItemStack item) {
        this.item = item != null ? item.clone() : new ItemStack(Material.AIR);
    }

    /**
     * Конструктор ItemBuilder с указанным материалом.
     *
     * @param material Тип материала для нового ItemStack
     */
    public ItemBuilder(Material material) {
        this.item = new ItemStack(material != null ? material : Material.AIR);
    }

    /**
     * Создаёт ItemBuilder на основе секции конфигурации.
     *
     * @param section Секция конфигурации, содержащая данные о предмете
     * @return Новый экземпляр ItemBuilder с параметрами из конфига
     */
    public static ItemBuilder fromConfig(ConfigurationSection section) {
        if (section == null) {
            return new ItemBuilder(Material.AIR);
        }

        String materialStr = section.getString("material", "AIR");
        ItemBuilder builder = materialStr.startsWith("basehead-") ?
                new ItemBuilder(SkullUtils.getCustomSkull(materialStr)) :
                new ItemBuilder(parseMaterial(materialStr));

        builder.name(section.getString("display_name", ""))
                .lore(section.getStringList("lore"))
                .glow(section.getBoolean("glow"))
                .unbreakable(section.getBoolean("unbreakable"))
                .placed(section.getBoolean("placed", true));

        if (!section.getBoolean("placed", true)) {
            builder.placed(false);
        }

        if (section.contains("model_data")) {
            builder.model(section.getInt("model_data"));
        }

        if (section.getBoolean("hide_attributes")) {
            builder.hideAttributes();
        }

        if (section.getBoolean("hide_enchantments")) {
            builder.hideEnchants();
        }

        if (section.contains("color")) {
            String colorStr = section.getString("color");
            if (colorStr != null) {
                try {
                    String[] rgb = colorStr.split(",");
                    if (rgb.length == 3) {
                        int r = Integer.parseInt(rgb[0].trim());
                        int g = Integer.parseInt(rgb[1].trim());
                        int b = Integer.parseInt(rgb[2].trim());
                        builder.color(Color.fromRGB(r, g, b));
                    } else {
                        SunCore.getInstance().getLogger().warning("Неверный формат цвета в конфиге: " + colorStr);
                    }
                } catch (NumberFormatException e) {
                    SunCore.getInstance().getLogger().warning("Ошибка при разборе цвета: " + colorStr);
                }
            }
        }

        List<String> attributes = section.getStringList("attributes");
        if (!attributes.isEmpty()) {
            builder.attributes(attributes);
        }

        List<String> enchantments = section.getStringList("enchantments");
        if (!enchantments.isEmpty()) {
            builder.enchantments(enchantments);
        }

        return builder;
    }

    /**
     * Безопасно парсит строку материала, возвращая AIR при неверном формате.
     *
     * @param materialStr Название материала для парсинга
     * @return Спарсенный Material или AIR, если материал неверный
     */
    private static Material parseMaterial(String materialStr) {
        try {
            return Material.valueOf(materialStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            SunCore.getInstance().getLogger().warning("Неверный материал: " + materialStr);
            return Material.AIR;
        }
    }

    /**
     * Устанавливает, помечен ли предмет как размещённый.
     *
     * @param placed Флаг, указывающий, размещён ли предмет
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder placed(boolean placed) {
        if (!placed) {
            return namespacedKey(SunCore.getPlaceKey(), PersistentDataType.STRING, "value");
        }

        return removeNamespacedKey(SunCore.getPlaceKey());
    }

    /**
     * Применяет несколько зачарований из списка строк в формате "enchant:level".
     *
     * @param enchantments Список строк с зачарованиями
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder enchantments(List<String> enchantments) {
        if (enchantments == null) return this;
        enchantments.forEach(this::applyEnchantment);
        return this;
    }

    /**
     * Применяет одно зачарование из строки в формате "enchant:level".
     *
     * @param enchantmentStr Строка с зачарованием для парсинга и применения
     */
    private void applyEnchantment(String enchantmentStr) {
        try {
            String[] parts = enchantmentStr.split(":");
            if (parts.length != 2) return;

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(parts[0].toLowerCase()));
            if (enchantment == null) {
                SunCore.getInstance().getLogger().warning("Неизвестное зачарование: " + parts[0]);
                return;
            }

            int level = Integer.parseInt(parts[1]);
            enchantment(enchantment, level);
        } catch (NumberFormatException e) {
            SunCore.getInstance().getLogger().warning("Неверный уровень зачарования в: " + enchantmentStr);
        } catch (Exception e) {
            SunCore.getInstance().getLogger().warning("Неверный формат зачарования: " + enchantmentStr);
        }
    }

    /**
     * Применяет несколько атрибутов из списка строк в формате "slot:attribute:value".
     *
     * @param attributes Список строк с атрибутами
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder attributes(List<String> attributes) {
        if (attributes == null) return this;
        return addAttributes(parseAttributes(attributes));
    }

    /**
     * Добавляет спарсенные данные атрибутов к предмету.
     *
     * @param attributes Список объектов AttributeData
     * @return Этот экземпляр ItemBuilder
     */
    private ItemBuilder addAttributes(List<AttributeData> attributes) {
        if (attributes == null || attributes.isEmpty()) return this;

        ItemMeta meta = meta();
        if (meta == null) return this;

        attributes.forEach(data -> {
            AttributeModifier modifier = new AttributeModifier(
                    UUID.randomUUID(),
                    "custom_attribute_" + data.attribute.name(),
                    data.value,
                    AttributeModifier.Operation.ADD_NUMBER,
                    data.slot
            );
            meta.addAttributeModifier(data.attribute, modifier);
        });

        return meta(meta);
    }

    /**
     * Парсит строки атрибутов в объекты AttributeData.
     *
     * @param attributeStrings Список строк атрибутов
     * @return Список спарсенных объектов AttributeData
     */
    private List<AttributeData> parseAttributes(List<String> attributeStrings) {
        return attributeStrings.stream()
                .map(this::parseAttributeString)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Парсит одну строку атрибута в формате "slot:attribute:value".
     *
     * @param str Строка атрибута для парсинга
     * @return Спарсенный AttributeData или null, если формат неверный
     */
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
            SunCore.getInstance().getLogger().warning("Неверный формат атрибута: " + str);
        }
        return null;
    }

    /**
     * Парсит слот экипировки из строки.
     *
     * @param slotStr Строка со слотом для парсинга
     * @return EquipmentSlot или null, если слот неверный
     */
    private EquipmentSlot parseSlot(String slotStr) {
        return switch (slotStr.toLowerCase()) {
            case "hand", "mainhand" -> EquipmentSlot.HAND;
            case "offhand", "off_hand" -> EquipmentSlot.OFF_HAND;
            case "head" -> EquipmentSlot.HEAD;
            case "chest" -> EquipmentSlot.CHEST;
            case "legs" -> EquipmentSlot.LEGS;
            case "feet" -> EquipmentSlot.FEET;
            default -> null;
        };
    }

    /**
     * Парсит атрибут из строки.
     *
     * @param attrStr Строка с атрибутом для парсинга
     * @return Attribute или null, если атрибут неверный
     */
    private Attribute parseAttribute(String attrStr) {
        try {
            return Attribute.valueOf(attrStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Запись для хранения данных атрибута.
     */
    private record AttributeData(EquipmentSlot slot, Attribute attribute, double value) {
    }

    /**
     * Получает ItemMeta текущего предмета.
     *
     * @return ItemMeta или null, если не применимо
     */
    public ItemMeta meta() {
        return item.getItemMeta();
    }

    /**
     * Устанавливает ItemMeta для текущего предмета.
     *
     * @param meta ItemMeta для установки
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder meta(ItemMeta meta) {
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Устанавливает пользовательские данные модели для предмета.
     *
     * @param model Значение пользовательских данных модели
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder model(int model) {
        ItemMeta meta = meta();
        if (meta == null) return this;
        meta.setCustomModelData(model);
        return meta(meta);
    }

    /**
     * Добавляет флаги предмета.
     *
     * @param itemFlags Флаги предмета для добавления
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder flags(ItemFlag... itemFlags) {
        ItemMeta meta = meta();
        if (meta == null || itemFlags == null) return this;
        meta.addItemFlags(itemFlags);
        return meta(meta);
    }

    /**
     * Добавляет флаги предмета из строковых представлений.
     *
     * @param itemFlags Названия флагов предмета для добавления
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder flags(String... itemFlags) {
        if (itemFlags == null) return this;
        ItemMeta meta = meta();
        if (meta == null) return this;
        for (String flagName : itemFlags) {
            try {
                meta.addItemFlags(ItemFlag.valueOf(flagName.toUpperCase()));
            } catch (IllegalArgumentException e) {
                SunCore.getInstance().getLogger().warning("Неизвестный флаг предмета: " + flagName);
            }
        }
        return meta(meta);
    }

    /**
     * Добавляет зачарование к предмету.
     *
     * @param enchantment Зачарование для добавления
     * @param level       Уровень зачарования
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        if (enchantment == null) return this;
        ItemMeta meta = meta();
        if (meta == null) return this;
        meta.addEnchant(enchantment, level, true);
        return meta(meta);
    }

    /**
     * Получает отображаемое имя предмета.
     *
     * @return Отображаемое имя или пустая строка, если не установлено
     */
    public String name() {
        ItemMeta meta = meta();
        return meta != null && meta.hasDisplayName() ? meta.getDisplayName() : "";
    }

    /**
     * Устанавливает отображаемое имя предмета.
     *
     * @param name Отображаемое имя для установки
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder name(String name) {
        ItemMeta meta = meta();
        if (meta == null || name == null) return this;
        meta.setDisplayName(Colorize.parse(name));
        return meta(meta);
    }

    /**
     * Получает описание (lore) предмета.
     *
     * @return Список строк описания или пустой список, если не установлено
     */
    public List<String> lore() {
        ItemMeta meta = meta();
        return meta != null && meta.getLore() != null ? meta.getLore() : new ArrayList<>();
    }

    /**
     * Устанавливает описание (lore) предмета.
     *
     * @param lore Список строк описания для установки
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder lore(List<String> lore) {
        ItemMeta meta = meta();
        if (meta == null || lore == null) return this;
        meta.setLore(Colorize.parse(lore));
        return meta(meta);
    }

    /**
     * Добавляет строки описания к существующему описанию.
     *
     * @param lore Строки описания для добавления
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder addLore(String... lore) {
        if (lore == null) return this;
        ItemMeta meta = meta();
        if (meta == null) return this;
        List<String> currentLore = lore();
        currentLore.addAll(Colorize.parse(Arrays.asList(lore)));
        meta.setLore(currentLore);
        return meta(meta);
    }

    /**
     * Добавляет строки описания перед существующим описанием.
     *
     * @param lore Строки описания для добавления в начало
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder addLoreAbove(String... lore) {
        if (lore == null) return this;
        ItemMeta meta = meta();
        if (meta == null) return this;
        List<String> currentLore = lore();
        List<String> toAdd = Colorize.parse(Arrays.asList(lore));
        currentLore.addAll(0, toAdd);
        meta.setLore(currentLore);
        return meta(meta);
    }

    /**
     * Устанавливает цвет для предметов кожаной брони или зелий.
     *
     * @param color Цвет для установки
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder color(Color color) {
        ItemMeta meta = meta();
        if (meta == null || color == null) return this;
        if (meta instanceof LeatherArmorMeta leatherMeta) {
            leatherMeta.setColor(color);
        } else if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(color);
        }
        return meta(meta);
    }

    /**
     * Скрывает атрибуты предмета.
     *
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder hideAttributes() {
        return flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_POTION_EFFECTS);
    }

    /**
     * Скрывает зачарования предмета.
     *
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder hideEnchants() {
        return flags(ItemFlag.HIDE_ENCHANTS);
    }

    /**
     * Устанавливает, является ли предмет неразрушаемым.
     *
     * @param unbreakable Флаг неразрушаемости
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        ItemMeta meta = meta();
        if (meta == null) return this;
        meta.setUnbreakable(unbreakable);
        return meta(meta);
    }

    /**
     * Устанавливает количество предметов в стеке.
     *
     * @param amount Количество для установки
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder amount(int amount) {
        item.setAmount(Math.max(1, Math.min(amount, item.getMaxStackSize())));
        return this;
    }

    /**
     * Устанавливает материал предмета.
     *
     * @param material Материал для установки
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder material(Material material) {
        item.setType(material != null ? material : Material.AIR);
        return this;
    }

    /**
     * Получает текущий материал предмета.
     *
     * @return Текущий Material
     */
    public Material material() {
        return item.getType();
    }

    /**
     * Получает текущее количество предметов в стеке.
     *
     * @return Текущее количество
     */
    public int amount() {
        return item.getAmount();
    }

    /**
     * Применяет эффект свечения к предмету.
     *
     * @param isGlow Флаг применения эффекта свечения
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder glow(boolean isGlow) {
        if (!isGlow) return this;
        ItemMeta meta = meta();
        if (meta == null) return this;
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        meta(meta);
        return hideEnchants();
    }

    /**
     * Добавляет пару ключ-значение постоянных данных к предмету.
     *
     * @param key   NamespacedKey для данных
     * @param type  Тип постоянных данных
     * @param value Значение для установки
     * @param <T>   Примитивный тип
     * @param <Z>   Сложный тип
     * @return Этот экземпляр ItemBuilder
     */
    public <T, Z> ItemBuilder namespacedKey(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        ItemMeta meta = meta();
        if (meta == null || key == null || type == null || value == null) return this;
        meta.getPersistentDataContainer().set(key, type, value);
        return meta(meta);
    }

    /**
     * Получает значение постоянных данных предмета.
     *
     * @param key  NamespacedKey для запроса
     * @param type Тип постоянных данных
     * @param <T>  Примитивный тип
     * @param <Z>  Сложный тип
     * @return Значение или null, если не найдено
     */
    public <T, Z> Z getNamespacedKey(NamespacedKey key, PersistentDataType<T, Z> type) {
        ItemMeta meta = meta();
        if (meta == null || key == null || type == null) return null;
        return meta.getPersistentDataContainer().get(key, type);
    }

    /**
     * Проверяет, есть ли ключ постоянных данных.
     *
     * @param key  NamespacedKey для проверки
     * @param type Тип постоянных данных
     * @param <T>  Примитивный тип
     * @param <Z>  Сложный тип
     * @return True, если ключ существует, иначе false
     */
    public <T, Z> boolean hasNamespacedKey(NamespacedKey key, PersistentDataType<T, Z> type) {
        ItemMeta meta = meta();
        if (meta == null || key == null || type == null) return false;
        return meta.getPersistentDataContainer().has(key, type);
    }

    /**
     * Удаляет ключ постоянных данных из предмета.
     *
     * @param key NamespacedKey для удаления
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder removeNamespacedKey(NamespacedKey key) {
        ItemMeta meta = meta();
        if (meta == null || key == null) return this;
        meta.getPersistentDataContainer().remove(key);
        return meta(meta);
    }

    /**
     * Сохраняет свойства предмета в секцию конфигурации.
     *
     * @param section Секция конфигурации для сохранения
     * @return Этот экземпляр ItemBuilder
     */
    public ItemBuilder save(ConfigurationSection section) {
        if (section == null) return this;

        section.set("material", item.getType().name());
        section.set("amount", item.getAmount());

        ItemMeta meta = meta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                section.set("display_name", name());
            }

            if (meta.hasLore()) {
                section.set("lore", lore());
            }

            if (meta.hasCustomModelData()) {
                section.set("model_data", meta.getCustomModelData());
            }

            Set<ItemFlag> flags = meta.getItemFlags();
            if (!flags.isEmpty()) {
                section.set("flags", flags.stream()
                        .map(ItemFlag::name)
                        .collect(Collectors.toList()));
            }

            if (!meta.getEnchants().isEmpty()) {
                List<String> enchantList = meta.getEnchants().entrySet().stream()
                        .map(entry -> entry.getKey().getKey().getKey() + ":" + entry.getValue())
                        .collect(Collectors.toList());
                section.set("enchantments", enchantList);
            }

            if (meta instanceof LeatherArmorMeta leatherMeta) {
                Color color = leatherMeta.getColor();
                section.set("color", String.format("%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue()));
            } else if (meta instanceof PotionMeta potionMeta && potionMeta.getColor() != null) {
                Color color = potionMeta.getColor();
                section.set("color", String.format("%d,%d,%d", color.getRed(), color.getGreen(), color.getBlue()));
            }

            section.set("glow", meta.hasEnchant(Enchantment.DURABILITY) &&
                                meta.getEnchantLevel(Enchantment.DURABILITY) == 1 &&
                                meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS));

            section.set("hide_attributes", meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES));
            section.set("hide_enchantments", meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS));
            section.set("unbreakable", meta.isUnbreakable());
        }

        return this;
    }

    /**
     * Создаёт и возвращает финальный ItemStack.
     *
     * @return Клонированный ItemStack со всеми модификациями
     */
    public ItemStack build() {
        return item.clone();
    }
}