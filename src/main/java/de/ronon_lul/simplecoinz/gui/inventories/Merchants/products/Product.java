package de.ronon_lul.simplecoinz.gui.inventories.Merchants.products;

import de.ronon_lul.simplecoinz.gui.utils.Itemfactory;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Superclass for everything what a merchant wants to trade
 */
public abstract class Product {

    @Getter protected String customName;
    @Getter protected int price;
    @Getter protected int amount;
    @Getter protected Material material;


    public String buyString(){
        return "you bought " + ChatColor.AQUA + amount + ChatColor.DARK_PURPLE + " " + customName + ChatColor.RESET + " for " + price + "C !";
    }

    /**
     * standard constructor to generate any Product
     * @param mat The Material the Product is of
     * @param customName the name of the Product
     * @param price how much the product should cost
     * @param amount how many you get when you buy
     */
    public Product(Material mat, String customName, int price, int amount) {
        this.material = mat;
        this.customName = customName;
        this.price = price;
        this.amount = amount;
    }

    /**
     * generates the final ItemStack
     * @return the final ItemStack
     */
    public ItemStack generateItem() {
        return new Itemfactory.ItemBuilder(material)
                .setAmount(amount)
                .setName(customName)
                .setLore(price + "C")
                .build();
    }
}
