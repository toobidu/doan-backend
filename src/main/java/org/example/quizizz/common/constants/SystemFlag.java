package org.example.quizizz.common.constants;

import lombok.Getter;

@Getter
public enum SystemFlag {
    NORMAL("0"),
    SYSTEM("1");

    private final String value;

    SystemFlag(String value) {
        this.value = value;
    }

}
