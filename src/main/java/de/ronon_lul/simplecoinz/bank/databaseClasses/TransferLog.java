package de.ronon_lul.simplecoinz.bank.databaseClasses;

import com.mongodb.async.client.MongoCollection;
import de.ronon_lul.simplecoinz.utils.Database.CollectionNames;
import de.ronon_lul.simplecoinz.utils.Database.DataBaseObj;
import de.ronon_lul.simplecoinz.utils.Database.MongoDBConnector;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.*;

@AllArgsConstructor
public class TransferLog implements DataBaseObj {

    private static MongoCollection<Document> transferCollection = MongoDBConnector.getCollection(CollectionNames.transfers);


    @Getter
    private final String uuidFrom;

    @Getter
    private final String uuidTo;

    @Getter
    private final double amount;

    @Getter
    private final TransferReason reason;

    @Getter
    private final Time time;


    /**
     * Only used inside the Class
     *also saves the Transferlog directly to the Database
     */
    private TransferLog(String _uuidFrom, String _uuidTo, double _amount, TransferReason _reason) {
        uuidFrom = _uuidFrom;
        uuidTo = _uuidTo;
        amount = _amount;
        reason = _reason;
        time = Time.current();

        saveToDB();
    }

    /**
     * Gernerate an TransferLog from an Document
     * @param doc the src Doc
     * @return the finished BankUser
     */
    public TransferLog(Document doc){
        if(doc == null)
            throw new RuntimeException("This TransferLog does not exist");

        this.uuidFrom = doc.getString("uuidFrom");
        this.uuidTo = doc.getString("uuidTo");
        this.amount = doc.getDouble("ammount");
        this.reason = TransferReason.valueOf(doc.getString("grund"));
        this.time = Time.fromString(doc.getString("time"));
    }


    /**
     * Create an TransferLOg for an Transaction between 2 Players
     *
     * @param _uuidFrom the UUID from the sender
     * @param _uuidTo  the UUID from the receiver
     * @param _amount  the amount of money transferred
     * @return the Ready Transferlog (already saved into the Database)
     */
    public static TransferLog transaction(String _uuidFrom, String _uuidTo, double _amount) {
        return new TransferLog(_uuidFrom, _uuidTo, _amount, TransferReason.TRANSFER);
    }

    /**
     * Create an Transferlog for an cashIn action
     * @param _uuid the UUID of the player
     * @param _amount the amount of money transferred
     * @return
     */
    public static TransferLog cashIn(String _uuid, double _amount) {
        return new TransferLog(_uuid, " ", _amount, TransferReason.CASHIN);
    }

    /**
     * Create an Transferlog for an cashIn action
     * @param _uuid the UUID of the player
     * @param _amount the amount of money transferred
     * @return
     */
    public static TransferLog cashOut(String _uuid, double _amount) {
        return new TransferLog(_uuid, " ", _amount, TransferReason.CASHOUT);
    }

    //Database Connections
    public static void getTransfersByUUID(String uuid, Consumer<List<TransferLog>> consumer) {
        List<TransferLog> tmp = new ArrayList<>();
        transferCollection
                .find(or(eq("uuidFrom", uuid), eq("uuidTo", uuid)))
                .forEach(transferlogDoc -> {
                    TransferLog tmptransferLog = new TransferLog(transferlogDoc);
                    tmp.add(tmptransferLog);
                }, (result, t) -> {
                    if (t != null) System.out.println(t.getMessage());
                    consumer.accept(tmp);
                });
    }


    private void saveToDB() {
        transferCollection.insertOne(this.getAsDocument(),
                (result, t) -> {
                    if (t != null) System.out.println(t.getMessage());
                });
    }


    @Override
    public String toString() {
        return "TransferLog{" +
                "uuidFrom='" + uuidFrom + '\'' +
                ", uuidTo='" + uuidTo + '\'' +
                ", amount=" + amount +
                ", reason=" + reason +
                ", time=" + time +
                '}';
    }

    public String fancyString(String uuid){
        switch (getReason()){
            case CASHIN: return ChatColor.GREEN+ "+" + getAmount() + "C  -> CashIN (" + getTime().toString() + ")";
            case CASHOUT: return ChatColor.RED+ "-" + getAmount() + "C  -> CashOut (" + getTime().toString() + ")";
            case TRANSFER: return uuid.equals(getUuidFrom())
                    ? ChatColor.RED+ "-" + getAmount() + "C  -> Transfer to: " + Bukkit.getOfflinePlayer(UUID.fromString(getUuidTo())).getName() +" (" + getTime().toString() + ")"
                    : ChatColor.GREEN+ "+" + getAmount() + "C  -> Transfer from: " + Bukkit.getOfflinePlayer(UUID.fromString(getUuidFrom())).getName() +" (" + getTime().toString() + ")";
        }
        return "";
    }




    @Override
    public Document getAsDocument() {
        return new Document("uuidFrom", uuidFrom)
                .append("uuidTo", uuidTo)
                .append("ammount", amount)
                .append("grund", reason.name())
                .append("time", time.toString());
    }
}
