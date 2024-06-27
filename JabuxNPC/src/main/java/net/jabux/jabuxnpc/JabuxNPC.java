package net.jabux.jabuxnpc;

import net.jabux.jabuxnpc.command.NPCCommand;
import net.jabux.jabuxnpc.database.MySQLManager;
import net.jabux.jabuxnpc.event.NPCClickListener;
import net.jabux.jabuxnpc.maneger.NPCManager;
import org.bukkit.plugin.java.JavaPlugin;
public class JabuxNPC extends JavaPlugin {
    private NPCManager npcManager;
    private MySQLManager mySQLManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        mySQLManager = new MySQLManager(this);
        npcManager = new NPCManager(this, mySQLManager);
        getCommand("npc").setExecutor(new NPCCommand(this, npcManager));
        getServer().getPluginManager().registerEvents(new NPCClickListener(this, npcManager), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getServer().getScheduler().runTaskLater(this, npcManager::loadNPCs, 20L);
    }


    @Override
    public void onDisable() {
        mySQLManager.close();
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }
}