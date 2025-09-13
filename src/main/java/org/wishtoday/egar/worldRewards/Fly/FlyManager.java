package org.wishtoday.egar.worldRewards.Fly;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.wishtoday.egar.worldRewards.WorldRewards;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

public class FlyManager implements Listener {
    @SuppressWarnings("DataFlowIssue")
    @NotNull
    public static final NamespacedKey CAN_FLY_ON_TODAY = NamespacedKey.fromString("can_fly_on_today", WorldRewards.getInstance());
    private static FlyManager instance = new FlyManager();
    private Map<UUID, Counter> counters = new HashMap<>();
    private List<UUID> needReset = Lists.newArrayList();
    private static final Consumer<UUID> DEFAULT_TASK = uuid -> {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.getPersistentDataContainer().set(CAN_FLY_ON_TODAY, PersistentDataType.BOOLEAN, false);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.sendMessage("飞行时间限制已到达");
    };
    private static final Runnable RESET_TASK = () -> {
        Map<UUID, Counter> map = instance.counters;
        for (Map.Entry<UUID, Counter> entry : map.entrySet()) {
            entry.getValue().resetCurrentSec();
            OfflinePlayer player = Bukkit.getOfflinePlayer(entry.getKey());
            if (player.isOnline()) {
                instance.resetFlyStateToPlayer(player.getPlayer());
                continue;
            }
            instance.needReset.add(player.getUniqueId());
        }
    };
    public void setNeedReset(List<UUID> needReset) {
        if (needReset == null || needReset.isEmpty()) return;
        this.needReset = needReset;
    }
    public void setCounters(Map<UUID, Counter> counters) {
        if (counters == null || counters.isEmpty()) return;
        this.counters = counters;
    }
    public List<UUID> getNeedReset() {
        return needReset;
    }
    public Map<UUID, Counter> getCounters() {
        return counters;
    }
    private void resetFlyStateToPlayer(Player player) {
        player.getPersistentDataContainer().set(CAN_FLY_ON_TODAY, PersistentDataType.BOOLEAN, true);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.sendMessage("飞行时间限制已重置");
    }
    public void start() {
        registerCounterScheduler();
        registerResetScheduler();
    }
    private void registerCounterScheduler() {
        Bukkit.getScheduler().runTaskTimer(
                WorldRewards.getInstance(),
                this::count,
                20L,20L
        );
    }
    private void registerResetScheduler() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.toLocalDate().plusDays(1).atStartOfDay();
        Duration duration = Duration.between(now, start);
        long millis = duration.toMillis();
        long tick = millis / 50;
        Bukkit.getScheduler().runTaskLater(
                WorldRewards.getInstance(),
                RESET_TASK, tick
        );
    }
    public void count() {
        counters.forEach((uuid, counter) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) return;
            if (!player.getAllowFlight()) return;
            if (player.isOp()) return;
            if (counter.checkAndAdd()) DEFAULT_TASK.accept(uuid);
        });
    }
    public static FlyManager getInstance() {
        return instance;
    }
    @EventHandler
    public void resetFlyTime(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!needReset.contains(player.getUniqueId())) return;
        resetFlyStateToPlayer(player);
        needReset.remove(player.getUniqueId());
    }
    public Counter getCounter(UUID uuid) {
        return counters.get(uuid);
    }
    public void putCounter(UUID uuid, Counter counter) {
        if (counters.containsKey(uuid)) return;
        counters.put(uuid, counter);
    }
}
