package org.wishtoday.egar.worldRewards;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.wishtoday.egar.worldRewards.Command.WRSCommand;
import org.wishtoday.egar.worldRewards.Events.ChangeWorldEvent;
import org.wishtoday.egar.worldRewards.Events.PlayerEatEvent;

public final class WorldRewards extends JavaPlugin {
    private static WorldRewards plugin;
    @Override
    public void onEnable() {
        plugin = this;
        this.getServer().getPluginManager().registerEvents(new ChangeWorldEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerEatEvent(), this);
        saveDefaultConfig();
        registerCommands();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
    public static WorldRewards getInstance() {
        return plugin;
    }
    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();
            WRSCommand.registerCommand(registrar);
        });
    }
}
