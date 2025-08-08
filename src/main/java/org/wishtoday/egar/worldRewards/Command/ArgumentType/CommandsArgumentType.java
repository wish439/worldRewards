package org.wishtoday.egar.worldRewards.Command.ArgumentType;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.egar.worldRewards.Events.CancelCommandEvent;

import java.util.concurrent.CompletableFuture;

public class CommandsArgumentType implements CustomArgumentType.Converted<String, String> {

    @Override
    public @NotNull String convert(@NotNull String nativeType) {
        return nativeType;
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        for (String command : CancelCommandEvent.cancelCommands.keySet()) {
            if (command.startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(command);
            }
        }
        return builder.buildFuture();
    }
}
