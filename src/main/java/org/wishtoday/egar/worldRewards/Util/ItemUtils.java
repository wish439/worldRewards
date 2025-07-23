package org.wishtoday.egar.worldRewards.Util;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ItemUtils {
    public static void givePlayerItem(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> map = player.getInventory().addItem(item);
        if (map.isEmpty()) return;
        List<ItemStack> items = new ArrayList<>(map.values());
        Location location = player.getEyeLocation().clone();
        location.setY(location.getY() - 0.30000001192092896D);
        for (ItemStack stack : items) {
            player.getWorld().dropItem(location, stack,  drop -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                float pitchRad = player.getPitch();
                float yawRad = player.getYaw();

                double sinPitch = Math.sin(pitchRad);
                double cosPitch = Math.cos(pitchRad);
                double sinYaw = Math.sin(yawRad);
                double cosYaw = Math.cos(yawRad);

                float randomAngle = random.nextFloat() * 6.2831855F; // 2π
                float randomMagnitude = 0.02F * random.nextFloat();

                double x = (cosYaw * cosPitch * 0.3F) + Math.sin(randomAngle) * randomMagnitude;
                double y = -sinPitch * 0.3F + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F;
                double z = (-sinYaw * cosPitch * 0.3F) + Math.cos(randomAngle) * randomMagnitude;

                drop.setVelocity(new Vector(x,y,z));
                drop.setThrower(player.getUniqueId());
                drop.setPickupDelay(40);
            });
        }
    }
    public static void givePlayerItems(Player player, List<ItemStack> items) {
        for (ItemStack stack : items) {
            givePlayerItem(player, stack);
        }
    }
}
