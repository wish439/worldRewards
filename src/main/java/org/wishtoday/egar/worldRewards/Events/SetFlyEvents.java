package org.wishtoday.egar.worldRewards.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataType;
import org.wishtoday.egar.worldRewards.Command.FlyCommand;

public class SetFlyEvents implements Listener {
    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        testAndSetFly(event);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        testAndSetFly(event);
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        testAndSetFly(event);
    }
    private void testAndSetFly(Player player) {
        Boolean b = player.getPersistentDataContainer().get(FlyCommand.CAN_FLY, PersistentDataType.BOOLEAN);
        if (b == null || !b) return;
        player.setAllowFlight(true);
        //player.setFlying(false);
    }
    private void testAndSetFly(PlayerEvent event) {
        Player player = event.getPlayer();
        testAndSetFly(player);
    }
}
