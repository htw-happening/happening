package com.happening.happening;

import com.happening.happening.models.ChatEntryModel;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ByteArrayTest {

    @Test
    public void ObjectToArrayToObject() throws Exception {

        ChatEntryModel obj1 = new ChatEntryModel("TestAuthor","TestTime","TestType","TestContent");

        assertEquals(obj1.getAuthor(),"TestAuthor");
        assertEquals(obj1.getCreationTime(),"TestTime");
        assertEquals(obj1.getType(),"TestType");
        assertEquals(obj1.getContent(),"TestContent");

        byte[] b1 = obj1.toBytes();
        assertEquals(b1.length, 128);

        ChatEntryModel obj2 = new ChatEntryModel(b1);
        assertEquals(obj2.getAuthor(),"TestAuthor");
        assertEquals(obj2.getCreationTime(),"TestTime");
        assertEquals(obj2.getType(),"TestType");
        assertEquals(obj2.getContent(),"TestContent");

        byte[] b2 = obj2.toBytes();
        assertEquals(b2.length, 128);

        assertArrayEquals(b1, b2);


    }

    @Test
    public void checkLongInput() throws Exception {
        ChatEntryModel obj3 = new ChatEntryModel(
                "TestAuthorasdlkajsdklajsdlkasjdlkasjdlkasjdklasjdlkasjdlkasjdlkasjdlkasjfklhfaklsjflkajfkahfkasjdlkasjdlkajsdlkasjdlkajsdlkasjfklahgflaksjdlaksjfkdljas",
                "TestTimeasdlkajsdklajsdlkasjdlkasjdlkasjdklasjdlkasjdlkasjdlkasjdlkasjfklhfaklsjflkajfkahfkasjdlkasjdlkajsdlkasjdlkajsdlkasjfklahgflaksjdlaksjfkdljas",
                "TestTypeasdlkajsdklajsdlkasjdlkasjdlkasjdklasjdlkasjdlkasjdlkasjdlkasjfklhfaklsjflkajfkahfkasjdlkasjdlkajsdlkasjdlkajsdlkasjfklahgflaksjdlaksjfkdljas",
                "TestContentasdlkajsdklajsdlkasjdlkasjdlkasjdklasjdlkasjdlkasjdlkasjdlkasjfklhfaklsjflkajfkahfkasjdlkasjdlkajsdlkasjdlkajsdlkasjfklahgflaksjdlaksjfkdljas"
        );
        assertEquals(obj3.getAuthor(),"TestAuthorasdlkajsdklajsdlkasjdlkasjdlkasjdklasjdlkasjdlkasjdlkasjdlkasjfklhfaklsjflkajfkahfkasjdlkasjdlkajsdlkasjdlkajsdlkasjfklahgflaksjdlaksjfkdljas");
        assertEquals(obj3.getCreationTime(),"TestTimeasdlkajsdklajsdlkasjdlkasjdlkasjdklasjdlkasjdlkasjdlkasjdlkasjfklhfaklsjflkajfkahfkasjdlkasjdlkajsdlkasjdlkajsdlkasjfklahgflaksjdlaksjfkdljas");
        assertEquals(obj3.getType(),"TestTypeasdlkajsdklajsdlkasjdlkasjdlkasjdklasjdlkasjdlkasjdlkasjdlkasjfklhfaklsjflkajfkahfkasjdlkasjdlkajsdlkasjdlkajsdlkasjfklahgflaksjdlaksjfkdljas");
        assertEquals(obj3.getContent(),"TestContentasdlkajsdklajsdlkasjdlkasjdlkasjdklasjdlkasjdlkasjdlkasjdlkasjfklhfaklsjflkajfkahfkasjdlkasjdlkajsdlkasjdlkajsdlkasjfklahgflaksjdlaksjfkdljas");
        byte[] b3 = obj3.toBytes();
        assertEquals(b3.length, 128);

        ChatEntryModel obj4 = new ChatEntryModel(b3);
        System.out.println("Trimmed value: " + obj4.getAuthor());           // "TestAuthorasdlkajsdklajsdlkasjd"
        System.out.println("Trimmed value: " + obj4.getCreationTime());     // "TestTimeasdlkajsdklajsdlkasjdlk"
        System.out.println("Trimmed value: " + obj4.getType());             // "TestTypeasdlkajsdklajsdlkasjdlk"
        System.out.println("Trimmed value: " + obj4.getContent());          // "TestContentasdlkajsdklajsdlkasj"
        byte[] b4 = obj4.toBytes();


        byte[] b5 = new String("TestAuthorasdlkajsdklajsdlkasjd TestTimeasdlkajsdklajsdlkasjdlk TestTypeasdlkajsdklajsdlkasjdlk TestContentasdlkajsdklajsdlkasj ").getBytes(StandardCharsets.UTF_8);

        ChatEntryModel obj6 = new ChatEntryModel(b5);
        byte[] b6 = obj6.toBytes();
        assertArrayEquals(b4, b6);

    }

    @Test
    public void checkNull() throws Exception{
        ChatEntryModel obj5 = new ChatEntryModel(null, null, null, null);
        assertEquals(obj5.getAuthor(), null);
        assertEquals(obj5.getCreationTime(), null);
        assertEquals(obj5.getType(), null);
        assertEquals(obj5.getContent(), null);
    }
}
