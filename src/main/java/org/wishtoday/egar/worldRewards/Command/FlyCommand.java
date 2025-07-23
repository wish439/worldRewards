package org.wishtoday.egar.worldRewards.Command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.Commands.argument;

public class FlyCommand {
    public static void registerCommand(Commands command) {
        command.register(
                literal("fly")
                        .requires(sourceStack -> sourceStack.getSender().isOp())
                        .then(argument(
                                        "player", ArgumentTypes.player()
                                )
                                        .then(
                                                argument("bool", BoolArgumentType.bool())
                                                        .executes(
                                                                context -> {
                                                                    List<Player> players = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource());
                                                                    Boolean bool = context.getArgument("bool", Boolean.class);
                                                                    players.forEach(player -> {
                                                                        player.setAllowFlight(bool);
                                                                        player.setFlying(bool);
                                                                    });
                                                                    return Command.SINGLE_SUCCESS;
                                                                }
                                                        )
                                        )
                        ).build());
    }
}
