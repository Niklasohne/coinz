package de.ronon_lul.simplecoinz.bank.errorCodes;

public class NotEnoughMoney extends  Exception{

    public NotEnoughMoney(){
        super("there was not enough money for this transaction");
    }
}
