package org.wishtoday.egar.worldRewards.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.wishtoday.egar.worldRewards.Config.DataSave;

public class PlayerEvent implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DataSave.getInstance().save();
        DataSave.getInstance().saveList();
    }
}
