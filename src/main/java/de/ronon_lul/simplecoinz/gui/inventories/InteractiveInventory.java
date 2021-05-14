package de.ronon_lul.simplecoinz.gui.inventories;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * the SuperClass for every GUI
 */
public abstract class InteractiveInventory implements Listener {

    protected Player owner;
    protected Inventory inventory;

    public InteractiveInventory(Player owner, Inventory inventory){
        this.owner = owner;
        this.inventory = inventory;
    }

    /**
     * get the name of the inventory, used to distinguish in which GUI the player is currently and how to react
     * @return the Name of the inventory
     */
    public abstract String getName();

    /**
     * build and open an individual GUI
     */
    public abstract void open();

    /**
     * helper method, checks if the triggered InventoryClickEvent is for the current GUI
     * @param event the click event itself
     * @param name the name of the current GUI
     * @return if the Event should get canceled
     */
    protected static boolean eventIsnull(InventoryClickEvent event, String name){
        if (!event.getView().getTitle().equals(name)) return true;
        if (event.getCurrentItem() == null) return true;
        if (event.getCurrentItem().getItemMeta() == null) return true;
        if (event.getCurrentItem().getItemMeta().displayName() == null) return true;
        event.setCancelled(true);

        return false;
    }

    /**
     * here the subclasses need to define how the GUI has to react to userInput
     * @param event the InventoryClickEvent
     */
    @EventHandler
    public abstract void handler(InventoryClickEvent event);

}
