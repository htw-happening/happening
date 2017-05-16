package blue.happening;

import blue.happening.HappeningClient;
import blue.happening.IHappeningCallback;


interface IHappeningService {
    void registerHappeningCallback(IHappeningCallback happeningCallback);
    String hello(String message);
    List<HappeningClient> getClients();
}
