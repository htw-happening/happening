package blue.happening.mesh;

class Route implements Comparable<Route> {

    private RemoteDevice viaDevice;
    private RemoteDevice toDevice;
    private float mq;

    Route(RemoteDevice viaDevice, RemoteDevice toDevice) {
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

    RemoteDevice getViaDevice() {
        return viaDevice;
    }

    RemoteDevice getToDevice() {
        return toDevice;
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
