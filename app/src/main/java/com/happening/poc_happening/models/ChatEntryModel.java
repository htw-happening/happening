package com.happening.poc_happening.models;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ChatEntryModel extends ByteArrayModel {

    private String author;
    private String creationTime;
    private String type;
    private String content;

    public ChatEntryModel(byte[] bytes) {
        super(bytes);
        if (this.isBytesValide()) {
            this.author = new String(trimZeros(Arrays.copyOfRange(bytes, 0, 31)), StandardCharsets.UTF_8);
            this.creationTime = new String(trimZeros(Arrays.copyOfRange(bytes, 32, 63)), StandardCharsets.UTF_8);
            this.type = new String(trimZeros(Arrays.copyOfRange(bytes, 64, 95)), StandardCharsets.UTF_8);
            this.content = new String(trimZeros(Arrays.copyOfRange(bytes, 96, 127)), StandardCharsets.UTF_8);
        }
    }

    public ChatEntryModel(String author, String creationTime, String type, String content) {
        super();
        this.author = author;
        this.creationTime = creationTime;
        this.type = type;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    @Override
    public byte[] toBytes() {
        byte[] bytes = new byte[128];
        byte[] tmp1 = author.getBytes(StandardCharsets.UTF_8);
        byte[] tmp2 = creationTime.getBytes(StandardCharsets.UTF_8);
        byte[] tmp3 = type.getBytes(StandardCharsets.UTF_8);
        byte[] tmp4 = content.getBytes(StandardCharsets.UTF_8);

        System.arraycopy(tmp1, 0, bytes, 0, tmp1.length <= 32 ? tmp1.length : 32);
        System.arraycopy(tmp2, 0, bytes, 32, tmp2.length <= 32 ? tmp2.length : 32);
        System.arraycopy(tmp3, 0, bytes, 64, tmp3.length <= 32 ? tmp3.length : 32);
        System.arraycopy(tmp4, 0, bytes, 96, tmp4.length <= 32 ? tmp4.length : 32);
        return bytes;
    }

}
