package org.wishtoday.egar.worldRewards.Fly;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;
import static org.wishtoday.egar.worldRewards.Fly.FlyManager.CAN_FLY_ON_TODAY;

public class FlyManagerCommand {
    @SuppressWarnings("UnstableApiUsage")
    public static void registerCommand(Commands command) {
        command.register(
                literal("flymanager")
                        .requires(sourceStack -> sourceStack.getSender().isOp())
                        .then(literal("getcurrsec")
                                .then(argument("player", ArgumentTypes.player()).executes(context -> {
                                    Player player = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst();
                                    context.getSource().getSender().sendMessage(player.getName() + ":" + FlyManager.getInstance().getCounter(player.getUniqueId()).getCurrentSec() + "秒");
                                    return 1;
                                }))
                        )
                        .then(literal("getanticipationsec")
                                .then(argument("player", ArgumentTypes.player())
                                        .executes(context -> {
                                            Player player = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst();
                                            context.getSource().getSender().sendMessage(player.getName() + ":" + FlyManager.getInstance().getCounter(player.getUniqueId()).getAnticipationSec() + "秒");
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("setanticipationsec")
                                .then(argument("player", ArgumentTypes.player())
                                        .then(argument("newsec", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    Player player = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst();
                                                    FlyManager.getInstance().getCounter(player.getUniqueId()).setAnticipationSec(IntegerArgumentType.getInteger(context, "newsec"));
                                                    context.getSource().getSender().sendMessage(player.getName() + "已被设置为" + IntegerArgumentType.getInteger(context, "newsec") + "秒");
                                                    return 1;
                                                }))
                                )
                        )
                        .then(literal("setcurrsec")
                                .then(argument("player", ArgumentTypes.player())
                                        .then(argument("newsec", IntegerArgumentType.integer())
                                                .executes(context -> {
                                                    Player player = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst();
                                                    FlyManager.getInstance().getCounter(player.getUniqueId()).setCurrentSec(IntegerArgumentType.getInteger(context, "newsec"));
                                                    context.getSource().getSender().sendMessage(player.getName() + "已被设置为" + IntegerArgumentType.getInteger(context, "newsec") + "秒");
                                                    return 1;
                                                }))
                                )
                        )
                        .then(literal("canfly")
                                .then(argument("player", ArgumentTypes.player())
                                        .executes(context -> {
                                            Player player = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst();
                                            context.getSource().getSender().sendMessage(player.getName() + (player.getAllowFlight() ? "可以" : "不可以") + "飞行");
                                            return 1;
                                        }))
                        )
                        .then(literal("removestate")
                                .then(argument("player", ArgumentTypes.player())
                                        .executes(context -> {
                                            Player player = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst();
                                            player.getPersistentDataContainer().set(CAN_FLY_ON_TODAY, PersistentDataType.BOOLEAN, true);
                                            context.getSource().getSender().sendMessage(player.getName() + "可以" + "飞行了");
                                            return 1;
                                        }))
                        )
                        .build());
    }
}
