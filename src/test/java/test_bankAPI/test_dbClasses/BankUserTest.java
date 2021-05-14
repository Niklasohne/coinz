package test_bankAPI.test_dbClasses;

import de.ronon_lul.simplecoinz.bank.databaseClasses.BankUser;
import de.ronon_lul.simplecoinz.bank.errorCodes.NotEnoughMoney;
import de.ronon_lul.simplecoinz.utils.Database.MongoDBConnector;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

public class BankUserTest {

    static MockedStatic<MongoDBConnector> mockedMongoDbConnector;
    static BankUser bankusermockA = spy(BankUser.class);
    static BankUser bankusermockB = spy(BankUser.class);
    static Field cashField;
    static Field bankField;
    static Field uuidField;
    static Field nameField;

    @BeforeClass
    public static void setup(){
        //make needed Fields accesible
        try {
            cashField = BankUser.class.getDeclaredField("cash");
            cashField.setAccessible(true);
            bankField = BankUser.class.getDeclaredField("bank");
            bankField.setAccessible(true);
            uuidField = BankUser.class.getDeclaredField("uuid");
            uuidField.setAccessible(true);
            nameField = BankUser.class.getDeclaredField("name");
            nameField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        //Mock Database interaction
        doNothing().when(bankusermockA).addBankUserToDB();
        doNothing().when(bankusermockA).updateBankUserToDB();

        doNothing().when(bankusermockB).addBankUserToDB();
        doNothing().when(bankusermockB).updateBankUserToDB();
    }

    @Test
    public void cashIn_enough(){
        try {
            cashField.set(bankusermockA,20);
            bankField.set(bankusermockA,0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            bankusermockA.cashIn(10);
        } catch (NotEnoughMoney notEnoughMoney) {
            notEnoughMoney.printStackTrace();
            fail();
        }

        assertEquals(10, bankusermockA.getCash(), 0);
        assertEquals(10, bankusermockA.getBank(),0);
    }

    @Test
    public void cashIn_notenough(){
        try {
            cashField.set(bankusermockA,20);
            bankField.set(bankusermockA,0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            bankusermockA.cashIn(30);
        }catch (NotEnoughMoney e){
            assertEquals(20, bankusermockA.getCash(), 0);
            assertEquals(0, bankusermockA.getBank(),0);
            assert true;
        }
    }

    @Test
    public void cashOut_enough(){
        try {
            cashField.set(bankusermockA,0);
            bankField.set(bankusermockA,20);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            bankusermockA.cashOut(10);
        } catch (NotEnoughMoney notEnoughMoney) {
            notEnoughMoney.printStackTrace();
            fail();
        }

        assertEquals(10, bankusermockA.getBank(), 0);
        assertEquals(10, bankusermockA.getCash(),0);
    }

    @Test
    public void cashOut_notenough(){
        try {
            cashField.set(bankusermockA,0);
            bankField.set(bankusermockA,20);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            bankusermockA.cashOut(30);
        }catch (NotEnoughMoney e){
            assertEquals( 20, bankusermockA.getBank(), 0);
            assertEquals(0, bankusermockA.getCash(),0);
        }
    }


    @Test
    public void receiveCash(){
        try {
            cashField.set(bankusermockA,0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        bankusermockA.receiveCash(42);
        assertEquals(42,bankusermockA.getCash(), 0);
    }

    @Test
    public void spendCash_enough(){
        try {
            cashField.set(bankusermockA,50);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            bankusermockA.spendCash(42);
        } catch (NotEnoughMoney notEnoughMoney) {
            fail();
        }

        assertEquals(8, bankusermockA.getCash(), 0);
    }

    @Test
    public void spendCash_notenough(){
        try {
            cashField.set(bankusermockA,10);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        try {
            bankusermockA.spendCash(42);
        } catch (NotEnoughMoney notEnoughMoney) {
            notEnoughMoney.printStackTrace();
        }

        assertEquals(10, bankusermockA.getCash(), 0);
    }

    @Test
    public void transfer(){
        try {
            bankField.set(bankusermockA,50);
            bankField.set(bankusermockB,0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            bankusermockA.transfer(25, bankusermockB);
        } catch (NotEnoughMoney notEnoughMoney) {
            fail();
        }

        assertEquals(25, bankusermockA.getBank(), 0);
        assertEquals(25, bankusermockB.getBank(), 0);
    }

}
