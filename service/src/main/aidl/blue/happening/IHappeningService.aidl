package blue.happening;

import blue.happening.HappeningClient;
import blue.happening.HappeningParcel;


interface IHappeningService {

    String hello(String message);
    HappeningClient getClient(String clientId);
    List<HappeningClient> getClients();
    void send(in List<HappeningClient> recipients, in HappeningParcel parcel);
    List<HappeningParcel> getParcels();
}
