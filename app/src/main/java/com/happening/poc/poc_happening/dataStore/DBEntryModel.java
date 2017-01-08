package com.happening.poc.poc_happening.dataStore;

public class DBEntryModel {

    private String name;
    private String address;
    private String lastSeen;


    public DBEntryModel(String name, String address, String lastSeen) {
        this.name = name;
        this.address = address;
        this.lastSeen = lastSeen;

    }

    public String getName() { return name; }
    public String getAddress() {
        return address;
    }
    public String getLastSeen() { return lastSeen; }

}
