package org.wishtoday.egar.worldRewards.Config;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.egar.worldRewards.Events.ChangeWorldEvent;
import org.wishtoday.egar.worldRewards.WorldRewards;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Config {
    public static int reload() {
        File file = new File(WorldRewards.getInstance().getDataFolder()
                , "config.yml");
        try {
            WorldRewards.getInstance().getConfig().load(file);
        } catch (IOException | InvalidConfigurationException e) {
            WorldRewards.getInstance().getLogger()
                    .warning("config load fail:" + e.getMessage());
            return -1;
        }
        WorldRewards.getInstance().getLogger().info("config reloaded");
        return 1;
    }
    public static List<ItemStack> getItems(World world) {
        String name = world.getName();
        List<ItemStack> items = new ArrayList<>();
        FileConfiguration config = WorldRewards.getInstance().getConfig();
        ConfigurationSection overload = config.getConfigurationSection(name);
        if (overload == null) return items;
        Set<String> keys = overload.getKeys(false);
        //item,item2
        for (String s : keys) {
            if ("seconds".equalsIgnoreCase(s)) continue;
            String path = name + "." + s + ".";
            Material material = Material.getMaterial(config.getString(path + "material").toUpperCase());
            if (material == null) continue;
            System.out.println(material);
            int count = config.getInt(path + "count");
            Component disname = config.getString(path + "display_name") == null ? null : Component.text(config.getString(path + "display_name"));
            ItemStack stack = new ItemStack(material, count);
            if (disname != null) {
                ItemMeta meta = stack.getItemMeta();
                meta.displayName(disname);
                stack.setItemMeta(meta);
            }
            items.add(stack);
        }
        return items;
    }
    public static int getSeconds(World world) {
        return WorldRewards.getInstance().getConfig().getInt(world.getName() + ".seconds");
    }
    public static int getGoldenAppleStrength() {
        return WorldRewards.getInstance().getConfig().getInt("weaken_enchanted_golden_apple");
    }
    public enum World {
        OVERLOAD("overload", ChangeWorldEvent.OVERLOAD
                , org.bukkit.World.Environment.NORMAL),
        NETHER("nether", ChangeWorldEvent.THE_NETHER
                , org.bukkit.World.Environment.NETHER),
        END("end", ChangeWorldEvent.THE_END
                , org.bukkit.World.Environment.THE_END),;
        private String name;
        private NamespacedKey key;
        private org.bukkit.World.Environment environment;
        private static Map<String, World> worlds = new HashMap<>();
        private static EnumMap<org.bukkit.World.Environment, World> enumMap = new EnumMap<>(org.bukkit.World.Environment.class);
        World(String name, NamespacedKey key, org.bukkit.World.Environment environment) {
            this.name = name;
            this.key = key;
            this.environment = environment;
        }
        static {
            for (World value : values()) {
                worlds.put(value.getName(), value);
                enumMap.put(value.environment, value);
            }
        }

        public String getName() {
            return name;
        }
        public NamespacedKey getKey() {
            return key;
        }
        @NotNull
        public static World getWorldFromWorld(org.bukkit.World world) {
            if (!enumMap.containsKey(world.getEnvironment())) return World.OVERLOAD;
            return enumMap.get(world.getEnvironment());
        }
        @NotNull
        public static World getWorldFromName(String name) {
            if (!worlds.containsKey(name)) return World.OVERLOAD;
            return worlds.get(name);
        }
    }
}
