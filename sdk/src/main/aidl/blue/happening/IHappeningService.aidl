package blue.happening;

import blue.happening.HappeningClient;
import blue.happening.IHappeningCallback;


interface IHappeningService {
    void registerHappeningCallback(IHappeningCallback happeningCallback);
    String hello(String message);
    List<HappeningClient> getClients();



    List<HappeningClient> getDevices();
    void registerApp(int appId);
    void deregisterApp(int appId);
    void sendToDevice(int deviceId);
    void restart();

}
