package net.jabux.jabuxnpc.maneger;

import net.jabux.jabuxnpc.JabuxNPC;
import net.jabux.jabuxnpc.database.MySQLManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NPCManager {
    private JabuxNPC plugin;
    private MySQLManager mySQLManager;
    private Map<Integer, NPC> npcs = new HashMap<>();

    public NPCManager(JabuxNPC plugin, MySQLManager mySQLManager) {
        this.plugin = plugin;
        this.mySQLManager = mySQLManager;
        loadNPCs();
    }

    public void loadNPCs() {
        String sql = "SELECT * FROM npcs;";
        try (Connection connection = mySQLManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String skin = rs.getString("skin");
                String server = rs.getString("server");
                String world = rs.getString("world");
                double x = rs.getDouble("x");
                double y = rs.getDouble("y");
                double z = rs.getDouble("z");
                float yaw = rs.getFloat("yaw");
                float pitch = rs.getFloat("pitch");
                int playerCount = rs.getInt("player_count");
                Location location = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
                NPC npc = new NPC(id, name, skin, server, location);
                npc.setPlayerCount(playerCount);
                npc.create();
                npcs.put(id, npc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createNPC(Location location) {
        String sql = "INSERT INTO npcs (name, skin, server, world, x, y, z, yaw, pitch, player_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (Connection connection = mySQLManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "Default Name");
            stmt.setString(2, "DefaultSkin");
            stmt.setString(3, "DefaultServer");
            stmt.setString(4, location.getWorld().getName());
            stmt.setDouble(5, location.getX());
            stmt.setDouble(6, location.getY());
            stmt.setDouble(7, location.getZ());
            stmt.setFloat(8, location.getYaw());
            stmt.setFloat(9, location.getPitch());
            stmt.setInt(10, 0); // Inicializar player_count como 0
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    NPC npc = new NPC(id, "Default Name", "DefaultSkin", "DefaultServer", location);
                    npc.create();
                    npcs.put(id, npc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public NPC getNPC(int id) {
        return npcs.get(id);
    }

    public void deleteNPC(int id) {
        String sql = "DELETE FROM npcs WHERE id = ?;";
        try (Connection connection = mySQLManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            npcs.remove(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Collection<NPC> getNPCs() {
        return npcs.values();
    }

    public void incrementPlayerCount(int npcId) {
        NPC npc = npcs.get(npcId);
        if (npc != null) {
            npc.incrementPlayerCount();
            String sql = "UPDATE npcs SET player_count = player_count + 1 WHERE id = ?;";
            try (Connection connection = mySQLManager.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, npcId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void decrementPlayerCount(int npcId) {
        NPC npc = npcs.get(npcId);
        if (npc != null) {
            npc.decrementPlayerCount();
            String sql = "UPDATE npcs SET player_count = player_count - 1 WHERE id = ?;";
            try (Connection connection = mySQLManager.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, npcId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}