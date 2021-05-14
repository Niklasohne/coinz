package de.ronon_lul.simplecoinz.bank.databaseClasses;

import com.mongodb.async.client.MongoCollection;
import de.ronon_lul.simplecoinz.utils.Database.CollectionNames;
import de.ronon_lul.simplecoinz.utils.Database.DataBaseObj;
import de.ronon_lul.simplecoinz.utils.Database.MongoDBConnector;
import de.ronon_lul.simplecoinz.bank.errorCodes.NotEnoughMoney;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;


/**
 * Represents information about an player in the database
 */
@AllArgsConstructor
public class BankUser implements DataBaseObj {

    private static final MongoCollection<Document> bankUserCollection = MongoDBConnector.getCollection(CollectionNames.user_list);

    @Getter
    private final String uuid;
    @Getter
    private final String name;
    @Getter
    private double cash = 0;
    @Getter
    private double bank = 0;


    @Deprecated
    public BankUser() {
        uuid = "";
        name = "";
    }

    /**
     * Create a brand new BankUser, NOT from the database
     *
     * @param name the Username of the Player
     * @param uuid the unique ID of the Player
     */
    public BankUser(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    /**
     * Generate an BankUser from an Document
     * @param doc the src Doc
     */
    private BankUser (Document doc){
        if(doc == null)
            throw new RuntimeException("This User was not found");
        this.uuid = doc.getString("uuid");
        this.name = doc.getString("name");
        this.bank = doc.getDouble("bank");
        this.cash = doc.getDouble("cash");
    }




    /**
     * search the Database async for a specific Bankuser by the Name
     *
     * @param user     the username of the BankUser you want to search for
     * @param callback the Callbackfunktion, what you want to do with the BankUser
     */
    public static void asyncBankUserByName(String user, Consumer<BankUser> callback) {
        bankUserCollection
                .find(eq("name", user))
                .first((bankUserDoc, t) -> {
                    System.out.println(bankUserDoc);
                    BankUser tmpUser = bankUserDoc == null ? null : new BankUser(bankUserDoc);
                    callback.accept(tmpUser);
                });
    }


    /**
     * search the Database async for a specific Bankuser by the UUID
     *
     * @param id       the UUID of the BankUser you want to search for
     * @param callback the Callbackfunktion, what you want to do with the BankUser
     */
    public static void asyncBankUserByID(String id, Consumer<BankUser> callback) {
        bankUserCollection.find(eq("uuid", id)).first((bankUserDoc, t) -> {
            BankUser tmpUser = bankUserDoc == null ? null : new BankUser(bankUserDoc);
            callback.accept(tmpUser);
        });
    }


    /**
     * Getting cash, e.g. for selling Stuff
     * @param amount the amount you receive
     */
    public void receiveCash(double amount) {
        cash += amount;
        updateBankUserToDB();
    }


    /**
     * spending Cash e.g for buying Cash
     * @param amount the amount you spend
     * @throws NotEnoughMoney if the player has not enough money in his wallet
     */
    public void spendCash(double amount) throws NotEnoughMoney {
        if (cash < amount)
            throw new NotEnoughMoney();
        cash -= amount;
        updateBankUserToDB();
    }

    /**
     * Transfer a specific amount of Cash into the players own BankAccount
     *
     * @param value the amount the player wants to transfer
     * @throws NotEnoughMoney if the player has not enough money in his wallet
     */
    public void cashIn(double value) throws NotEnoughMoney {
        if (cash < value)
            throw new NotEnoughMoney();
        cash -= value;
        bank += value;
        System.out.println("Cash: " + cash);
        updateBankUserToDB();
    }

    /**
     * Transfer a specific amount of Money from the bankaccount to the players Cash wallet
     *
     * @param value the amount the player wants to transfer
     * @throws NotEnoughMoney if the player has not enough money in his Bank account
     */
    public void cashOut(double value) throws NotEnoughMoney {
        if (bank < value)
            throw new NotEnoughMoney();
        bank -= value;
        cash += value;
        updateBankUserToDB();
    }


    /**
     * Transfer a specific amount of Money from the Bankaccount of the Player to the BankAccount of another player
     *
     * @param value    the amount the player wants to send
     * @param receiver the BankUser where the Player wants to send the money to
     * @throws NotEnoughMoney if the player has not enough money in his Bankaccount
     */
    public void transfer(double value, BankUser receiver) throws NotEnoughMoney {
        if (bank < value)
            throw new NotEnoughMoney();
        bank -= value;
        receiver.transferIN(value);
        updateBankUserToDB();
    }

    /**
     * a private Helpfunktion to handle incoming transfers
     *
     * @param value the amount the player receives
     */
    private void transferIN(double value) {
        bank += value;
        updateBankUserToDB();
    }


    /**
     * add the BankUser to the Database
     * checks if BankUser already exists first
     */
    public void addBankUserToDB() {
        bankUserCollection.find(eq("uuid", uuid)).first((result, t) -> {
            if (result != null) {
                System.out.println("Player already existed");
            } else {
                bankUserCollection.insertOne(getAsDocument(), (unused, t_2) -> {
                    if (t_2 != null) System.out.println(t_2.getMessage());
                });
            }
        });
    }


    /**
     * removes (if existing) the BankPlayer from the Database
     */
    public void removeBankUserFromDB() {
        bankUserCollection.findOneAndDelete(eq("uuid", this.uuid), (result, t) -> {
            if (t != null) System.out.println(t.getMessage());
        });
    }


    /**
     * refresh the BankPlayers values to the Database
     * done after each Interaction
     */
    public void updateBankUserToDB() {
        bankUserCollection.findOneAndReplace(eq("uuid", this.uuid), getAsDocument(), (result, t) -> {
            if (t != null) System.out.println(t.getMessage());
        });
    }


    @Override
    public String toString() {
        return "BankUser{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", cash=" + cash +
                ", bank=" + bank +
                '}';
    }

    @Override
    public Document getAsDocument() {

        return new Document("uuid", uuid)
                .append("name", name)
                .append("cash", cash)
                .append("bank", bank);
    }

}
