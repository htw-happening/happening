package blue.happening.service.bluetooth;

/**
 * Created by fabi on 23.05.17.
 */

interface IRemoteDevice {
    public boolean send(byte[] bytes);
}
