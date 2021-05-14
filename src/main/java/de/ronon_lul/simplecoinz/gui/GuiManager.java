package de.ronon_lul.simplecoinz.gui;

import de.ronon_lul.simplecoinz.gui.inventories.*;
import de.ronon_lul.simplecoinz.gui.inventories.Merchants.Blacksmith;
import de.ronon_lul.simplecoinz.gui.inventories.Merchants.BlockMerchant;
import de.ronon_lul.simplecoinz.gui.inventories.bank.CashInBank;
import de.ronon_lul.simplecoinz.gui.inventories.bank.CashOutBank;
import de.ronon_lul.simplecoinz.gui.inventories.bank.OverviewBank;
import de.ronon_lul.simplecoinz.gui.inventories.bank.TransferBank;
import de.ronon_lul.simplecoinz.gui.utils.Itemfactory;
import de.ronon_lul.simplecoinz.gui.utils.SignMenuFactory;
import org.bukkit.plugin.Plugin;

/**
 * Manager for all GUI objects
 */
public class GuiManager {

    private static SignMenuFactory signMenuFactory;
    private static Itemfactory itemfactory;
    private static Plugin plugin;


    public static SignMenuFactory getSignMenuFactory() {
        return signMenuFactory;
    }

    public static Itemfactory getItemfactory() {
        return itemfactory;
    }

    /**
     * Add all handlers for GUI interaction to the plugin
     *
     * @param _plugin main plugin
     */
    public static void setupGuiManager(Plugin _plugin){
        plugin = _plugin;
        signMenuFactory = new SignMenuFactory(plugin);
        itemfactory = new Itemfactory();

        //Bank
        addGuiEventHandler(new OverviewBank(null));
        addGuiEventHandler(new CashInBank(null));
        addGuiEventHandler(new CashOutBank(null));
        addGuiEventHandler(new TransferBank(null));

        //Merchant
        addGuiEventHandler(new BlockMerchant(null));
        addGuiEventHandler(new Blacksmith(null));

        //standard
        addGuiEventHandler(new Confirmation(null,false,""));

    }

    /**
     * Helper function
     */
    private  static <T extends InteractiveInventory> void addGuiEventHandler(T eventHandler){
        plugin.getServer().getPluginManager().registerEvents(eventHandler, plugin);
    }
}
