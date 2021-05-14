package de.ronon_lul.simplecoinz.gui.inventories.bank;

import de.ronon_lul.simplecoinz.SimpleCoinz;
import de.ronon_lul.simplecoinz.bank.databaseClasses.BankUser;
import de.ronon_lul.simplecoinz.bank.databaseClasses.TransferLog;
import de.ronon_lul.simplecoinz.gui.GuiManager;
import de.ronon_lul.simplecoinz.gui.inventories.InteractiveInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * the general Gui for the Bank
 *
 * [wallet |   -   |   -   |   -   |   -   |   -   |   -    |   -   | bank  ]
 * [   -   |   -   |CashIN |   -   |CashOut|   -   |Transfer|   -   |  -    ]
 * [ logs  |   -   |   -   |   -   |   -   |   -   |   -    |   -   | exit  ]
 */
public class OverviewBank extends InteractiveInventory {

    private static final int WALLETSLOT = 0;
    private static final int BANKACCSLOT = 8;
    private static final int CASHINSLOT = 11;
    private static final int CASHOUTSLOT = 13;
    private static final int TRANSFERSLOT = 15;
    private static final int EXITSLOT = 26;
    private static final int RECORDS = 18;


    public OverviewBank(Player owner) {
        super(owner, Bukkit.createInventory(owner, InventoryType.CHEST, Component.text("Overview")));
    }

    /**
     * Helper function to Set all items to the right positions
     * @param user data from the user, to setup wallet and bankaccount in the right was
     */
    private void setup(BankUser user) {
        inventory.setItem(WALLETSLOT, GuiManager.getItemfactory().wallet(user.getName(), user.getCash()));
        inventory.setItem(BANKACCSLOT, GuiManager.getItemfactory().bankAcc(user.getName(), user.getBank()));
        inventory.setItem(CASHINSLOT, GuiManager.getItemfactory().cashInSymbol());
        inventory.setItem(CASHOUTSLOT, GuiManager.getItemfactory().cashOutSymbol());
        inventory.setItem(TRANSFERSLOT, GuiManager.getItemfactory().transferSymbol());
        inventory.setItem(EXITSLOT, GuiManager.getItemfactory().exitSymbol());
    }


    /**
     * @return the inventory name
     */
    @Override
    public String getName() {
        return "Overview";
    }

    /**
     * opens the UI for the "owner" Player
     * Request Bankuser Information from the Database and async opens the Inventory for the player when the Data is ready
     */
    @Override
    public void open() {
        if (owner == null)
            return;

        TransferLog.getTransfersByUUID(owner.getUniqueId().toString(), transferLogs -> {
            inventory.setItem(RECORDS, GuiManager.getItemfactory().transferRecord(owner.getUniqueId().toString(), transferLogs));

            BankUser.asyncBankUserByID(owner.getUniqueId().toString(), user -> {
                setup(user);
                Bukkit.getScheduler().runTask(SimpleCoinz.getMain(), () -> owner.openInventory(inventory));
            });

        });
    }

    /**
     * handles the Click Interactions for this GUI
     * @param event current click event
     *
     * Exit -> close the inventory
     * Other -> use the selected Menu
     */
    @Override
    @EventHandler
    public void handler(InventoryClickEvent event) {
        if (eventIsnull(event, getName()))
            return;

        Player p = (Player) event.getWhoClicked();

        switch (event.getSlot()) {
            case EXITSLOT:
                p.closeInventory();
                break;
            case CASHINSLOT:
                p.closeInventory();
                new CashInBank(p).open();
                break;
            case CASHOUTSLOT:
                p.closeInventory();
                new CashOutBank(p).open();
                break;
            case TRANSFERSLOT:
                p.closeInventory();
                new TransferBank(p).open();
                break;
        }

    }
}
