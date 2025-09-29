package org.wishtoday.egar.worldRewards.Events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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

import java.util.*;

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
        String string = getStringBeforePlayer(s);
        int i = indexOfPlayerOnCommand(string);
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
        String string = getStringBeforePlayer(s);
        int i = indexOfPlayerOnCommand(string);
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
            if (target == null) continue;
            Boolean b = target.getPersistentDataContainer().get(CancelCommand.NEED_CANCEL, PersistentDataType.BOOLEAN);
            if (b == null || !b) continue;
            target.sendMessage(sender.getName() + "对您使用了" + String.join(" ", commands) + "已被worldRewards插件拦截");
            target.sendMessage(Component.text("worldRewards为您保驾护航😋😋😋").color(TextColor.color(51,255,255)));
            event.setCancelled(true);
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
    private String getStringBeforePlayer(String fullCommand) {
        int i = indexOfPlayer(fullCommand);
        if (i == -1) return fullCommand;
        String[] split = fullCommand.split(" ");
        int index;
        if (split.length > i) index = fullCommand.indexOf(split[i]);
        else index = fullCommand.length();
        String substring = fullCommand.substring(0, index);
        return substring.trim();
    }
    private int indexOfPlayer(String s) {
        Map<String, String> cancels = Config.getCancels();
        Set<String> keySet = cancels.keySet();
        int j = -1;
        for (String string : keySet) {
            if (s.startsWith(string)) {
                j = string.split(" ").length;
                break;
            }
        }
        return j;
    }
}
