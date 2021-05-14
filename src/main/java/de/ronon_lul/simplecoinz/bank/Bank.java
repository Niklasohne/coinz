package de.ronon_lul.simplecoinz.bank;

import de.ronon_lul.simplecoinz.bank.databaseClasses.BankUser;
import de.ronon_lul.simplecoinz.bank.databaseClasses.TransferLog;
import de.ronon_lul.simplecoinz.bank.errorCodes.NotEnoughMoney;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Consumer;

public class Bank {

    @AllArgsConstructor
    static public class InteractionAnswer {
        @Getter
        private final boolean Success;
        @Getter
        private final String message;
        @Getter
        private final TransferLog log;
    }

    public static void transfer(String uuidSender, String uuidGetter, double amount) {
        transfer(uuidSender, uuidGetter, amount, result -> {
        });
    }

    public static void transfer(String uuidSender, String uuidGetter, double amount, Consumer<InteractionAnswer> extendedHandler) {
        BankUser.asyncBankUserByID(uuidSender, sender ->
                BankUser.asyncBankUserByID(uuidGetter, getter -> {
                    try {
                        sender.transfer(amount, getter);
                        TransferLog log = TransferLog.transaction(uuidSender, uuidGetter, amount);
                        extendedHandler.accept(new InteractionAnswer(true, "success", log));
                    } catch (NotEnoughMoney e) {
                        extendedHandler.accept(new InteractionAnswer(false, e.getMessage(), null));
                    }
                }));
    }


    public static void cashIn(String uuidPLayer, double amount) {
        cashIn(uuidPLayer, amount, result -> {
        });
    }

    public static void cashIn(String uuidPLayer, double amount, Consumer<InteractionAnswer> extendedHandler) {
        BankUser.asyncBankUserByID(uuidPLayer, player -> {
            System.out.println(player);

            try {
                player.cashIn(amount);
                TransferLog log = TransferLog.cashIn(uuidPLayer, amount);
                extendedHandler.accept(new InteractionAnswer(true, "success", log));
            } catch (NotEnoughMoney e) {
                extendedHandler.accept(new InteractionAnswer(false, e.getMessage(), null));
            }
        });
    }


    public static void cashOut(String uuidPlayer, double amount, Consumer<InteractionAnswer> extendedHandler) {
        BankUser.asyncBankUserByID(uuidPlayer, player -> {
            try {
                player.cashOut(amount);
                TransferLog log = TransferLog.cashOut(uuidPlayer, amount);
                extendedHandler.accept(new InteractionAnswer(true, "success", log));
            } catch (NotEnoughMoney e) {
                extendedHandler.accept(new InteractionAnswer(false, e.getMessage(), null));
            }
        });
    }
}
