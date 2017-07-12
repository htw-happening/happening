package blue.happening.mesh;


public interface IRemoteDevice extends Comparable<IRemoteDevice> {

    public String getUuid();

    // public boolean isNeighbour();

    public float getTq();

    public float getEq();

    public float getRq();

    public boolean sendMessage(Message message);

    public boolean remove();

    public boolean equals(Object object);
}
