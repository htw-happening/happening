package blue.happening;

import blue.happening.HappeningClient;
import blue.happening.HappeningParcel;


interface IHappeningService {

    String hello(String message);
    HappeningClient getClient(String clientId);
    HappeningClient[] getClients();
    void send(in HappeningClient[] recipients, in HappeningParcel parcel);
    HappeningParcel[] getParcels();
}
