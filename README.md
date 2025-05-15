[![](https://jitpack.io/v/7byLoper/SunCore.svg)](https://jitpack.io/#7byLoper/SunCore)
# SunCore — Утилиты для Spigot/Paper

Библиотека для удобной разработки плагинов Minecraft с упором на:  
✔ **Гибкие меню** (кнопки, декорации, навигация)  
✔ **Цветовые градиенты** (HEX, RGB, анимация текста)  
✔ **Умный ItemBuilder** (поддержка атрибутов, NBT, конфигов)  
✔ **Упрощенная отправка сообщений** (ActionBar, Title, Broadcast)  
✔ **Гибкая система команд** (подкоманды, автодополнение, проверка прав)  
✔ **Удобное управление конфигами** (загрузка, сохранение, автоматическое обновление)

---

## 📦 Подключение SunCore к вашему проекту

### Для Maven проектов

1. Добавьте репозиторий JitPack в `pom.xml`:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2. Добавьте зависимость SunCore:
```xml
<dependencies>
    <dependency>
        <groupId>com.github.7byloper</groupId>
        <artifactId>SunCore</artifactId>
        <version>1.0.3.1-BETA</version> <!-- Укажите актуальную версию -->
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Для Gradle проектов

1. Добавьте в `build.gradle`:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.7byloper:SunCore:1.0.3.1-BETA'
}
```

### Для Kotlin DSL (build.gradle.kts)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.7byloper:SunCore:1.0.3.1-BETA")
}
```

## 🔍 Где взять актуальную версию?
Последнюю версию можно найти на:
- [JitPack странице SunCore](https://jitpack.io/#7byloper/SunCore)
- [GitHub репозитории](https://github.com/7byloper/SunCore)

---

## 🛠 **ConfigManager - Управление конфигурациями**

### Базовое использование:
```java
@Getter
public class MyConfigManager extends ConfigManager {
    private String someValue;
    
    public MyConfigManager(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void loadConfigs() {
        // Регистрация конфигов
        addCustomConfig(new CustomConfig(plugin, "config.yml"));
        addCustomConfig(new CustomConfig(plugin, "messages.yml"));
    }

    @Override
    public void loadValues() {
        // Загрузка значений из конфигов
        CustomConfig config = getCustomConfig("config.yml");
        someValue = config.getConfig().getString("some.path");
    }
}
```

### Функционал:
- Автоматическая загрузка и сохранение конфигов
- Поддержка нескольких конфигурационных файлов
- Методы для массового обновления (`reloadAll()`) и сохранения (`saveAll()`)
- Логирование операций с конфигами

---

## 🕹️ **Система команд**

### Создание команды с подкомандами:
```java
public class MyCommand extends AdvancedSmartCommandExecutor {
    public MyCommand() {
        // Добавление подкоманд с правами и алиасами
        addSubCommand(new ReloadCommand(), 
            new Permission("myplugin.reload"), 
            "reload", "rl");
        
        addSubCommand(new HelpCommand(),
            null, // Без прав
            "help", "?");
    }

    @Override
    public String getDontPermissionMessage() {
        return "§cУ вас нет прав на эту команду!";
    }
}
```

### Реализация подкоманды:
```java
public class ReloadCommand implements SubCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        // Логика выполнения команды
        sender.sendMessage("§aКонфигурация перезагружена!");
    }

    @Override
    public List<String> onTabCompleter(CommandSender sender, String[] args) {
        // Автодополнение для команды
        return Arrays.asList("fast", "full");
    }
}
```

### Особенности:
- Автоматическая проверка прав доступа
- Встроенное автодополнение для подкоманд
- Гибкая система алиасов
- Разделение логики команд на отдельные классы

---

## 🖥️ **Меню и кнопки**
### `Menu` — базовый класс инвентарей:
```java
public class MyMenu extends Menu {
    @Override
    public String getTitle() { return "&6Меню"; }
    
    @Override
    public int getSize() { return 27; }
    
    @Override
    public void getItemsAndButtons() {
        addDecorItems(Material.GRAY_STAINED_GLASS_PANE, 0, 1, 2); // Декорации
        
        buttons.add(new Button(new ItemBuilder(Material.DIAMOND), 13) {
            @Override
            public void onClick(InventoryClickEvent e) {
                e.getPlayer().sendMessage("Клик по алмазу!");
            }
        });
        
        addReturnButton(22, new ItemBuilder(Material.ARROW)); // Кнопка "Назад" (Работает, если передать параметр `Menu parent`)
    }
}
```

### `Button` — обработка кликов:
```java
Button customBtn = new Button(new ItemBuilder(Material.EMERALD), 10, 11, 12) {
    @Override
    public void onClick(InventoryClickEvent e) {
        e.getWhoClicked().sendMessage("Вы нажали на кнопку!");
    }
};
```

---

## 🎨 **Цвета и текст**
### Форматирование:
```java
Colorize.parse("&aЗеленый &#FFD700градиент"); // Поддержка HEX
Colorize.generateGradientString("Текст", "#FF0000", "#00FF00"); // Генерация градиента
```

### Отправка сообщений:
```java
MessagesUtils.sendTitle(player, "&eЗаголовок", "&7Подзаголовок", 10, 20, 10);
MessagesUtils.sendActionBar(player, "&6Сообщение в ActionBar");
MessagesUtils.broadcast("&cГлобальное сообщение");
```

---

## 🛠 **ItemBuilder**
### Создание предметов:
```java
ItemStack item = new ItemBuilder(Material.DIAMOND_SWORD)
    .name("&6Легендарный меч")
    .lore(Arrays.asList("&7Древний артефакт", "&c+10 урона"))
    .enchantment(Enchantment.DAMAGE_ALL, 5)
    .glow(true)
    .model(1234)
    .build();
```

### Работа с конфигами:
```yaml
# config.yml
weapon:
  material: DIAMOND_SWORD
  display_name: "&6Меч из конфига"
  lore:
    - "&7Загружено из файла"
  glow: true
```
```java
ItemStack configItem = ItemBuilder.fromConfig(config.getConfigurationSection("weapon")).build();
```

---

## ☕ Требования к Java
**SunCore использует Java 16+**  
Для работы библиотеки требуется:
- **JDK 16** или новее для сборки
- **JRE 16+** на сервере для запуска

---

## 📦 Установка
1. Скачайте последнюю версию `SunCore.jar` из раздела [Releases](https://github.com/7byloper/SunCore/releases)
2. Поместите файл в папку `plugins` вашего сервера
3. Перезапустите сервер (`/reload` или полный рестарт)

---

## 📜 Лицензия
MIT License. Разработано для удобства и скорости разработки.