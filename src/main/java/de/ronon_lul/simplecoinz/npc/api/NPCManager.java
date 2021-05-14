package de.ronon_lul.simplecoinz.npc.api;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.ronon_lul.simplecoinz.npc.api.events.NPCClickInteraction;
import de.ronon_lul.simplecoinz.npc.api.events.NPCClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Manages current active NPCs
 */
public class NPCManager implements Listener {
    private final JavaPlugin plugin;

    private final Set<NPC> registeredNPCs = new HashSet<>();

    public NPCManager(JavaPlugin plugin) {
        this.plugin = plugin;
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        EnumWrappers.EntityUseAction useAction = event.getPacket().getEntityUseActions().read(0);
                        int entityId = event.getPacket().getIntegers().read(0);
                        handleEntityClick(event.getPlayer(), entityId, NPCClickInteraction.fromProtocolLib(useAction));
                    }
                }
        );

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private final Cache<Player, NPC> clickedNPCCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1L, TimeUnit.SECONDS)
            .build();

    /**
     * Triggers the Right event with finding the right NPC
     * maybe inefficient with to many NPCs
     * @param player the player who clicked
     * @param entityId the entity he clicked
     * @param action the click
     */
    private void handleEntityClick(Player player, int entityId, NPCClickInteraction action) {
        registeredNPCs.stream()
                .filter(npc -> npc.getId() == entityId)
                .forEach(npc -> Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                    NPC previouslyClickedNPC = clickedNPCCache.getIfPresent(player);
                    if (previouslyClickedNPC != null && previouslyClickedNPC.equals(npc)) return; // If they've clicked this same NPC in the last 0.5 seconds ignore this click
                    clickedNPCCache.put(player, npc);
                    NPCClickEvent event = new NPCClickEvent(npc, player, action);
                    Bukkit.getPluginManager().callEvent(event);
                }, 2));
    }


    /**
     * Spawn an NPC on the server
     * @param npc to spawn
     */
    public void addNPC(NPC npc){
        registeredNPCs.add(npc);
        npc.showAll();
    }


    /**
     * remove ALL NPC from the server
     */
    public void removeALl(){
        Set<NPC> npcsCopy = new HashSet<>(registeredNPCs);
        npcsCopy.forEach(this::removeNPC);
    }

    /**
     * remove 1 NPC from the server
     * @param npc to remove
     */
    public void removeNPC(NPC npc){
        npc.hideAll();
        registeredNPCs.remove(npc);
    }

    /**
     * @param player make all NPCs visible for this player
     */
    private void showAllTo(Player player){
        registeredNPCs.forEach(npc -> npc.showTo(player));
    }
    /**
     * @param player make all NPCs invisible for this player
     */
    private void hideAllTo(Player player){
        registeredNPCs.forEach(npc -> npc.hideTo(player));
    }

    /**
     * first make all invisible, than activate all again
     * @param player for this player
     */
    public void refreshAllTo(Player player){
        hideAllTo(player);
        showAllTo(player);
    }


    /**
     * when someone is clicked an NPC, catch the Event
     * @param event the NNPCClickEvent
     */
    @EventHandler
    public void onNPCClick(NPCClickEvent event){
        SimpleNPC npc =  (SimpleNPC) event.getClickedNPC();
        npc.handleInteraction(event);
    }

    /**
     * when the player changes worlds (e.g. Nether< - >overworld)
     * all NPCs must be reloaded for him to prevent bugs
     * @param event the NNPCClickEvent
     */
    @EventHandler
    public void playerChangeWorld(PlayerChangedWorldEvent event){
        refreshAllTo(event.getPlayer());
    }

    /**
     * make every NPC visible for an Player when joining the server
     * @param event the NNPCClickEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        showAllTo(event.getPlayer());
    }
}
