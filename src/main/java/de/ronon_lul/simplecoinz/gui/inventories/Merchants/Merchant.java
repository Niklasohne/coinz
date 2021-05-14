package de.ronon_lul.simplecoinz.gui.inventories.Merchants;

import de.ronon_lul.simplecoinz.bank.databaseClasses.BankUser;
import de.ronon_lul.simplecoinz.bank.errorCodes.NotEnoughMoney;
import de.ronon_lul.simplecoinz.gui.inventories.InteractiveInventory;
import de.ronon_lul.simplecoinz.gui.inventories.Merchants.products.Product;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

/**
 * standard Merchant gui
 * <p>
 * only varies on what stuff they sell
 */
public abstract class Merchant extends InteractiveInventory {
    private final Product[] stuff;

    public Merchant(Player owner, Inventory inv, Product[] stuff) {
        super(owner, inv);
        this.stuff = stuff;
    }

    /**
     * opens th GUI
     * shows everything the merchant is selling , hte wallet and an exit-sign
     */
    @Override
    public void open() {
        Arrays.stream(stuff).forEach(product -> inventory.addItem(product.generateItem()));

        owner.openInventory(inventory);
    }


    /**
     * handles User input
     * when pressing any item -> buy it
     *
     * @param event the InventoryClickEvent
     */
    @Override
    @EventHandler
    public void handler(InventoryClickEvent event) {
        if (eventIsnull(event, getName()))
            return;

        Player player = (Player) event.getWhoClicked();

        Product product = event.getSlot() < stuff.length ? stuff[event.getSlot()] : null;

        if (product == null)
            return;

        buy(player, product);
    }


    /**
     * tries to buy an item for the player
     * quits if the player has not enough space in the inventory or not enough money
     * @param player who wants to buy something
     * @param product he wants to buy
     */
    private static void buy(Player player, Product product) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage("you dont have enough space in your inventory");
            return;
        }
        BankUser.asyncBankUserByID(player.getUniqueId().toString(), bankUser -> {
            try {
                bankUser.spendCash(product.getPrice());
                player.getInventory().addItem(product.generateItem());
                player.sendMessage(product.buyString());
            } catch (NotEnoughMoney e) {
                player.sendMessage("you dont have enough money");
            }
        });
    }
}
