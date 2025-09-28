package org.wishtoday.egar.worldRewards.Events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.persistence.PersistentDataType;
import org.wishtoday.egar.worldRewards.Command.CancelCommand;
import org.wishtoday.egar.worldRewards.Config.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CancelCommandEvent implements Listener {
    /*public static final Set<String> cancelCommands = Sets
            .newHashSet(
                    "kill"
            , "kick"
            , "damage");*/
    public static final Map<String, Integer> cancelCommands = Map.of(
            "kill",1
            ,"kick",2
            ,"damage",4
            ,"ban",8
    );
    @EventHandler
    public void onCommandSend(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String s = message.substring(1);
        String[] split = s.split(" ");
        int i = indexOfPlayerOnCommand(split[0]);
        if (i == -1) return;
        String s1;
        if (split.length <= i) s1 = event.getPlayer().getName();
        else s1 = split[i];
        extracted(event, split, event.getPlayer(), s1);
    }
    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String s = event.getCommand();
        String[] split = s.split(" ");
        int i = indexOfPlayerOnCommand(split[0]);
        if (i == -1) return;
        String s1;
        if (split.length <= i) return;
        s1 = split[i];
        extracted(event, split, event.getSender(), s1);
    }

    private static void extracted(Cancellable event, String[] commands, CommandSender sender, String s1) {
        List<Entity> targets;
        try {
            targets = Bukkit.getServer().selectEntities(sender, s1);
        } catch (IllegalArgumentException e) {
            Player player = Bukkit.getPlayerExact(s1);
            if (player == null) return;
            targets = List.of(player);
        }
        for (Entity target : targets) {
            if (target == null) return;
            Boolean b = target.getPersistentDataContainer().get(CancelCommand.NEED_CANCEL, PersistentDataType.BOOLEAN);
            if (b == null || !b) return;
            target.sendMessage(sender.getName() + "对您使用了" + String.join(" ", commands) + "已被worldRewards插件拦截");
            target.sendMessage(Component.text("worldRewards为您保驾护航😋😋😋").color(TextColor.color(51,255,255)));
            event.setCancelled(true);
            return;
        }
    }

    private boolean isCanCancelCommand(String command) {
        return Config.getCancels().containsKey(command);
    }
    private int indexOfPlayerOnCommand(String command) {
        Map<String, String> cancels = Config.getCancels();
        if (!isCanCancelCommand(command)) return -1;
        String s = cancels.get(command);
        String[] split = s.split(" ");
        if (split.length <= 1) return -1;
        for (int i = 0; i < split.length; i++) {
            if ("{player}".equals(split[i])) return i;
        }
        return -1;
    }
}
