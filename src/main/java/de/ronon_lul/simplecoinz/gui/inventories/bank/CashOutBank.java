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
 * An Gui Inventory where the player can transfer Money from his Bankaccount to his wallet
 */
public class CashOutBank extends SelectAmount {

    public CashOutBank(Player owner) {
        super(owner, Bukkit.createInventory(owner, InventoryType.CHEST, Component.text("CASH OUT")));
    }


    /**
     * tries to Cash out the amount the player selected from the players Bank Account
     * The Player receives an Confirmation if the transaction was successful
     *
     * @param p       The player who executed the Action
     * @param inv     The CashInBank Inventory from the player
     * @param current the amount the player wants to cash out from the Bank account
     */
    @Override
    protected void executeAction(Player p, Inventory inv, double current) {
        Bank.cashOut(p.getUniqueId().toString(), current, interactionAnswer -> Bukkit.getScheduler().runTask(SimpleCoinz.getMain(), () -> {
            p.closeInventory();
            new Confirmation(p, interactionAnswer.isSuccess(), interactionAnswer.getMessage()).open();
        }));
    }

    /**
     * @return the inventory name
     */
    @Override
    public String getName() {
        return "CASH OUT";
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

