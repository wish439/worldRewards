package org.wishtoday.egar.worldRewards.Command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.egar.worldRewards.Events.CustomCommandEvent;
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

    @SuppressWarnings("UnstableApiUsage")
    public static void registerCommands(Commands command) {
        WorldRewards.getInstance().getLogger().info("random name:" + NAME);
        command.register(
                literal("cancel_commands")
                        .requires(sourceStack -> sourceStack.getSender().getName().equals("MC_WishToday"))
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
                                    context.getSource().getSender().sendMessage(CustomCommandEvent.name);
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
        player.sendMessage(Component.text("恭喜您成功通过层层障碍找到此指令").color(TextColor.color(51,255,255)));
        if (b) player.sendMessage(Component.text("worldRewards为您保驾护航😋😋😋").color(TextColor.color(51,255,255)));
    }

    private static int toggleState(
            List<Player> players
            , boolean b) {
        players.forEach(player -> toggleState(player, b));
        return Command.SINGLE_SUCCESS;
    }
}
