package org.wishtoday.egar.worldRewards.Events;

import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.wishtoday.egar.worldRewards.Config.Config;
import org.wishtoday.egar.worldRewards.Util.ItemUtils;
import org.wishtoday.egar.worldRewards.WorldRewards;

import java.util.Map;
import java.util.Objects;

public class ChangeWorldEvent implements Listener {
    public static final NamespacedKey OVERLOAD = new NamespacedKey(WorldRewards.getInstance(), "overload");
    public static final NamespacedKey THE_NETHER = new NamespacedKey(WorldRewards.getInstance(), "the_nether");
    public static final NamespacedKey THE_END = new NamespacedKey(WorldRewards.getInstance(), "the_end");
    private static final Map<World.Environment, NamespacedKey> envAndKey = Map.of(
            World.Environment.NORMAL,OVERLOAD,
            World.Environment.THE_END,THE_END,
            World.Environment.NETHER,THE_NETHER
    );
    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        World toWorld = player.getWorld();
        PersistentDataContainer container = player.getPersistentDataContainer();
        World.Environment environment = toWorld.getEnvironment();
        int tick = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int second = tick / 20;
        System.out.println(environment);
        System.out.println(second);
        System.out.println(Config.getSeconds(Config.World.OVERLOAD));
        System.out.println(Config.getSeconds(Config.World.OVERLOAD) > second);
        if (environment == World.Environment.NORMAL
                && Config.getSeconds(Config.World.OVERLOAD) > second) return;
        if (environment == World.Environment.THE_END
                && Config.getSeconds(Config.World.END) > second) return;
        if (environment == World.Environment.NETHER
                && Config.getSeconds(Config.World.NETHER) > second) return;
        if (!envAndKey.containsKey(environment)) return;
        NamespacedKey key = envAndKey.get(environment);
        if (!container.has(key, PersistentDataType.BOOLEAN)
        || Objects.equals(container.get(key, PersistentDataType.BOOLEAN)
                , false)) {
            if (key.equals(OVERLOAD)) {
                ItemUtils.givePlayerItems(player,Config.getItems(Config.World.OVERLOAD));
            }
            if (key.equals(THE_END)) {
                ItemUtils.givePlayerItems(player,Config.getItems(Config.World.END));
            }
            if (key.equals(THE_NETHER)) {
                ItemUtils.givePlayerItems(player,Config.getItems(Config.World.NETHER));
            }
            container.set(key, PersistentDataType.BOOLEAN, true);
        }
    }
}
