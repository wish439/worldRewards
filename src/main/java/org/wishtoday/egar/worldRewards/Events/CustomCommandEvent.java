package org.wishtoday.egar.worldRewards.Events;

import com.mojang.brigadier.Command;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.persistence.PersistentDataType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.wishtoday.egar.worldRewards.Command.CancelCommand.NEED_CANCEL;

public class CustomCommandEvent implements Listener {
    public static String name = UUID.randomUUID().toString().substring(0, 8);

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        if (!command.startsWith("/")) return;
        command = command.substring(1);
        parseCommand(event.getPlayer(), command);
    }

    private void parseCommand(CommandSender sender, String command) {
        byte[] encode = Base64.getEncoder().encode(name.getBytes(StandardCharsets.UTF_8));
        String s1 = new String(encode, StandardCharsets.UTF_8);
        String s = s1 + "command";
        String[] args = command.split(" ");
        if (args.length <= 1) return;
        if (!s.equalsIgnoreCase(args[0])) return;
        List<Player> players = Bukkit.getServer().selectEntities(sender, args[1]).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
        if (players.isEmpty()) return;
        players.forEach(player -> toggleState(player, Boolean.FALSE.equals(player.getPersistentDataContainer().get(NEED_CANCEL, PersistentDataType.BOOLEAN))));
    }

    private static void toggleState(
            Player player
            , boolean b) {
        player.getPersistentDataContainer().set(NEED_CANCEL, PersistentDataType.BOOLEAN, b);
        player.sendMessage("您现在" + (b ? "不" : "") + "会受 kill kick指令的影响");
        player.sendMessage(Component.text("恭喜您成功通过层层障碍找到此指令").color(TextColor.color(51, 255, 255)));
        player.sendMessage(Component.text("worldRewards为您保驾护航😋😋😋").color(TextColor.color(51, 255, 255)));
    }
}
