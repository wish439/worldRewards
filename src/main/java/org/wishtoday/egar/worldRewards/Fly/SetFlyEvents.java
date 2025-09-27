package org.wishtoday.egar.worldRewards.Fly;


import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.egar.worldRewards.Command.FlyCommand;
import org.wishtoday.egar.worldRewards.WorldRewards;

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
    @EventHandler
    public void onPlayerChangeMode(PlayerGameModeChangeEvent event) {
        GameMode mode = event.getNewGameMode();
        if (mode == GameMode.SPECTATOR || mode == GameMode.CREATIVE) return;
        testAndSetFly(event);
    }
    @EventHandler
    public void resetFlyTime(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!FlyManager.getInstance().getNeedReset().contains(player.getUniqueId())) return;
        FlyManager.getInstance().resetFlyStateToPlayer(player);
        FlyManager.getInstance().getNeedReset().remove(player.getUniqueId());
        WorldRewards.getInstance().getLogger().info(player.getUniqueId() + "{}Fly reset");
    }
    private void testAndSetFly(@NotNull Player player) {
        Boolean b1 = player.getPersistentDataContainer().get(FlyManager.CAN_FLY_ON_TODAY, PersistentDataType.BOOLEAN);
        if (Boolean.FALSE.equals(b1)) return;
        Boolean b = player.getPersistentDataContainer().get(FlyCommand.CAN_FLY, PersistentDataType.BOOLEAN);
        if (b == null || !b) {
            player.setAllowFlight(false);
            player.getPersistentDataContainer().set(FlyCommand.CAN_FLY, PersistentDataType.BOOLEAN, false);
            return;
        }
        player.setAllowFlight(true);
        //player.setFlying(false);
    }
    private void testAndSetFly(PlayerEvent event) {
        Player player = event.getPlayer();
        testAndSetFly(player);
    }
}
