package org.wishtoday.egar.worldRewards.Config;

import org.wishtoday.egar.worldRewards.Fly.Counter;
import org.wishtoday.egar.worldRewards.Fly.FlyManager;
import org.wishtoday.egar.worldRewards.WorldRewards;

import java.io.File;
import java.sql.*;
import java.util.*;

public class DataSave {
    private static final File FILE = new File(WorldRewards.getInstance().getDataFolder(), "database.db");
    private Connection con = null;
    private static DataSave instance;


    public void save() {
        Map<UUID, Counter> counters = FlyManager.getInstance().getCounters();
        String sql = """
                INSERT INTO flys (id, v)
                VALUES (?, ?)
                ON CONFLICT(id) DO UPDATE SET
                v = excluded.v
                """;
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
        } catch (SQLException e) {
            WorldRewards.getInstance().getLogger().warning("Could not INSERT to database.:" + e.getMessage());
        }

        PreparedStatement finalPs = ps;
        counters.forEach((uuid, counter) -> {
            try {
                finalPs.setString(1, uuid.toString());
                finalPs.setString(2, counter.serialization());
                finalPs.executeUpdate();
            } catch (SQLException | NullPointerException e) {
                WorldRewards.getInstance().getLogger().warning("Could not INSERT to database.:" + e.getMessage());
            }
        });
        WorldRewards.getInstance().getLogger().info("Saved database.");
    }

    public void saveList() {
        List<UUID> needReset = FlyManager.getInstance().getNeedReset();
        String sql = """
                INSERT INTO flys (id, v)
                VALUES (?, ?)
                ON CONFLICT(id) DO UPDATE SET
                v = excluded.v
                """;
        List<String> list = needReset.stream().map(UUID::toString).toList();
        try (PreparedStatement prepareStatement = con.prepareStatement(sql)) {
            prepareStatement.setString(1, "need_reset");
            prepareStatement.setString(2, String.join(",", list));
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            WorldRewards.getInstance().getLogger().warning("Could not INSERT to database.:" + e.getMessage());
        }
        WorldRewards.getInstance().getLogger().info("Saved database.");
    }

    public void load() {
        if (!FILE.exists()) create();
        if (con == null) connect(FILE);
        HashMap<UUID, Counter> map = new HashMap<>();
        String sql = "SELECT id, v FROM flys WHERE LENGTH(id) = 36";
        try (PreparedStatement prepareStatement = con.prepareStatement(sql)) {
            ResultSet set = prepareStatement.executeQuery();
            while (set.next()) {
                map.put(UUID.fromString(set.getString("id")), Counter.deSerialization(set.getString("v")));
            }
        } catch (SQLException e) {
            WorldRewards.getInstance().getLogger().warning("Could not SELECT Load to database.:" + e.getMessage());
        }
        FlyManager.getInstance().setCounters(map);
    }

    public void loadList() {
        List<UUID> needReset = new ArrayList<>();
        String sql = "SELECT v FROM flys WHERE id = 'need_reset'";
        try (PreparedStatement prepareStatement = con.prepareStatement(sql)) {
            ResultSet set = prepareStatement.executeQuery();
            while (set.next()) {
                String string = set.getString("v");
                if (string.isEmpty()) continue;
                needReset.addAll(Arrays.stream(string.split(",")).map(UUID::fromString).toList());
            }
        } catch (SQLException e) {
            WorldRewards.getInstance().getLogger().warning("Could not SELECT LoadList to database.:" + e.getMessage());
        }
        FlyManager.getInstance().setNeedReset(needReset);
    }

    public synchronized static DataSave getInstance() {
        if (instance == null) {
            instance = new DataSave();
        }
        return instance;
    }

    private void create() {
        this.connect(FILE);
        if (con != null) {
            String SQL = "CREATE TABLE IF NOT EXISTS flys (id CHAR(36) PRIMARY KEY , v TEXT NOT NULL)";
            try (Statement statement = con.createStatement()) {
                statement.executeUpdate(SQL);
            } catch (SQLException e) {
                WorldRewards.getInstance().getLogger().warning("Could not create to database.:" + e.getMessage());
            }
        }
    }

    private void connect(File path) {
        String SQLPath = "jdbc:sqlite:" + path;
        try {
            con = DriverManager.getConnection(SQLPath);
        } catch (SQLException e) {
            WorldRewards.getInstance().getLogger().warning("Could not connect to database.:" + e.getMessage());
        }
        WorldRewards.getInstance().getLogger().info("Connected to database.");
    }
}
