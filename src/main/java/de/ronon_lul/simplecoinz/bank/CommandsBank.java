package de.ronon_lul.simplecoinz.bank;

import de.ronon_lul.simplecoinz.bank.databaseClasses.BankUser;
import de.ronon_lul.simplecoinz.bank.errorCodes.NotEnoughMoney;
import de.ronon_lul.simplecoinz.gui.inventories.bank.OverviewBank;
import de.ronon_lul.simplecoinz.utils.ConsoleLogger;
import org.bukkit.entity.Player;

public class CommandsBank {

    private final ConsoleLogger logger;

    public CommandsBank(ConsoleLogger logger) {
        this.logger = logger;
    }


    public boolean showBank(Player player) {
        new OverviewBank(player).open();
        return true;
    }

    public boolean receive(Player player, String[] args) {
        try {
            double amount = Double.parseDouble(args[0]);
            BankUser.asyncBankUserByID(player.getUniqueId().toString(), bankUser -> bankUser.receiveCash(amount));
            player.sendMessage("you successfully got " + amount + " C");
        } catch (NumberFormatException e) {
            player.sendMessage("this was not a number");
        }
        return true;
    }

    public boolean spend(Player player, String[] args) {
        if(args.length != 1){
            player.sendMessage("you need 1 argument!");
            return false;
        }
        try {
            double amount = Double.parseDouble(args[0]);
            BankUser.asyncBankUserByID(player.getUniqueId().toString(), bankUser -> {
                try {
                    bankUser.spendCash(amount);
                    player.sendMessage("you successfully got " + amount + " C");
                } catch (NotEnoughMoney notEnoughMoney) {
                    player.sendMessage("you dont have enough cash with you !");
                    logger.warning(notEnoughMoney.getMessage() + "( " + player.getName() + " )");
                }
            });
        } catch (NumberFormatException e) {
            player.sendMessage("this was not a number");
        }
        return true;
    }

    public boolean cashIn(Player player, String[] args) {
        if(args.length != 1){
            player.sendMessage("you need 1 argument!");
            return false;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("this was not a number");
            return true;
        }
        Bank.cashIn(player.getUniqueId().toString(), amount, interactionAnswer -> {
            if (interactionAnswer.isSuccess())
                player.sendMessage("Successful CashIn " + amount + "C");
            else
                player.sendMessage("You dont have enough Cash with you!");
        });
        return true;
    }

    public boolean cashOut(Player player, String[] args) {
        if(args.length != 1){
            player.sendMessage("you need 1 argument!");
            return false;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("this was not a number");
            return true;
        }

        Bank.cashOut(player.getUniqueId().toString(),amount,interactionAnswer -> {
            if(interactionAnswer.isSuccess())
                player.sendMessage("Successful CashOut " + amount + "C");
            else
                player.sendMessage("You dont have enough Money on the Bank!");
        });
        return true;
    }

}
