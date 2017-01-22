package com.happening.poc.poc_happening;

import com.happening.poc.poc_happening.models.ChatEntryModel;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

/**
 * Created by daired on 22/01/17.
 */

public class ByteArrayTest {

    @Test
    public void arrayToObjectToArray() throws Exception {

        ChatEntryModel obj1 = new ChatEntryModel("TestAuthor","TestTime","TestType","TestContent");

        assertEquals(obj1.getAuthor(),"TestAuthor");
        assertEquals(obj1.getCreationTime(),"TestTime");
        assertEquals(obj1.getType(),"TestType");
        assertEquals(obj1.getContent(),"TestContent");

        byte[] b1 = obj1.toBytes();

        ChatEntryModel obj2 = new ChatEntryModel(b1);
        assertEquals(obj2.getAuthor(),"TestAuthor");
        assertEquals(obj2.getCreationTime(),"TestTime");
        assertEquals(obj2.getType(),"TestType");
        assertEquals(obj2.getContent(),"TestContent");
    }
}
