package ru.loper.suncore.utils.items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import ru.loper.suncore.SunCore;

import java.lang.reflect.Field;
import java.util.UUID;


public class SkullUtils {

    public static ItemStack getCustomSkull(String base64) {
        base64 = base64.replace("basehead-", "");
        ItemStack head = SunCore.getInstance().getHead();
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        if (headMeta != null) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", base64));

            try {
                Field profileField = headMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(headMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                SunCore.printStacktrace("Something went horribly wrong trying to create basehead URL", e);
            }

            head.setItemMeta(headMeta);
        }

        return head;
    }
}
