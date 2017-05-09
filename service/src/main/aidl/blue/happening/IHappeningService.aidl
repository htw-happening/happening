package blue.happening;

import blue.happening.IHappeningCallback;

interface IHappeningService {
    void registerHappeningCallback(IHappeningCallback happeningCallback);
    String hello(String message);
    String getClient(String clientId);
}
