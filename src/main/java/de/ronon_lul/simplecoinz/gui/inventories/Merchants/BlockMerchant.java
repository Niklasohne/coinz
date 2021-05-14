package de.ronon_lul.simplecoinz.gui.inventories.Merchants;

import de.ronon_lul.simplecoinz.gui.inventories.Merchants.products.Blocks.Dirt;
import de.ronon_lul.simplecoinz.gui.inventories.Merchants.products.Blocks.FancyDirt;
import de.ronon_lul.simplecoinz.gui.inventories.Merchants.products.Product;
import de.ronon_lul.simplecoinz.gui.inventories.Merchants.products.Blocks.Stone;
import de.ronon_lul.simplecoinz.npc.NPCTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;


/**
 * The BlockMerchant is selling multiple Blocks
 */
public class BlockMerchant extends Merchant {

    private static final String name = NPCTypes.BLOCKMERCHANT.name();

    public BlockMerchant(Player owner) {
        super(owner, Bukkit.createInventory(owner, 54, Component.text(name)), new Product[]{
                new Dirt(),
                new Stone(),
                new FancyDirt()
        });
    }

    @Override
    public String getName() {
        return name;
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
