package de.ronon_lul.simplecoinz.npc.api.events;

import com.comphenix.protocol.wrappers.EnumWrappers;


/**
 * possible ways how it would be possible the player interacts with the NPC
 */
public enum NPCClickInteraction {
    INTERACT, INTERACT_AT, ATTACK;

    /**
     * pars protocollib inputs
     * @param action protocollib action
     * @return NPCClickInteraction
     */
    public static NPCClickInteraction fromProtocolLib(EnumWrappers.EntityUseAction action){
        switch (action){

            case INTERACT: return INTERACT;
            case ATTACK: return ATTACK;
            case INTERACT_AT: return  INTERACT_AT;
            default: return null;
        }
    }
}
