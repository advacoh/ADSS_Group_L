package dev.domain;
public enum Certification {
    MANAGER("Manager"),
    CASHIER("Cashier"),
    WAREHOUSE("Warehouse");

    private final String value;

    Certification(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}