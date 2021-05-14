package de.ronon_lul.simplecoinz.gui.utils;

import de.ronon_lul.simplecoinz.bank.databaseClasses.TransferLog;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory to build GUI elements
 */
public class Itemfactory {

    public ItemStack wallet(String name, double amount) {
        return new ItemBuilder(Material.LEATHER)
                .setName(name + " 's Wallet ")
                .setLore(amount + " C")
                .build();
    }

    public ItemStack bankAcc(String name, double amount) {
        return new ItemBuilder(Material.GOLD_BLOCK)
                .setName(name + "'s BankAccount")
                .setLore(amount + " C")
                .build();
    }

    public ItemStack transferRecord(String uuid, List<TransferLog> records) {
        List<String> lore = new ArrayList<>();
        if(records.size() > 10)
            records = records.subList(records.size()-10, records.size());
        Collections.reverse(records);
        for (TransferLog l : records)
            lore.add(l.fancyString(uuid));
        return new ItemBuilder(Material.WRITABLE_BOOK)
                .setName("Records")
                .setLore(lore)
                .build();
    }


    public ItemStack cashInSymbol() {
        return new ItemBuilder(Material.GREEN_DYE)
                .setName("Cash In")
                .build();
    }

    public ItemStack cashOutSymbol() {
        return new ItemBuilder(Material.RED_DYE)
                .setName("Cash Out")
                .build();
    }

    public ItemStack transferSymbol() {
        return new ItemBuilder(Material.CYAN_DYE)
                .setName("Send another Player money")
                .build();
    }

    public ItemStack exitSymbol() {
        return new ItemBuilder(Material.BARRIER)
                .setName("EXIT")
                .build();

    }

    public ItemStack addX(int amount){
        return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                .setName(ChatColor.GREEN + "Add " + amount)
                .build();
    }

    public ItemStack subX(int amount){
        return new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setName(ChatColor.RED + "Sub " + amount)
                .build();
    }

    public ItemStack confirm(){
        return new ItemBuilder(Material.GREEN_BANNER)
                .setName("Confirm")
                .build();
    }

    public ItemStack success(){
        return new ItemBuilder(Material.GREEN_CARPET)
                .setName("DONE")
                .build();
    }

    public ItemStack fail(String lore){
        return new ItemBuilder(Material.RED_CARPET)
                .setName("ERROR")
                .setLore(Arrays.asList(lore, "", "Please try again later"))
                .build();
    }


    public ItemStack current(double amount){
        return new ItemBuilder(Material.GOLD_INGOT)
                .setName("Currently: ")
                .setLore(""+amount)
                .build();
    }

    public ItemStack tipin(){
        return new ItemBuilder(Material.SPRUCE_SIGN)
                .setName("Set specific amount")
                .build();
    }


    public static class ItemBuilder{
        private final ItemStack i;

        public ItemBuilder(Material mat){
            i = new ItemStack(mat);
        }

        public ItemBuilder setAmount(int val){
            i.setAmount(val);
            return this;
        }

        public ItemBuilder setName(String name){
            ItemMeta meta = i.getItemMeta();
            meta.displayName(Component.text(name));
            i.setItemMeta(meta);
            return this;
        }

        public ItemBuilder setLore(String text){
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(text));
            i.lore(lore);
            return this;
        }

        public ItemBuilder setLore(List<String> text){
            i.lore(text.stream().map(Component::text).collect(Collectors.toList()));
            return this;
        }



        public ItemStack build(){
            return i;
        }
    }

}
