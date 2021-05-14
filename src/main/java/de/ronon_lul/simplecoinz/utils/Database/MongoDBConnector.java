package de.ronon_lul.simplecoinz.utils.Database;

import de.ronon_lul.simplecoinz.bank.errorCodes.MultipleSetup;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;


/**
 * Handles the setup of the Database
 */
public class MongoDBConnector {

    @Getter private static MongoClient mongoClient;
    @Getter private static MongoDatabase database;
    private static boolean alreadySetup = false;


    public static void setup (String connection, String databaseName){
        if(alreadySetup)
            throw new MultipleSetup();
        alreadySetup = true;

        mongoClient = MongoClients.create(connection);
        database = mongoClient.getDatabase(databaseName);
    }

    public static MongoCollection<Document> getCollection(CollectionNames collectionName){
        return database.getCollection(collectionName.toString());
    }

}

