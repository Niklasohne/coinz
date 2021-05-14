package de.ronon_lul.simplecoinz.gui.inventories;

import de.ronon_lul.simplecoinz.gui.GuiManager;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * Simple Confirmation GUI
 * The User can see a TOP/FLOP if something worked or not. and if not, why
 */
public class Confirmation extends InteractiveInventory {

    private static final String name = "Confirmation";

    private static final int CONFIRMATION_SLOT = 2;
    private static final int EXIT = 4;

    private final boolean success;
    private final String msg;

    public Confirmation(Player owner, boolean success, String msg) {
        super(owner, Bukkit.createInventory(owner, InventoryType.HOPPER, Component.text(name)));
        this.success = success;
        this.msg = msg;
    }

    @Override
    public String getName() {
        return name;
    }


    /**
     * opens th GUI
     * depending on the success boolean it will tell the player everything went good, or what went wrong
     */
    @Override
    public void open() {
        inventory.setItem(
                CONFIRMATION_SLOT,
                success ? GuiManager.getItemfactory().success()
                        : GuiManager.getItemfactory().fail(msg)
        );

        inventory.setItem(EXIT, GuiManager.getItemfactory().exitSymbol());
        owner.openInventory(inventory);
    }


    /**
     * handles User input
     * when pressing any "used" slot -> exit the menu
     *
     * @param event the InventoryClickEvent
     */
    @Override
    @EventHandler
    public void handler(InventoryClickEvent event) {
        if (eventIsnull(event, getName()))
            return;
        Player p = (Player) event.getWhoClicked();

        switch (event.getSlot()) {
            case CONFIRMATION_SLOT:
            case EXIT:
                p.closeInventory();
                break;
        }
    }
}
