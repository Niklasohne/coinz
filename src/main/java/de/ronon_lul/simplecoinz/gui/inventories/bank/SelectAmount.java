package de.ronon_lul.simplecoinz.gui.inventories.bank;

import de.ronon_lul.simplecoinz.SimpleCoinz;
import de.ronon_lul.simplecoinz.bank.databaseClasses.BankUser;
import de.ronon_lul.simplecoinz.gui.GuiManager;
import de.ronon_lul.simplecoinz.gui.inventories.InteractiveInventory;
import de.ronon_lul.simplecoinz.gui.utils.SignMenuFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Objects;

/**
 * abstract GUI for CashIn and CashOut actions
 *
 * [Wallet |   -   |   -   |   -   |text IN|   -   |   -    |   -   | Bank  ]
 * [   -   | -100  |  -10  |  -1   |CURRENT|  +1   |  +10   | +100  |   -   ]
 * [   -   |   -   |   -   |   -   |Confirm|   -   |   -    |   -   | Exit  ]
 *
 */
public abstract class SelectAmount extends InteractiveInventory {

    protected static final int CASHSLOT = 0;
    protected static final int TIPIN = 4;
    protected static final int BANKSLOT = 8;

    protected static final int MINUS100SLOT = 10;
    protected static final int MINUS10SLOT = 11;
    protected static final int MINUS1SLOT = 12;

    protected static final int CURRENTSLOT = 13;

    protected static final int PLUS1SLOT = 14;
    protected static final int PLUS10SLOT = 15;
    protected static final int PLUS100SLOT = 16;

    protected static final int CONFIRM = 22;
    protected static final int EXIT = 26;

    public SelectAmount(Player owner, Inventory inventory) {
        super(owner, inventory);
    }

    @Override
    public String getName() {
        return "  ";
    }

    /**
     * Helper function to Set all items to the right positions
     * @param user data from the user, to setup wallet and bankaccount in the right was
     */
    protected void setup(BankUser user) {
        inventory.setItem(CASHSLOT, GuiManager.getItemfactory().wallet(user.getName(), user.getCash()));
        inventory.setItem(BANKSLOT, GuiManager.getItemfactory().bankAcc(user.getName(), user.getBank()));
        inventory.setItem(MINUS1SLOT, GuiManager.getItemfactory().subX(1));
        inventory.setItem(MINUS10SLOT, GuiManager.getItemfactory().subX(10));
        inventory.setItem(MINUS100SLOT, GuiManager.getItemfactory().subX(100));
        inventory.setItem(PLUS1SLOT, GuiManager.getItemfactory().addX(1));
        inventory.setItem(PLUS10SLOT, GuiManager.getItemfactory().addX(10));
        inventory.setItem(PLUS100SLOT, GuiManager.getItemfactory().addX(100));
        inventory.setItem(CONFIRM, GuiManager.getItemfactory().confirm());
        inventory.setItem(EXIT, GuiManager.getItemfactory().exitSymbol());

        inventory.setItem(TIPIN, GuiManager.getItemfactory().tipin());

        update(inventory, 0);
    }

    /**
     * when the chosen amount changes, this item needs to update too
     * @param inv the inventory where the item needs to get updated
     * @param current the new chosen amount what should get displayed
     */
    protected void update(Inventory inv, double current) {
        inv.setItem(CURRENTSLOT, GuiManager.getItemfactory().current(current));
    }

    /**
     * opens the actual GUI
     * Request Bankuser Informations from the Database and async opens the Inventory for the player when the Data is ready
     */
    @Override
    public void open() {
        if (owner == null)
            return;
        BankUser.asyncBankUserByID(owner.getUniqueId().toString(), user -> {
            setup(user);
            Bukkit.getScheduler().runTask(SimpleCoinz.getMain(),
                    () -> owner.openInventory(inventory));
        });
    }


    /**
     * handles the Click Interaction for all SelectAmount GUIs
     * @param event current click event
     *
     * EXIT -> closInventory
     * plusX / minusX -> change the current chosen amount with delta X
     * TIPIN -> opens an textfield(sign) so you can instant choose the right amount
     * CONFIRM -> confirm your action with the current chosen amount of money
     */
    public boolean standardHandle(InventoryClickEvent event) {
        if (eventIsnull(event, getName()))
            return false;

        Player p = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();

        double current = 0;
        if (inv.getItem(CURRENTSLOT) != null)
            current = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(inv.getItem(CURRENTSLOT)).getItemMeta().getLore()).get(0));


        switch (event.getSlot()) {
            case EXIT:
                p.closeInventory();
                break;
            case PLUS1SLOT:
                update(inv, current + 1);
                break;
            case PLUS10SLOT:
                update(inv, current + 10);
                break;
            case PLUS100SLOT:
                update(inv, current + 100);
                break;
            case MINUS1SLOT:
                update(inv, current - 1 > 0 ? current - 1 : 0);
                break;
            case MINUS10SLOT:
                update(inv, current - 10 > 0 ? current - 10 : 0);
                break;
            case MINUS100SLOT:
                update(inv, current - 10 > 0 ? current - 100 : 0);
                break;
            case TIPIN:
                SignMenuFactory.Menu insert = GuiManager.getSignMenuFactory().newMenu(Arrays.asList("", "---------", "insert the", "amount"))
                        .reopenIfFail(false)
                        .response(((player, result) -> {
                            System.out.println(result[0]);
                            double d = Double.parseDouble(result[0]);
                            System.out.println(d);
                            if (d < 0) {
                                p.sendMessage("number to small");
                                update(inv, 0);
                                p.openInventory(inventory);
                            } else {
                                update(inv,d);
                                Bukkit.getScheduler().runTask(SimpleCoinz.getMain(), () -> p.openInventory(inv));
                            }
                            return true;
                        }
                        ));
                insert.open(p);
                break;
            case CONFIRM: {
                executeAction(p, inv, current);
            }
            default:
                break;

        }
        return true;
    }


    /**
     * because the actual action what you want to do with the chosen money is different for different children
     * you need to define what will happen after pressing "CONFIRM"
     * @param p the player who is using the GUI
     * @param inv the GUI
     * @param current the current chosen amount of money
     */
    protected abstract void executeAction(Player p, Inventory inv, double current);
}
