package de.ronon_lul.simplecoinz.utils.Database;

import org.bson.Document;

/**
 * Interface for all classes which also should get saved in the Database
 */
public interface DataBaseObj {

    /**
     * Helper function to make the Database interaction simpler
     *
     * @return the Class, converted to a Document
     */
    Document getAsDocument();
}
