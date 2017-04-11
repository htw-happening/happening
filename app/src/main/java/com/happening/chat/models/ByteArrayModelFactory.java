package com.happening.chat.models;

public class ByteArrayModelFactory {

    public static ChatEntryModel createChatEntryModel(byte[] bytes) {
        return new ChatEntryModel(bytes);
    }


}
