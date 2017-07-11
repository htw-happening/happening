package blue.happening.mesh;


class Route implements Comparable<Route> {

    private String viaDevice;
    private String toDevice;
    private float mq;

    Route(String viaDevice, String toDevice) {
        this.viaDevice = viaDevice;
        this.toDevice = toDevice;
        this.mq = 0.0f;
    }

    float getMq() {
        return mq;
    }

    void setMq(float mq) {
        this.mq = mq;
    }

    String getViaDevice() {
        return viaDevice;
    }

    String getToDevice() {
        return toDevice;
    }

    @Override
    public int hashCode() {
        // TODO: Is there a better way to hash strings?
        return (viaDevice + toDevice).hashCode();
    }

    @Override
    public int compareTo(Route other) {
        return Float.compare(
                this.getMq(),
                other.getMq());
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (!(object instanceof Route)) {
            return false;
        }
        return ((Route) object).getViaDevice().equals(getViaDevice()) &&
                ((Route) object).getToDevice().equals(getToDevice());
    }
}
