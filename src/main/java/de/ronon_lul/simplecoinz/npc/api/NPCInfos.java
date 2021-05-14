package de.ronon_lul.simplecoinz.npc.api;

import de.ronon_lul.simplecoinz.npc.NPCTypes;
import de.ronon_lul.simplecoinz.npc.api.events.NPCClickEvent;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.Location;

import java.util.function.Consumer;


/**
 * Contains everything you need to know to spawn an NPC
 */
@Builder
@Getter
public class NPCInfos {
    private final String name;
    private final String texture;
    private final String signature;
    private final Location location;
    private final boolean hideNameTag;
    private final NPCTypes type;
    private final Consumer<NPCClickEvent> handler;
}
