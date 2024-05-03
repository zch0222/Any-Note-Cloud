package com.anynote.ai.enums;

public enum ChatRole {

    USER(0),
    BOT(1);

    private final int value;

    ChatRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
