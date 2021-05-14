package de.ronon_lul.simplecoinz.gui.inventories.bank;

import de.ronon_lul.simplecoinz.SimpleCoinz;
import de.ronon_lul.simplecoinz.bank.Bank;
import de.ronon_lul.simplecoinz.gui.inventories.Confirmation;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * An Gui Inventory where the player can transfer Money from his Wallet to his bankaccount
 */
public class CashInBank extends SelectAmount {

    private final static String name = "CASH IN";

    public CashInBank(Player owner) {
        super(owner, Bukkit.createInventory(owner, InventoryType.CHEST, Component.text(name)));
    }

    /**
     * tries to Cash in the amount the player selected to the players Bank Account
     * The Player receives an Confirmation if the transaction was successful
     *
     * @param p       The player who executed the Action
     * @param inv     The CashInBank Inventory from the player
     * @param current the amount the player wants to cash in into the Bank account
     */
    @Override
    protected void executeAction(Player p, Inventory inv, double current) {
        Bank.cashIn(p.getUniqueId().toString(), current, interactionAnswer -> Bukkit.getScheduler().runTask(
                SimpleCoinz.getMain(),
                () -> {
                    p.closeInventory();
                    new Confirmation(p, interactionAnswer.isSuccess(), interactionAnswer.getMessage()).open();
                }
        ));
    }

    /**
     * @return the inventory name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * needed because EventHandler with Superclasses dont work otherwise
     * See in SelectAmount
     */
    @Override
    @EventHandler
    public void handler(InventoryClickEvent event) {
        super.standardHandle(event);
    }
}

