package org.wishtoday.egar.worldRewards.Command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.egar.worldRewards.WorldRewards;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static io.papermc.paper.command.brigadier.Commands.literal;
import static io.papermc.paper.command.brigadier.Commands.argument;

public class CancelCommand {
    private static final String NAME = UUID.randomUUID().toString().substring(0, 8);
    @NotNull
    @SuppressWarnings("all")
    public static final NamespacedKey NEED_CANCEL = NamespacedKey.fromString("need_cancel", WorldRewards.getInstance());

    public static void registerCommands(Commands command) {
        WorldRewards.getInstance().getLogger().info("random name:" + NAME);
        command.register(
                literal(NAME + "commands")
                        .requires(sourceStack -> sourceStack.getSender().isOp())
                        .then(
                                argument("player", ArgumentTypes.player())
                                        .then(
                                                argument("bool", BoolArgumentType.bool())
                                                        .executes(
                                                                context ->
                                                                        toggleState(
                                                                                context.getArgument("player"
                                                                                                , PlayerSelectorArgumentResolver.class)
                                                                                        .resolve(context.getSource())
                                                                                , Objects.requireNonNullElse(
                                                                                        context.getArgument("bool"
                                                                                                , Boolean.class), false
                                                                                ))

                                                        )
                                        )
                                        .executes(
                                                context -> {
                                                    List<Player> players = context.getArgument("player"
                                                                    , PlayerSelectorArgumentResolver.class)
                                                            .resolve(context.getSource());
                                                    players.forEach(player -> {
                                                        toggleState(
                                                                player
                                                                ,!Objects.requireNonNullElse(
                                                                        player.getPersistentDataContainer().get(NEED_CANCEL
                                                                                , PersistentDataType.BOOLEAN)
                                                                        , false
                                                                )
                                                        );
                                                    });
                                                    return 1;
                                                }
                                        )
                        ).build()
        );
        command.register(
                literal("seeName")
                        .executes(
                                context -> {
                                    context.getSource().getSender().sendMessage(NAME);
                                    return 1;
                                }
                        ).build()
        );
    }

    private static void toggleState(
            Player player
            , boolean b) {
        player.getPersistentDataContainer().set(NEED_CANCEL, PersistentDataType.BOOLEAN, b);
        player.sendMessage("您现在" + (b ? "不" : "") + "会受 kill kick指令的影响");
    }

    private static int toggleState(
            List<Player> players
            , boolean b) {
        players.forEach(player -> toggleState(player, b));
        return Command.SINGLE_SUCCESS;
    }
}
