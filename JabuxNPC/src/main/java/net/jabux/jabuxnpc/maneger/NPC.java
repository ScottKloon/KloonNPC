package net.jabux.jabuxnpc.maneger;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.jabux.jabuxnpc.skin.SkinFetcher.fetchSkinProperty;


public class NPC {
    private int id;
    private String name;
    private String skin;
    private String server;
    private Location location;
    private int playerCount;

    public NPC(int id, String name, String skin, String server, Location location) {
        this.id = id;
        this.name = name;
        this.skin = skin;
        this.server = server;
        this.location = location;
        this.playerCount = 0; // Inicializar a contagem de jogadores
    }

    public void create() {
        MinecraftServer nmsServer = ((CraftWorld) location.getWorld()).getHandle().getMinecraftServer();
        WorldServer nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);

        // Adiciona a skin ao GameProfile
        if (skin != null && !skin.isEmpty()) {
            Property property = fetchSkinProperty(skin);
            if (property != null) {
                gameProfile.getProperties().put("textures", property);
            }
        }

        EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        npc.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        }
    }



    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Location getLocation() {
        return location;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public void incrementPlayerCount() {
        this.playerCount++;
    }

    public void decrementPlayerCount() {
        this.playerCount--;
    }
}