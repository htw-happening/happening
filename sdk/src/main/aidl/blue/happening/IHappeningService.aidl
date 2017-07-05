package blue.happening;

import blue.happening.HappeningClient;
import blue.happening.IHappeningCallback;


interface IHappeningService {
    void registerHappeningCallback(IHappeningCallback happeningCallback, String appId);
    List<HappeningClient> getClients();
    void sendMessage(in byte[] message, String uuid, String appId);
    void broadcastMessage(in byte[] message, String appId);
    void startService();
    void restartService();
    void stopService();
}
