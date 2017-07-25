package blue.happening.sdk;

import android.content.Context;

import blue.happening.HappeningClient;

/**
 * Implement these methods for getting callbacks of the happening network. Mandatory for
 * {@link Happening#register(Context, HappeningCallback)}.
 */
public interface HappeningCallback {

    int MESSAGE_ACTION_ARRIVED = 0;
    int MESSAGE_ACTION_RECEIVED = 1;
    int MESSAGE_ACTION_DROPPED = 2;
    int MESSAGE_ACTION_FORWARDED = 3;
    int MESSAGE_ACTION_SENT = 4;

    int MESSAGE_TYPE_OGM = 1;
    int MESSAGE_TYPE_UCM = 2;

    /**
     * Callback if a new network {@link HappeningClient client} appeared.
     * @param client
     */
    void onClientAdded(HappeningClient client);

    /**
     * Callback if a network {@link HappeningClient client} was updated.
     * @param client
     */
    void onClientUpdated(HappeningClient client);

    /**
     * Callback if a network {@link HappeningClient client} was removed.
     * @param client
     */
    void onClientRemoved(HappeningClient client);

    /**
     * Callback for showing the network traffic for this device.
     * @param packageType {@link #MESSAGE_TYPE_OGM}, {@link #MESSAGE_TYPE_UCM}
     * @param action {@link #MESSAGE_ACTION_ARRIVED}, {@link #MESSAGE_ACTION_DROPPED},
     *                                              {@link #MESSAGE_ACTION_FORWARDED},
     *                                              {@link #MESSAGE_ACTION_RECEIVED},
     *                                              {@link #MESSAGE_ACTION_SENT}
     */
    void onMessageLogged(int packageType, int action);

    /**
     * Callback if the app receives a message from another {@link HappeningClient}.
     * @param message
     * @param source
     */
    void onMessageReceived(byte[] message, HappeningClient source);
}
