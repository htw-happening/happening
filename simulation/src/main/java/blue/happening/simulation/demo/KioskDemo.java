package blue.happening.simulation.demo;


import java.util.ArrayList;
import java.util.List;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.mobility.DTWaypoint;
import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.PredefinedMobilityPattern;
import blue.happening.simulation.mobility.Waypoint;

public class KioskDemo extends HappeningDemo {

    public static void main(String[] args) throws InterruptedException {
        new KioskDemo().start();
    }

    void populateGraph() {
        final double frameHeight = getFrame().getVisualizerPanel().getHeight();
        final double frameWidth = getFrame().getVisualizerPanel().getWidth();
        final double vPadding = frameHeight * 0.1f;
        final double vSpace = frameHeight - 2 * vPadding;
        final double hPadding = frameWidth * 0.1f;
        final double hSpace = frameWidth - 2 * hPadding;

        class MobilityFactory<V, E> {
            private List<MobilityPattern<V, E>> patterns = new ArrayList<>();
            private boolean onRepeat;

            public MobilityFactory(boolean onRepeat) {
                this.onRepeat = onRepeat;
            }

            private MobilityFactory<V, E> addWaypoints(String... waypoints) {
                List<Waypoint<V, E>> waypointList = new ArrayList<>();
                for (String waypoint : waypoints) {
                    String[] args = waypoint.replace(" ", "").split(",");
                    if (args.length == 3) {
                        double sxf = hPadding + (Integer.parseInt(args[0]) / 100f * hSpace);
                        double syf = vPadding + (Integer.parseInt(args[1]) / 100f * vSpace);
                        double travelTime = 100 - Double.parseDouble(args[2]);
                        waypointList.add(new DTWaypoint<V, E>(sxf, syf, travelTime));
                    }
                }
                patterns.add(new PredefinedMobilityPattern<>(onRepeat, waypointList));
                return this;
            }

            private List<MobilityPattern<V, E>> getPatterns() {
                return patterns;
            }
        }

        List<MobilityPattern<Device, Connection>> newNeighbour = new MobilityFactory<Device, Connection>(false)
                .addWaypoints(" 0, 0, 0")
                .addWaypoints("50, 0, 0", "20, 0, 30")
                .getPatterns();


        List<MobilityPattern<Device, Connection>> newRemote = new MobilityFactory<Device, Connection>(false)
                .addWaypoints(" 0, 0, 0")
                .addWaypoints("20, 0, 0")
                .addWaypoints("80, 0, 0", "40, 0, 30")
                .getPatterns();

        List<MobilityPattern<Device, Connection>> patterns = newRemote;
        for (int i = 0; i < patterns.size(); i++) {
            Device device = new Device("Device_" + i, getGraph(), postman, runner, messageDelay, messageLoss);
            MobilityPattern<Device, Connection> pattern = patterns.get(i);
            DTWaypoint<Device, Connection> initalWaypoint = (DTWaypoint<Device, Connection>) pattern.nextWaypoint(getGraph(), device);
            getGraph().addVertex(device, initalWaypoint.getSxf(), initalWaypoint.getSyf(), pattern, txRadius, rxRadius);
        }
    }
}
