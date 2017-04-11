package blue.happening.chat.datastore;

public class DBTestEntryModel {

    private String name;
    private String address;
    private String lastSeen;

    public DBTestEntryModel(String name, String address, String lastSeen) {
        this.name = name;
        this.address = address;
        this.lastSeen = lastSeen;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getLastSeen() {
        return lastSeen;
    }

}
