package com.payment.paymentSystem.enums;

public enum Currency {
    GEL("Georgian Lari", "₾"),
    USD("US Dollar", "$"),
    EUR("Euro", "€"),
    RUB("Russian Ruble", "₽"),
    TRY("Turkish Lira", "₺");

    private final String name;
    private final String symbol;

    Currency(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
}
