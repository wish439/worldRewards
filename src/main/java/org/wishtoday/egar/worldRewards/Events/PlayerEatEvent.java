package org.wishtoday.egar.worldRewards.Events;

import com.google.common.collect.Lists;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.wishtoday.egar.worldRewards.Config.Config;

import java.util.Arrays;
import java.util.List;

public class PlayerEatEvent implements Listener {
    List<PotionEffectType> effectTypes = Arrays.asList(
            PotionEffectType.ABSORPTION,
            PotionEffectType.REGENERATION,
            PotionEffectType.FIRE_RESISTANCE,
            PotionEffectType.RESISTANCE
    );
    List<PotionEffect> effects = Arrays.asList(
            //生命恢复1级5秒
            new PotionEffect(PotionEffectType.REGENERATION,100,0),
            //抗性1级75秒
            new PotionEffect(PotionEffectType.RESISTANCE, 1500, 0),
            //火焰抗性1级75秒
            new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1500, 0),
            //伤害吸收3级60秒
            new PotionEffect(PotionEffectType.ABSORPTION, 1200, 2)
    );

    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        if (!Config.isWeakenGoldenApple()) return;
        ItemStack item = event.getItem();
        if (item.getType() != Material.ENCHANTED_GOLDEN_APPLE) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        effectTypes.forEach(player::removePotionEffect);
        player.addPotionEffects(effects);
        player.setSaturation(player.getSaturation() + 2);
        player.setFoodLevel(player.getFoodLevel() + 9);
        if (player.getGameMode() == GameMode.CREATIVE) return;
        EquipmentSlot hand = event.getHand();
        ItemStack stack = hand == EquipmentSlot.HAND
                ? player.getInventory().getItemInMainHand()
                : player.getInventory().getItemInOffHand();
        stack.setAmount(stack.getAmount() - 1);
    }
}
