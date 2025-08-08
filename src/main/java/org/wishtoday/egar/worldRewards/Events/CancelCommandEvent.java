package org.wishtoday.egar.worldRewards.Events;

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

import java.util.List;
import java.util.Map;

public class CancelCommandEvent implements Listener {
    /*public static final Set<String> cancelCommands = Sets
            .newHashSet(
                    "kill"
            , "kick"
            , "damage");*/
    public static final Map<String, Integer> cancelCommands = Map.of(
            "kill",1
            ,"kick",2
            ,"damage",3
    );
    @EventHandler
    public void onCommandSend(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        System.out.println(message);
        String s = message.substring(1);
        String[] split = s.split(" ");
        if (!isCanCancelCommand(split[0])) return;
        String s1;
        if (split.length == 1) s1 = event.getPlayer().getName();
        else s1 = split[1];
        extracted(event, split, event.getPlayer(), s1);
    }
    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String s = event.getCommand();
        System.out.println(s);
        String[] split = s.split(" ");
        if (!isCanCancelCommand(split[0])) return;
        String s1;
        if (split.length == 1) return;
        s1 = split[1];
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
            event.setCancelled(true);
            return;
        }
    }

    private boolean isCanCancelCommand(String command) {
        return cancelCommands.containsKey(command);
    }
}
