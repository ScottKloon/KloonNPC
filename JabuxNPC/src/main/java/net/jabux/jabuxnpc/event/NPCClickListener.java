package net.jabux.jabuxnpc.event;

import net.jabux.jabuxnpc.JabuxNPC;
import net.jabux.jabuxnpc.maneger.NPC;
import net.jabux.jabuxnpc.maneger.NPCManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NPCClickListener implements Listener {
    private JabuxNPC plugin;
    private NPCManager npcManager;

    public NPCClickListener(JabuxNPC plugin, NPCManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    @EventHandler
    public void onNPCClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        for (NPC npc : npcManager.getNPCs()) {
            if (npc.getLocation().getWorld().equals(entity.getWorld())
                    && npc.getLocation().distance(entity.getLocation()) < 1) {
                // Identificar o NPC e conectar ao servidor
                npcManager.incrementPlayerCount(npc.getId());
                sendPlayerToServer(player, npc.getServer());
                updateNPCDisplayName(npc);
                break;
            }
        }
    }

    private void sendPlayerToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
    }

    private void updateNPCDisplayName(NPC npc) {
        String displayName = ChatColor.GOLD + "CLIQUE PARA CONECTAR\n"
                + ChatColor.RESET + npc.getName() + "\n"
                + ChatColor.GREEN + npc.getPlayerCount() + " Jogadores";
        // Atualize o nome do NPC (exemplo usando NMS ou API de hologramas)
    }
}