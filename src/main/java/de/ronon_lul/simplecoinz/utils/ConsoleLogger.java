package de.ronon_lul.simplecoinz.utils;

import java.util.function.Consumer;

public class ConsoleLogger {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_CYAN = "\033[0;36m";

    private final String name;
    private final Consumer<String> logMethode;

    /**
     * HelperClass to make Logging easier
     * @param name Name of the logged Component
     * @param logMethode How to log (Console / File / etc.)
     */
    public ConsoleLogger(String name, Consumer<String> logMethode){
        this.name = "[" + name + "] ";
        this.logMethode = logMethode;
    }

    /**
     * Print an Warning
     * @param text the warning
     */
    public void warning(String text){
        logMethode.accept(ANSI_YELLOW + name + text + ANSI_RESET);
    }

    /**
     * Print an Error
     * @param text the Error
     */
    public void error(String text){
        logMethode.accept(ANSI_RED + name + text + ANSI_RESET);
    }

    /**
     * Print an simple Log
     * @param text the Log
     */
    public void log(String text){
        logMethode.accept(ANSI_CYAN + name + text + ANSI_RESET);
    }
}
