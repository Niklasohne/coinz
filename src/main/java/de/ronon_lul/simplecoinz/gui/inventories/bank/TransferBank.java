package de.ronon_lul.simplecoinz.gui.inventories.bank;

import de.ronon_lul.simplecoinz.SimpleCoinz;
import de.ronon_lul.simplecoinz.bank.Bank;
import de.ronon_lul.simplecoinz.bank.databaseClasses.BankUser;
import de.ronon_lul.simplecoinz.gui.GuiManager;
import de.ronon_lul.simplecoinz.gui.inventories.Confirmation;
import de.ronon_lul.simplecoinz.gui.utils.HeadFactory;
import de.ronon_lul.simplecoinz.gui.utils.SignMenuFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Objects;

public class TransferBank extends SelectAmount{


    private static final String name = "transfer";
    protected static final int SelectUser = 3;


    public TransferBank(Player owner) {
        super(owner, Bukkit.createInventory(owner, InventoryType.CHEST, Component.text(name)));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void setup(BankUser user) {
        super.setup(user);
        inventory.setItem(SelectUser, HeadFactory.select());
    }

    @EventHandler
    @Override
    public void handler(InventoryClickEvent event) {
        if(!super.standardHandle(event))
            return;

        if(event.getSlot() == SelectUser) {
            Inventory inv = event.getInventory();
            Player p = (Player) event.getWhoClicked();
            SignMenuFactory.Menu getPlayer = GuiManager.getSignMenuFactory().newMenu(Arrays.asList("", "---------", "insert the", "player"))
                    .reopenIfFail(false)
                    .response(((player, result) -> {
                        if (Bukkit.getOfflinePlayerIfCached(result[0]) == null) {
                            inv.setItem(SelectUser, HeadFactory.playerNotFount());
                        } else {
                            inv.setItem(SelectUser,HeadFactory.playerHead(result[0]));
                        }
                        Bukkit.getScheduler().runTask(SimpleCoinz.getMain(), () ->p.openInventory(inv));
                        return true;
                    }
                    ));
            getPlayer.open(p);
        }
    }

    @Override
    protected void executeAction(Player p, Inventory inv, double current) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(Objects.requireNonNull(inv.getItem(SelectUser)).getItemMeta().getDisplayName());
        assert offlinePlayer != null;
        Bank.transfer(p.getUniqueId().toString(), offlinePlayer.getUniqueId().toString(), current, interactionAnswer -> Bukkit.getScheduler().runTask(
                SimpleCoinz.getMain(),
                () -> {
                    p.closeInventory();
                    new Confirmation(p,interactionAnswer.isSuccess(), interactionAnswer.getMessage()).open();
                }
        ));
    }
}
