package de.ronon_lul.simplecoinz.npc.api.events;

import de.ronon_lul.simplecoinz.npc.api.NPC;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Custom event which gets triggered when someone interacts with an NPC
 */
public class NPCClickEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Getter
    private final NPC clickedNPC;
    @Getter
    private final Player whoClicked;
    @Getter
    private final NPCClickInteraction clickInteraction;

    public NPCClickEvent(NPC clickedNPC, Player whoClicked, NPCClickInteraction clickInteraction) {
        this.clickedNPC = clickedNPC;
        this.whoClicked = whoClicked;
        this.clickInteraction = clickInteraction;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
