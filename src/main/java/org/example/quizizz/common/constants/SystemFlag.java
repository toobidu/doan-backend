package org.example.quizizz.common.constants;

public enum SystemFlag {
    NORMAL("0"),
    SYSTEM("1");

    private final String value;

    SystemFlag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
