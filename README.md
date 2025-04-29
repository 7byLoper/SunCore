[![](https://jitpack.io/v/7byLoper/SunCore.svg)](https://jitpack.io/#7byLoper/SunCore)
# SunCore — Утилиты для Spigot/Paper

Библиотека для удобной разработки плагинов Minecraft с упором на:  
✔ **Гибкие меню** (кнопки, декорации, навигация)  
✔ **Цветовые градиенты** (HEX, RGB, анимация текста)  
✔ **Умный ItemBuilder** (поддержка атрибутов, NBT, конфигов)  
✔ **Упрощенная отправка сообщений** (ActionBar, Title, Broadcast)  

Вот готовый раздел для вашего README.md с инструкциями по подключению SunCore как зависимости:

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
        <version>1.0.0-RELEASE</version> <!-- Укажите актуальную версию -->
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
    compileOnly 'com.github.7byloper:SunCore:v1.0.0'
}
```

### Для Kotlin DSL (build.gradle.kts)

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.7byloper:SunCore:v1.0.0")
}
```

## 🔍 Где взять актуальную версию?
Последнюю версию можно найти на:
- [JitPack странице SunCore](https://jitpack.io/#7byloper/SunCore)
- [GitHub репозитории](https://github.com/7byloper/SunCore)

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
        
        addReturnButton(22, new ItemBuilder(Material.ARROW)); // Кнопка "Назад"
    }
}
```

### `Button` — обработка кликов:  
```java
Button customBtn = new Button(new ItemBuilder(Material.EMERALD), 10, 11, 12) {
    @Override
    public void onClick(InventoryClickEvent e) {
        // Логика клика
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
