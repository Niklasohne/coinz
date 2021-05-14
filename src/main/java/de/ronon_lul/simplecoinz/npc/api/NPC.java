package de.ronon_lul.simplecoinz.npc.api;

import de.ronon_lul.simplecoinz.npc.NPCTypes;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * standard Interface for NPC, currently only 1 implementation
 */
public interface NPC {
    String getName();

    void showTo(Player player);

    void hideTo(Player player);

    void showAll();

    void hideAll();

    Location getLocation();

    int getId();

    NPCTypes getType();
}
