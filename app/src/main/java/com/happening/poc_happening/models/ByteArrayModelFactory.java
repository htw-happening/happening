package com.happening.poc_happening.models;

import com.happening.poc_happening.models.ChatEntryModel;

/**
 * Created by daired on 22/01/17.
 */

public class ByteArrayModelFactory {

    public static ChatEntryModel createChatEntryModel(byte[] bytes){
        return new ChatEntryModel(bytes);
    }


}
