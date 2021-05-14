package de.ronon_lul.simplecoinz.npc;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handling of NPC commands
 */
public class CommandsNPC {

    NPCController controller;

    public CommandsNPC(NPCController controller){
        this.controller = controller;
    }

    public boolean addNPC(CommandSender sender, String[] args){
        if(!(sender instanceof Player))
            return true;

        Player player = (Player) sender;

        if(args.length != 1)
            player.sendMessage("you need to select a type!");

        try {
            NPCTypes type = NPCTypes.valueOf(args[0]);
            controller.createNewNPC(type, player.getLocation());

        }catch (Exception e){
            player.sendMessage("This type does not exist!");
        }
        return true;
    }
}
