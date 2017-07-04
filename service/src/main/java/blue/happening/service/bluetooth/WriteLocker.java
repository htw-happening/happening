package blue.happening.service.bluetooth;


public class WriteLocker {

    private static WriteLocker instance;

    private WriteLocker(){}

    public static WriteLocker getInstance() {
        if (instance == null) instance = new WriteLocker();
        return instance;
    }

}
