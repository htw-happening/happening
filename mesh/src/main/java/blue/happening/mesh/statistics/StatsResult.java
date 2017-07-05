package blue.happening.mesh.statistics;

public class StatsResult {
    private Stat ucmIncoming;
    private Stat ucmOutgoing;
    private Stat ogmIncoming;
    private Stat ogmOutgoing;

    public Stat getUcmIncoming() {
        return ucmIncoming;
    }

    public void setUcmIncoming(Stat ucmIncoming) {
        this.ucmIncoming = ucmIncoming;
    }

    public Stat getUcmOutgoing() {
        return ucmOutgoing;
    }

    public void setUcmOutgoing(Stat ucmOutgoing) {
        this.ucmOutgoing = ucmOutgoing;
    }

    public Stat getOgmIncoming() {
        return ogmIncoming;
    }

    public void setOgmIncoming(Stat ogmIncoming) {
        this.ogmIncoming = ogmIncoming;
    }

    public Stat getOgmOutgoing() {
        return ogmOutgoing;
    }

    public void setOgmOutgoing(Stat ogmOutgoing) {
        this.ogmOutgoing = ogmOutgoing;
    }
}
