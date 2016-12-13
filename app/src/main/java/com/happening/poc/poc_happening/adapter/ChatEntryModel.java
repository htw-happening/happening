package com.happening.poc.poc_happening.adapter;

public class ChatEntryModel {

    private String author;
    private String content;

    public ChatEntryModel(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
