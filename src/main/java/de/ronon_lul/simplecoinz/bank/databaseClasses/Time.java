package de.ronon_lul.simplecoinz.bank.databaseClasses;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple Time representation to save /load Time to/from the Database
 */
public class Time {

    private LocalDateTime time;
    private static final String format = "yyyy.MM.dd_HH:mm:ss";
    private static final DateTimeFormatter formatter =  DateTimeFormatter.ofPattern(format);


    private Time(String string){

        time = LocalDateTime.parse(string, formatter);
    }
    private Time(){
    }

    /**
     * Generate a Time Object for the current Time
     * @return Time
     */
    public static Time current(){
        Time t = new Time();
        t.time = LocalDateTime.now();
        return t;
    }

    /**
     * Generate a Time Object from a string, compatible with @getAsString
     * @param src the String with the time in it
     * @return Time
     */
    public static Time fromString(String src){
        return new Time(src);
    }

    @Override
    public String toString(){
        return time.format(formatter);
    }

}
