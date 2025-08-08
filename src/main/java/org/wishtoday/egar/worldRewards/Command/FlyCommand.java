package org.wishtoday.egar.worldRewards.Command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.egar.worldRewards.WorldRewards;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.Commands.argument;

public class FlyCommand {
    @NotNull
    @SuppressWarnings("all")
    public static final NamespacedKey CAN_FLY = NamespacedKey.fromString("can_fly", WorldRewards.getInstance());
    public static void registerCommand(Commands command) {
        command.register(
                literal("fly")
                        .requires(sourceStack -> sourceStack.getSender().isOp())
                        .then(argument(
                                        "player", ArgumentTypes.players()
                                )
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
                        ).build());
    }

    private static int toggleActivationState(CommandContext<CommandSourceStack> context,boolean b, List<Player> players) {
        players.forEach(player -> toggleActivationState(context,b, player));
        return Command.SINGLE_SUCCESS;
    }

    private static void toggleActivationState(CommandContext<CommandSourceStack> context,boolean b, Player player) {
        player.setAllowFlight(b);
        player.setFlying(b);
        player.getPersistentDataContainer().set(CAN_FLY, PersistentDataType.BOOLEAN, b);
        CommandSender sender = context.getSource().getSender();
        if (!(sender instanceof Player senderPlayer)) return;
        if (senderPlayer.getUniqueId() != player.getUniqueId()) {
            player.sendMessage(b ? Component.text("已为你启用飞行").color(TextColor.color(255, 255, 0))
                    : Component.text("已为你禁用飞行").color(TextColor.color(255, 0, 0)));
        }
        sender.sendMessage(b ? Component.text("已启用飞行").color(TextColor.color(255,255,0))
                : Component.text("已禁用飞行").color(TextColor.color(255, 0, 0)));
    }
}
