package de.ronon_lul.simplecoinz.gui;

import de.ronon_lul.simplecoinz.SimpleCoinz;
import de.ronon_lul.simplecoinz.bank.databaseClasses.BankUser;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class BankScoreBoard {

    private BankUser bankUser;

    private final Team bankAmount;
    private final Team cashAmount;

    public BankScoreBoard(Player p) {

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective obj = board.registerNewObjective(
                "Coinz_stuff",
                "dummy",
                Component.text(ChatColor.AQUA + "Coinz")
        );

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score filler1 = obj.getScore(ChatColor.GRAY + " ");
        filler1.setScore(15);

        bankAmount = board.registerNewTeam("bankAmount");
        bankAmount.addEntry(ChatColor.GREEN + "");
        obj.getScore(ChatColor.GREEN + "").setScore(14);

        cashAmount = board.registerNewTeam("cashAmount");
        cashAmount.addEntry(ChatColor.RED + "");
        obj.getScore(ChatColor.RED + "").setScore(12);


        p.setScoreboard(board);

        BankUser.asyncBankUserByID(p.getUniqueId().toString(),
                tmpBankUser -> {
                    this.bankUser = tmpBankUser;
                    Bukkit.getScheduler().runTaskTimer(
                            SimpleCoinz.getMain(),
                            this::update,
                            0,
                            40
                    );
                }
        );
    }


    private void update() {
        BankUser.asyncBankUserByID(bankUser.getUuid(),
                tmpBankUser -> {
                    bankUser = tmpBankUser;
                    bankAmount.prefix(Component.text(ChatColor.GRAY + "Bank  :  " + ChatColor.GREEN + bankUser.getBank() + ChatColor.GRAY + " C"));
                    cashAmount.prefix(Component.text(ChatColor.GRAY + "Wallet :  " + ChatColor.GREEN + bankUser.getCash() + ChatColor.GRAY + " C"));
                }

        );
    }
}
