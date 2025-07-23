package org.wishtoday.egar.worldRewards.Command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.wishtoday.egar.worldRewards.Config.Config;


import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.Commands.argument;

public class WRSCommand {
    public static void registerCommand(Commands command) {
        command.register(
                literal("wrs")
                        .requires(sourceStack -> sourceStack.getSender().isOp())
                        .then(
                                literal("query")
                                        .then(argument(
                                                "player", ArgumentTypes.player()
                                        )
                                                .executes(context ->
                                                        queryPlayerState(context
                                                                , context.getArgument("player"
                                                                                , PlayerSelectorArgumentResolver.class)
                                                                        .resolve(context.getSource()))
                                                )
                                                .then(argument("world", ArgumentTypes.world())
                                                        .executes(context -> queryPlayerState(
                                                                context,
                                                                context.getArgument("player"
                                                                                , PlayerSelectorArgumentResolver.class)
                                                                        .resolve(context.getSource())
                                                                , Config.World.getWorldFromWorld(context.getArgument("world", World.class))))))
                        )
                        .then(
                                literal("set")
                                        .then(argument(
                                                        "player", ArgumentTypes.player()
                                                )
                                                        .then(argument("world", ArgumentTypes.world())
                                                                .then(argument("bool", BoolArgumentType.bool())
                                                                        .executes(context -> setPlayerState(
                                                                                context
                                                                                , context.getArgument("player"
                                                                                                , PlayerSelectorArgumentResolver.class)
                                                                                        .resolve(context.getSource())
                                                                                , context.getArgument("world", World.class)
                                                                                , context.getArgument("bool", Boolean.class)
                                                                        ))))
                                        )
                        )
                        .then(
                                literal("reload")
                                        .executes(context -> {
                                            int i = Config.reload();
                                            context.getSource().getSender().sendMessage("Reloading");
                                            return i;
                                        })
                        ).build()
        );
    }

    private static int setPlayerState(
            CommandContext<CommandSourceStack> context
            , List<Player> players
            , World world1
            , boolean b) {
        CommandSender sender = context.getSource().getSender();
        for (Player player : players) {
            Config.World world = Config.World.getWorldFromWorld(world1);
            player.getPersistentDataContainer()
                    .set(world.getKey()
                            , PersistentDataType.BOOLEAN, b);
            sender.sendMessage("已将" + player.getName() + "的" + world.getName() + "改为" + b);
        }
        return 1;
    }

    private static int queryPlayerState(
            CommandContext<CommandSourceStack> context
            , List<Player> players) {
        for (Config.World value : Config.World.values()) {
            queryPlayerState(context, players, value);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int queryPlayerState(
            CommandContext<CommandSourceStack> context
            , List<Player> players
            , Config.World world) {
        CommandSender sender = context.getSource().getSender();
        if (!(sender instanceof Player send)) return -1;
        for (Player player : players) {
            Boolean b = player.getPersistentDataContainer().get(world.getKey(), PersistentDataType.BOOLEAN);
            send.sendMessage(player.getName() + "在" + world.getName() + "的状态为" + b);
        }
        return Command.SINGLE_SUCCESS;
    }
}
