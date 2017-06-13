package blue.happening;

import blue.happening.HappeningClient;
import blue.happening.IHappeningCallback;


interface IHappeningService {
    void registerHappeningCallback(IHappeningCallback happeningCallback, String appId);
    String hello(String message);
    List<HappeningClient> getClients();



    List<HappeningClient> getDevices();
    void sendToDevice(String deviceId, String appId, in byte[] content);
    void restart();

}
