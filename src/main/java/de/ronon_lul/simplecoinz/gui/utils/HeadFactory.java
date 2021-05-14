package de.ronon_lul.simplecoinz.gui.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HeadFactory {

    public static ItemStack playerNotFount(){
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner("TheNewTsar");
        meta.displayName(Component.text("404 - Player is not active on this server"));
        stack.setItemMeta(meta);

        return stack;
    }

    public static ItemStack select(){
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner("vvvvvvvvvvvv");
        meta.displayName(Component.text("Select the receiver of the Transaction"));
        stack.setItemMeta(meta);

        return stack;
    }

    public static ItemStack playerHead(String name){
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(name);
        meta.displayName(Component.text(name));
        stack.setItemMeta(meta);

        return stack;
    }



}