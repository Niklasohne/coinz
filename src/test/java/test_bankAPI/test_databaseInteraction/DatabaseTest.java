package test_bankAPI.test_databaseInteraction;

import de.ronon_lul.simplecoinz.utils.Database.MongoDBConnector;
import de.ronon_lul.simplecoinz.bank.databaseClasses.BankUser;
import de.ronon_lul.simplecoinz.bank.databaseClasses.TransferLog;
import com.mongodb.async.client.ListDatabasesIterable;
import org.bson.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DatabaseTest {

    static String dbURL = "mongodb://192.168.178.142";


    static BankUser tester1;

    @BeforeClass
    public static void setup() {
        MongoDBConnector.setup(dbURL,"coinz");


        //add an TestUser to the DB
        tester1 = new BankUser("testerID", "testerName");
        tester1.addBankUserToDB();

    }


    @Test
    public void connectionTest() {
        try {
            Thread.sleep(1000);
            ListDatabasesIterable<Document> test = MongoDBConnector.getMongoClient().listDatabases();
            System.out.println(test);
        } catch (Exception e) {
            fail("no Connection possible");
        }
    }

    //Note: for next 3 tests: you need to create the User in the DB first! : ' db.user_list.insert({ "uuid" : "testerID", "name" : "testerName", "bank" : 0, "cash" : 0 }) '
    @Test
    public void getBankUserName() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        BankUser.asyncBankUserByName("testerName", bankUser -> {
            System.out.println(bankUser);
            if (bankUser != null && bankUser.getUuid().equals("testerID")) {
                success.set(true);
                lock.countDown();
            }
            else {
                fail("'testerName' was not found");
            }
        });

        lock.await(3000, TimeUnit.MILLISECONDS);
        assertTrue(success.get());
    }

    @Test
    public void getBankUserName_notExisting() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        BankUser.asyncBankUserByName("NoNExiSTIng", bankUser -> {
            if (bankUser == null) {
                success.set(true);
                lock.countDown();
            }
            else {
                fail("'NoNExiSTIng' was existing");
            }
        });

        lock.await(3000, TimeUnit.MILLISECONDS);
        assertTrue(success.get());
    }

    @Test
    public void getBankUserID() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        BankUser.asyncBankUserByID("testerID", bankUser -> {
            if (bankUser != null && bankUser.getName().equals("testerName")) {
                success.set(true);
                lock.countDown();
            }
            else {
                throw new RuntimeException("'testerID' was not found");
            }
        });

        lock.await(3000, TimeUnit.MILLISECONDS);
        assertTrue(success.get());
    }


    @Test
    public void getTransferLogs() throws InterruptedException {
        CountDownLatch lock = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);
        TransferLog.getTransfersByUUID("testerID_42", transferLogs -> {
            if (transferLogs != null && transferLogs.size() >0) {
                transferLogs.forEach(System.out::println);
                success.set(true);
                lock.countDown();
            }
            else {
                throw new RuntimeException("transferlogs not found");
            }
        });

        lock.await(3000, TimeUnit.MILLISECONDS);
        assertTrue(success.get());
    }




    @Test
    public void BankUserErstellen_löschen() throws InterruptedException {
        AtomicBoolean success = new AtomicBoolean(false);

        //Add new user to DB
        BankUser b = new BankUser("honey", "gandalfLULasdferagst");
        b.addBankUserToDB();
        Thread.sleep(3000);

        //check if existing
        CountDownLatch lock = new CountDownLatch(1);
        BankUser.asyncBankUserByName("honey", user -> {
            if (user != null)
                success.set(true);
            lock.countDown();
        });
        lock.await(3000, TimeUnit.MILLISECONDS);
        assertTrue("'honey' was not created",success.get());


        //removing
        b.removeBankUserFromDB();
        Thread.sleep(3000);
        //check if removed
        CountDownLatch lock2 = new CountDownLatch(1);
        BankUser.asyncBankUserByName("honey", bankUser -> {
            if (bankUser != null)
                success.set(true);
            lock2.countDown();
        });
        lock2.await(3000, TimeUnit.MILLISECONDS);
        assertTrue("'honey' was not delated", success.get());
    }
}
