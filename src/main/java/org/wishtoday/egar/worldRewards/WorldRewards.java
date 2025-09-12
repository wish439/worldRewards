package org.wishtoday.egar.worldRewards;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.wishtoday.egar.worldRewards.Command.CancelCommand;
import org.wishtoday.egar.worldRewards.Command.FlyCommand;
import org.wishtoday.egar.worldRewards.Command.WRSCommand;
import org.wishtoday.egar.worldRewards.Config.DataSave;
import org.wishtoday.egar.worldRewards.Events.CancelCommandEvent;
import org.wishtoday.egar.worldRewards.Events.ChangeWorldEvent;
import org.wishtoday.egar.worldRewards.Events.PlayerEatEvent;
import org.wishtoday.egar.worldRewards.Events.PlayerEvent;
import org.wishtoday.egar.worldRewards.Fly.FlyManager;
import org.wishtoday.egar.worldRewards.Fly.FlyManagerCommand;
import org.wishtoday.egar.worldRewards.Fly.SetFlyEvents;

public final class WorldRewards extends JavaPlugin {
    private static WorldRewards plugin;
    @Override
    public void onEnable() {
        plugin = this;
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new ChangeWorldEvent(), this);
        manager.registerEvents(new PlayerEatEvent(), this);
        manager.registerEvents(new SetFlyEvents(),this);
        manager.registerEvents(new CancelCommandEvent(), this);
        manager.registerEvents(new FlyManager(), this);
        manager.registerEvents(new PlayerEvent(), this);
        saveDefaultConfig();
        registerCommands();
        FlyManager.getInstance().start();
        DataSave.getInstance().load();
        DataSave.getInstance().loadList();
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
            FlyCommand.registerCommand(registrar);
            CancelCommand.registerCommands(registrar);
            FlyManagerCommand.registerCommand(registrar);
        });
    }
}
