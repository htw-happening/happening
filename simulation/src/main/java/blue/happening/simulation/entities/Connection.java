package blue.happening.simulation.entities;

public class Connection {

    private String name;
    private Device fromDev;
    private Device toDev;

    public Connection(Device fromDev, Device toDev) {
        this.name = fromDev.getName() + " => " + toDev.getName();
        this.fromDev = fromDev;
        this.toDev = toDev;
    }

    String getName() {
        return name;
    }

    public Device getFromDev() {
        return fromDev;
    }

    public Device getToDev() {
        return toDev;
    }

    @Override
    public int hashCode() {
        return 31 * 17 + getName().hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Connection) || o == null)
            return false;
        else if (o == this)
            return true;
        return this.getName().equals(((Connection) o).getName());
    }
}
