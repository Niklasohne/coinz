package test_bankAPI;

import de.ronon_lul.simplecoinz.bank.Bank;
import de.ronon_lul.simplecoinz.utils.Database.MongoDBConnector;
import de.ronon_lul.simplecoinz.bank.databaseClasses.BankUser;
import lombok.SneakyThrows;
import org.junit.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class BankTest {


    private static String dbURL = "mongodb://192.168.178.142";
    private static Bank simplebank;

    static String testy_UUID = "testerID_42";
    static String testy_NAME = "tester42";
    static BankUser testy;


    @BeforeClass
    public static void setup(){
        MongoDBConnector.setup(dbURL,"coinz");
        simplebank = new Bank();
    }

    @SneakyThrows
    @Before
    public void prepare() {
        testy = new BankUser(testy_UUID,testy_NAME, 20,0);
        testy.addBankUserToDB();

        Thread.sleep(3000);
    }

    @SneakyThrows
    @After
    public void cleanup(){
        testy.removeBankUserFromDB();
        Thread.sleep(3000);
    }


    @Test
    public void einzahlen_Erfolgreich() throws InterruptedException {


        CountDownLatch lock = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);

        //execute
        simplebank.cashIn(testy_UUID,15, ia -> {
            assertTrue(ia.isSuccess());
            assertEquals(15, ia.getLog().getAmount(), 0);

            BankUser.asyncBankUserByID(ia.getLog().getUuidFrom(), bankUser -> {
                if(bankUser.getBank() == 15 && bankUser.getCash() == 5)
                    success.set(true);

                lock.countDown();
            });
        });

        lock.await(5000, TimeUnit.MILLISECONDS);

        assertTrue(success.get());
    }
}
