package blue.happening.service.bluetooth;

interface IDeviceFinder {
    void registerCallback(Layer layer);

    void start();

    void stop();
}
