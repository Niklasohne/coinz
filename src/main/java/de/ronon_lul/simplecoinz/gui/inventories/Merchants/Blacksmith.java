package de.ronon_lul.simplecoinz.gui.inventories.Merchants;

import de.ronon_lul.simplecoinz.gui.inventories.Merchants.products.Product;
import de.ronon_lul.simplecoinz.gui.inventories.Merchants.products.Swords.LameSword;
import de.ronon_lul.simplecoinz.gui.inventories.Merchants.products.Swords.EpicSword;
import de.ronon_lul.simplecoinz.npc.NPCTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * The blacksmith is selling swords
 */
public class Blacksmith extends Merchant {

    private static final String name = NPCTypes.BLACKSMITH.name();

    public Blacksmith(Player owner) {
        super(owner, Bukkit.createInventory(owner, 54, Component.text(name)), new Product[]{
                new LameSword(),
                new LameSword(),
                new LameSword(),
                new LameSword(),
                new LameSword(),
                new EpicSword()
        });
    }

    @Override
    public String getName() {
        return null;
    }

    /**
     * needed because otherwise InventoryClickEvent would not get caught in the right way
     *
     * @param event the InventoryClickEvent
     */
    @Override
    @EventHandler
    public void handler(InventoryClickEvent event) {
        super.handler(event);
    }
}
