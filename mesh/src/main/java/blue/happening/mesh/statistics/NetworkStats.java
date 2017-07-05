package blue.happening.mesh.statistics;

import blue.happening.mesh.Message;

public class NetworkStats {
    private Stat in;
    private Stat out;

    public NetworkStats() {
        in = new Stat();
        out = new Stat();
    }

    public void addInComingMessage(Message message) {
        in.addMessage(message);
    }

    public Stat getIncomingStat() {
        return in.copy();
    }

    public void addOutGoingMessage(Message message) {
        out.addMessage(message);
    }

    public Stat getOutgoingStat(){
        return out.copy();
    }

    public void updateTs(double ts){
        in.updateTs(ts);
        out.updateTs(ts);
    }
}