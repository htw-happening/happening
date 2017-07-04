package blue.happening.mesh;

import java.util.Observable;
import java.util.Observer;

import blue.happening.mesh.statistics.NetworkStats;

import static blue.happening.mesh.Router.*;
import static blue.happening.mesh.Router.Events.OGM_SENT;

public class RouterObserver implements Observer {
    private NetworkStats ogmStats;
    private NetworkStats ucmStats;

    RouterObserver(NetworkStats ogmStats, NetworkStats ucmStats) {
        this.ogmStats = ogmStats;
        this.ucmStats = ucmStats;
    }

    @Override
    public void update(Observable observable, Object o) {
        Router.Event event = (Router.Event) o;
        switch (event.getType()) {
            case OGM_SENT:
                ogmStats.addOutGoingMessage((Message) event.getOptions());
                break;
            case UCM_SENT:
                ucmStats.addOutGoingMessage((Message) event.getOptions());
                break;

        }
    }
}
