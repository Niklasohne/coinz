package de.ronon_lul.simplecoinz.utils.Database;


/**
 * names of existing collections on the DB server
 */
public enum CollectionNames {
    user_list,
    transfers,
    npcLocations;

    @Override
    public String toString() {
        return name();
    }
}
