package de.ronon_lul.simplecoinz.utils;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin){
        this.plugin = plugin;

        if(getDbUrl() == null || getDbUrl().equals("") || getDbUrl().equals("insert your URL here")){
            plugin.saveDefaultConfig();
            System.out.println("please configure your DB connection");
        }
    }




    public String getDbUrl(){
       return plugin.getConfig().getString("mongodbUrl");
    }

    public String getName(){
        return plugin.getConfig().getString("mongodbName");
    }
}
