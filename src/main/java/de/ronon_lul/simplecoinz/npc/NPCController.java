package de.ronon_lul.simplecoinz.npc;

import com.mongodb.async.client.MongoCollection;
import de.ronon_lul.simplecoinz.utils.Database.CollectionNames;
import de.ronon_lul.simplecoinz.utils.Database.MongoDBConnector;
import de.ronon_lul.simplecoinz.SimpleCoinz;
import de.ronon_lul.simplecoinz.npc.api.DatabaseNPC;
import de.ronon_lul.simplecoinz.npc.api.NPC;
import de.ronon_lul.simplecoinz.npc.api.NPCManager;

import de.ronon_lul.simplecoinz.npc.api.SimpleNPC;
import org.bson.Document;
import org.bukkit.Location;

import java.util.Objects;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * Controller of all NPC
 *
 * loads NPC on server-start and tells the Manager what and where to spawn them
 *
 */
public class NPCController {

    private final SimpleCoinz plugin;
    private final static MongoCollection<Document> npcs = MongoDBConnector.getCollection(CollectionNames.npcLocations);
    private final NPCManager npcManager;


    public NPCController(SimpleCoinz plugin) {
        this.plugin = plugin;
        this.npcManager = new NPCManager(plugin);
        plugin.adddisableFunctions(unused -> npcManager.removeALl());
        loadFromDB();
    }

    /**
     * Create a new NPC , spawn it and save in the database
     * @param type type of NPC
     * @param location position where the NPC is standing
     */
    public void createNewNPC(NPCTypes type, Location location) {
        NPC npc = new SimpleNPC(plugin, Objects.requireNonNull(NPCFactory.generateInfoByType(type, location)));
        npcManager.addNPC(npc);
        addTODB(npc);
    }

    /**
     * delete a NPC , remove it from server and Database
     * @param npc you want to remove
     */
    public void deleteNPC(NPC npc) {
        npcManager.removeNPC(npc);
        removeFromDB(npc);
    }


    /**
     * loads all NPCs from the database and spawns them automatically
     */
    private void loadFromDB() {
        npcs.find().forEach(singleNPC -> {
                    System.out.println(singleNPC.toString());
                    DatabaseNPC dbnpc = new DatabaseNPC(singleNPC);
                    NPC npc = new SimpleNPC(plugin, Objects.requireNonNull(NPCFactory.generateInfoByType(dbnpc.getType(), dbnpc.getLoc())));
                    npcManager.addNPC(npc);
                }, (result, t) -> System.out.println(t != null ? t.getMessage() : "loaded all NPC's")
        );
    }


    private void addTODB(NPC npc) {
        DatabaseNPC dbnpc = new DatabaseNPC(npc.getType(), npc.getLocation());

        npcs.insertOne(dbnpc.getAsDocument(), (result, t) -> System.out.println(t != null ? t.getMessage() : "Added a new NPC"));
    }

    private void removeFromDB(NPC npc) {
        npcs.deleteOne(
                and(
                        eq("World", npc.getLocation().getWorld()),
                        eq("X", npc.getLocation().getX()),
                        eq("Y", npc.getLocation().getY()),
                        eq("Z", npc.getLocation().getZ()),
                        eq("Type", npc.getType())
                ), (result, t) -> {});
    }


}
