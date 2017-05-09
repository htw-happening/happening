package blue.happening;

import blue.happening.HappeningClient;


interface IHappeningCallback {
    void onClientAdded(in HappeningClient client);
    void onClientUpdated(in HappeningClient client);
    void onClientRemoved(in HappeningClient client);
    void onParcelQueued(long parcelId);
}
