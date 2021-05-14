package test_bankAPI.test_dbClasses;

import de.ronon_lul.simplecoinz.bank.databaseClasses.Time;
import org.junit.Test;


import static org.junit.Assert.*;

public class TimeTest {


    @Test
    public void timeFormatter(){
        Time tmp = Time.current();

        System.out.println(tmp.toString());
        String timeString = "2021.05.04_17:56:43";
        Time setTime = Time.fromString(timeString);
        assertEquals(timeString, setTime.toString());
    }
}
