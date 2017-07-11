package blue.happening;

import blue.happening.HappeningClient;


interface IHappeningCallback {

    void onClientAdded(in HappeningClient client);

    void onClientUpdated(in HappeningClient client);

    void onClientRemoved(in HappeningClient client);

    void logMessage(in int packageType, in int action);

    void onMessageReceived(in byte[] message, in HappeningClient source);
}
