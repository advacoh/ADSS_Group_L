package dev.domain;
public enum Certification {
    HR_MANAGER("HR manager"),
    CASHIER("Cashier"),
    WAREHOUSE("Warehouse"),
    SHIFT_MANAGER("shift manager");


    private final String value;

    Certification(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}