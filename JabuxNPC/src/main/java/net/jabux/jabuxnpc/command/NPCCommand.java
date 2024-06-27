package net.jabux.jabuxnpc.command;

import net.jabux.jabuxnpc.JabuxNPC;
import net.jabux.jabuxnpc.maneger.NPC;
import net.jabux.jabuxnpc.maneger.NPCManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCommand implements CommandExecutor {
    private JabuxNPC plugin;
    private NPCManager npcManager;

    public NPCCommand(JabuxNPC plugin, NPCManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Somente jogadores podem usar este comando.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("/npc criar - Cria um novo NPC.");
            player.sendMessage("/npc <id> name <name> - Define o nome do NPC.");
            player.sendMessage("/npc <id> server <server> - Define o servidor de conexão do NPC.");
            player.sendMessage("/npc <id> skin <skin> - Define a skin do NPC.");
            player.sendMessage("/npc delete <id> - Remove o NPC.");
            return true;
        }

        if (args[0].equalsIgnoreCase("criar")) {
            npcManager.createNPC(player.getLocation());
            player.sendMessage("NPC criado com sucesso.");
            return true;
        }

        if (args.length < 3) {
            player.sendMessage("Uso incorreto do comando.");
            return true;
        }

        try {
            int id = Integer.parseInt(args[0]);
            NPC npc = npcManager.getNPC(id);
            if (npc == null) {
                player.sendMessage("NPC não encontrado.");
                return true;
            }

            if (args[1].equalsIgnoreCase("name")) {
                String name = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);
                npc.setName(name);
                player.sendMessage("Nome do NPC atualizado para: " + name);
            } else if (args[1].equalsIgnoreCase("server")) {
                npc.setServer(args[2]);
                player.sendMessage("Servidor do NPC atualizado para: " + args[2]);
            } else if (args[1].equalsIgnoreCase("skin")) {
                npc.setSkin(args[2]);
                player.sendMessage("Skin do NPC atualizado para: " + args[2]);
            } else {
                player.sendMessage("Comando desconhecido.");
            }

        } catch (NumberFormatException e) {
            player.sendMessage("ID inválido.");
        }

        return true;
    }
}