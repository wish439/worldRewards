package org.wishtoday.egar.worldRewards.Command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.egar.worldRewards.Fly.Counter;
import org.wishtoday.egar.worldRewards.Fly.FlyManager;
import org.wishtoday.egar.worldRewards.WorldRewards;

import java.util.List;
import java.util.Map;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.Commands.argument;

public class FlyCommand {
    @NotNull
    @SuppressWarnings("all")
    public static final NamespacedKey CAN_FLY = NamespacedKey.fromString("can_fly", WorldRewards.getInstance());
    @SuppressWarnings("UnstableApiUsage")
    public static void registerCommand(Commands command) {
        command.register(
                literal("fly")
                        .requires(sourceStack -> sourceStack.getSender().isOp() || sourceStack.getSender().hasPermission("worldrewards.fly"))
                        .then(argument(
                                        "player", ArgumentTypes.players()
                                ).requires(sourceStack -> sourceStack.getSender().isOp())
                                        .executes(
                                                context -> {
                                                    List<Player> players = context.getArgument("player"
                                                                    , PlayerSelectorArgumentResolver.class)
                                                            .resolve(context.getSource());
                                                    players.forEach(
                                                            player -> toggleActivationState(context
                                                                    , !player.getAllowFlight()
                                                                    , player)
                                                    );
                                                    return Command.SINGLE_SUCCESS;
                                                }
                                        )
                                        .then(
                                                argument("bool", BoolArgumentType.bool())
                                                        .executes(
                                                                context ->
                                                                        toggleActivationState(context
                                                                                ,context.getArgument("bool"
                                                                                        , Boolean.class)
                                                                                , context.getArgument("player"
                                                                                                , PlayerSelectorArgumentResolver.class)
                                                                                        .resolve(context.getSource()))
                                                        )
                                        )
                        )
                        .then(
                                argument("bool", BoolArgumentType.bool())
                                        .requires(sourceStack -> sourceStack.getSender().hasPermission("worldrewards.fly"))
                                        .executes(
                                                context -> {
                                                    if (!(context.getSource().getSender() instanceof Player player)) return 0;
                                                    toggleActivationState(context
                                                            , context.getArgument("bool", Boolean.class)
                                                            , player);
                                                    return Command.SINGLE_SUCCESS;
                                                }
                                        )
                        )
                        .build());
    }

    private static int toggleActivationState(CommandContext<CommandSourceStack> context,boolean b, List<Player> players) {
        players.forEach(player -> toggleActivationState(context,b, player));
        return Command.SINGLE_SUCCESS;
    }

    private static void toggleActivationState(CommandContext<CommandSourceStack> context,boolean b, Player player) {
        Boolean b1 = player.getPersistentDataContainer().get(FlyManager.CAN_FLY_ON_TODAY, PersistentDataType.BOOLEAN);
        if (Boolean.FALSE.equals(b1)) {
            player.sendMessage("您已经达到今日飞行时间上限,无法开启飞行");
            return;
        }
        player.setAllowFlight(b);
        player.setFlying(b);
        player.getPersistentDataContainer().set(CAN_FLY, PersistentDataType.BOOLEAN, b);
        if (b) FlyManager.getInstance().putCounter(player.getUniqueId(),new Counter());
        CommandSender sender = context.getSource().getSender();
        if (!(sender instanceof Player senderPlayer)) return;
        if (!senderPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(b ? Component.text("已为你启用飞行").color(TextColor.color(255, 255, 0))
                    : Component.text("已为你禁用飞行").color(TextColor.color(255, 0, 0)));
        }
        sender.sendMessage(b ? Component.text("已启用飞行").color(TextColor.color(255,255,0))
                : Component.text("已禁用飞行").color(TextColor.color(255, 0, 0)));
    }
}
