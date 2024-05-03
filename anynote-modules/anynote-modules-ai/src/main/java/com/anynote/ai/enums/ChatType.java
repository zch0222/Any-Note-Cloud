package com.anynote.ai.enums;

public enum ChatType {

    DOC_RAG(0);

    private final int value;

    ChatType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
