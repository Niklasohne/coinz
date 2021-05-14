package de.ronon_lul.simplecoinz.bank.errorCodes;

public class MultipleSetup extends RuntimeException{

    public MultipleSetup(){
        super("Someone tries to setup the DataBase multiple times!");
    }
}
