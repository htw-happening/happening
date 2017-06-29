package blue.happening.simulation.entities;

public class Connection {

    private Device fromDevice;
    private Device toDevice;

    public Connection(Device fromDevice, Device toDevice) {

        this.fromDevice = fromDevice;
        this.toDevice = toDevice;
    }

    public Device getFromDevice() {
        return fromDevice;
    }

    public Device getToDevice() {
        return toDevice;
    }

    @Override
    public String toString() {
        return fromDevice.getName() + " > " + toDevice.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Connection) || o == null)
            return false;
        else if (o == this)
            return true;
        return this.getFromDevice().equals(((Connection) o).getFromDevice()) &&
                this.getToDevice().equals(((Connection) o).getToDevice());
    }
}
