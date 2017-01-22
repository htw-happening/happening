package com.happening.poc.poc_happening.models;

public class ChatEntryModel extends ByteArrayModel {

    private String author;
    private String content;

    public ChatEntryModel(byte[] bytes){
        super(bytes);
    }

    public ChatEntryModel(String author, String content) {
        super();
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    protected boolean validateBytes(byte[] bytes){
        //TODO validate byte array
        return true;
    }
}
