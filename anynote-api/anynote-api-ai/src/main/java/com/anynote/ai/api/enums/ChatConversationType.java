package com.anynote.ai.api.enums;

public enum ChatConversationType {

    RAG(0),

    CHAT(1);

    private final int value;

    ChatConversationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
