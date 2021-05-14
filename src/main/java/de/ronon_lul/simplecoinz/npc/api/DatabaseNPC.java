package de.ronon_lul.simplecoinz.npc.api;

import de.ronon_lul.simplecoinz.utils.Database.DataBaseObj;
import de.ronon_lul.simplecoinz.npc.NPCTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;


/**
 * Representation of an NPC for Database saving
 *
 * Document{TYPE=___,world=___,X=___,Y=___,Z=___}
 */
@AllArgsConstructor
public class DatabaseNPC implements DataBaseObj {

    @Getter
    private final NPCTypes type;
    @Getter
    private final Location loc;

    public DatabaseNPC(Document doc){
        if(doc == null)
            throw new RuntimeException("Doc for DatabaseNPC generation was null");
        type = NPCTypes.valueOf(doc.getString("Type"));
        loc = toLocation(doc);
    }

    /**
     * @return the Object as an Document
     */
    @Override
    public Document getAsDocument() {
        return new Document("Type" , type.name())
                .append("world", loc.getWorld().getName())
                .append("X", loc.getX())
                .append("Y", loc.getY())
                .append("Z", loc.getZ());
    }


    /**
     * Helper function to Read an Location out of an Document
     * @param doc the src Document
     * @return the Location of the NPC
     */
    private static Location toLocation(Document doc) {
        return new Location(
                Bukkit.getWorld(doc.getString("world")),
                doc.getDouble("X"),
                doc.getDouble("Y"),
                doc.getDouble("Z"),
                0,
                0
        );
    }
}
