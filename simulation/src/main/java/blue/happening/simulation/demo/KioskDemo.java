package blue.happening.simulation.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.MeshGraph;
import blue.happening.simulation.mobility.DTWaypoint;
import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.PredefinedMobilityPattern;
import blue.happening.simulation.mobility.RandomDSMobilityPattern;
import blue.happening.simulation.mobility.RectangularBoundary;
import blue.happening.simulation.mobility.StationaryMobilityPattern;
import blue.happening.simulation.mobility.Waypoint;

public class KioskDemo extends HappeningDemo {

    public static void main(String[] args) throws InterruptedException {
        new KioskDemo().start();
    }

    private List<MobilityPattern<Device, Connection>> getPattern(String patternKey) {
        final double frameHeight = getFrame().getContentPane().getHeight();
        final double frameWidth = getFrame().getContentPane().getWidth() / 2;

        class MobilityFactory<V, E> {
            private List<MobilityPattern<V, E>> patterns = new ArrayList<>();
            private RectangularBoundary<V, E> bound = new RectangularBoundary<>(0, 0, frameWidth, frameHeight);

            private MobilityFactory<V, E> addPredefinedDevice(String... waypoints) {
                final double vPadding = frameHeight * 0.3f;
                final double hPadding = frameWidth * 0.6f;
                List<Waypoint<V, E>> waypointList = new ArrayList<>();
                for (String waypoint : waypoints) {
                    String[] args = waypoint.replace(" ", "").split(",");
                    if (args.length == 3) {
                        double sxf = hPadding + (Integer.parseInt(args[0]));
                        double syf = vPadding + (Integer.parseInt(args[1]));
                        double travelTime = 100 - Double.parseDouble(args[2]);
                        waypointList.add(new DTWaypoint<V, E>(sxf, syf, travelTime));
                    }
                }
                MobilityPattern<V, E> fallback = new StationaryMobilityPattern<>();
                patterns.add(new PredefinedMobilityPattern<>(false, waypointList, fallback));
                return this;
            }

            private MobilityFactory<V, E> addRandomDevices(int count, double minSpeed, double maxSpeed) {
                final double vPadding = frameHeight * 0.2f;
                final double hPadding = frameWidth * 0.3f;
                final double vSpace = frameHeight - vPadding;
                final double hSpace = frameWidth - hPadding;
                final int root = (int) Math.ceil(Math.sqrt(count));
                int deviceIndex = 0;
                for (int i = 0; i < root; i++) {
                    for (int j = 0; j < root; j++) {
                        if (deviceIndex < count) {
                            double sxf = hPadding + (i * Math.min(99, hSpace / root));
                            double syf = vPadding + (j * Math.min(99, vSpace / root));
                            MobilityPattern<V, E> mobilityPattern = new RandomDSMobilityPattern<>(bound, minSpeed, maxSpeed);
                            mobilityPattern.setStartpoint(new DTWaypoint<V, E>(sxf, syf, 10));
                            patterns.add(mobilityPattern);
                            deviceIndex++;
                        }
                    }
                }
                return this;
            }

            private List<MobilityPattern<V, E>> getPatterns() {
                List<MobilityPattern<V, E>> copy = new ArrayList<>(patterns);
                patterns.clear();
                return copy;
            }
        }

        final MobilityFactory<Device, Connection> factory = new MobilityFactory<>();

        switch (patternKey) {
            case "durable_crowd":
                return factory
                        .addRandomDevices(16 + new Random().nextInt(24), 0.25D, 2.0D)
                        .getPatterns();

            case "massive_crowd":
                return factory
                        .addRandomDevices(64 + new Random().nextInt(96), 0.15D, 0.85D)
                        .getPatterns();

            case "random_crowd":
                return factory
                        .addRandomDevices(8 + new Random().nextInt(12), 0.2D, 1.8D)
                        .getPatterns();

            case "static_crowd":
                return factory
                        .addRandomDevices(12 + new Random().nextInt(24), 0.0D, 0.0D)
                        .getPatterns();

            case "slow_crowd":
                return factory
                        .addRandomDevices(12 + new Random().nextInt(16), 0.05D, 0.25D)
                        .getPatterns();

            case "new_neighbour":
                return factory
                        .addPredefinedDevice("  0, 0,  0")
                        .addPredefinedDevice("120, 0, 20", "80, 0, 20")
                        .getPatterns();

            case "neighbour_lost":
                return factory
                        .addPredefinedDevice(" 0, 0,  0")
                        .addPredefinedDevice("80, 0, 20", "120, 0, 20", "80, 0, 20")
                        .getPatterns();

            case "new_multihop":
                return factory
                        .addPredefinedDevice("  0, 0,  0")
                        .addPredefinedDevice(" 80, 0,  0")
                        .addPredefinedDevice("200, 0, 20", "160, 0, 20")
                        .getPatterns();

            case "lost_multihop":
                return factory
                        .addPredefinedDevice("  0, 0,  0")
                        .addPredefinedDevice(" 80, 0,  0")
                        .addPredefinedDevice("160, 0, 20", "200, 0, 20")
                        .getPatterns();

            case "also_neighbour":
                return factory
                        .addPredefinedDevice(" 0,   0,  0")
                        .addPredefinedDevice(" 0,  80,  0")
                        .addPredefinedDevice("80, 120, 20", "80, 40, 20")
                        .getPatterns();

            case "only_multihop":
                return factory
                        .addPredefinedDevice(" 0,  0,  0")
                        .addPredefinedDevice(" 0, 80,  0")
                        .addPredefinedDevice("80, 40, 20", "80, 120, 20")
                        .getPatterns();

            case "also_multihop":
                return factory
                        .addPredefinedDevice(" 0,  60,  0")
                        .addPredefinedDevice("60,   0, 20", "80,  20, 20")
                        .addPredefinedDevice("60, 120, 20", "80, 100, 20")
                        .getPatterns();

            case "only_neighbour":
                return factory
                        .addPredefinedDevice(" 0,  60,  0")
                        .addPredefinedDevice("80,  20, 20", "60,   0, 20")
                        .addPredefinedDevice("80, 100, 20", "60, 120, 20")
                        .getPatterns();

            case "new_route":
                return factory
                        .addPredefinedDevice(" 0, 40,  0")
                        .addPredefinedDevice("80,  0,  0")
                        .addPredefinedDevice("80, 80,  0")
                        .addPredefinedDevice("160, 80, 20", "160, 40, 20")
                        .getPatterns();

            case "lost_route":
                return factory
                        .addPredefinedDevice("  0, 40,  0")
                        .addPredefinedDevice(" 80,  0,  0")
                        .addPredefinedDevice(" 80, 80,  0")
                        .addPredefinedDevice("160, 40, 20", "160, 80, 20")
                        .getPatterns();
            default:
                return factory
                        .addRandomDevices(10, 0.2D, 2.0D)
                        .getPatterns();
        }
    }

    @Override
    MeshGraph createGraph(String patternKey) {
        final MeshGraph graph = new MeshGraph(noopInterval, noopSleep);
        final List<MobilityPattern<Device, Connection>> patternList = getPattern(patternKey);
        for (int i = 0; i < patternList.size(); i++) {
            Device device = new Device("device_" + i, HappeningDemo.getRunner(), messageDelay, messageLoss);
            MobilityPattern<Device, Connection> p = patternList.get(i);
            Waypoint<Device, Connection> initial = p.getStartpoint(graph, device);
            graph.addVertex(device, initial.getSxf(), initial.getSyf(), p, txRadius, rxRadius);
        }
        return graph;
    }

    @Override
    public String[] createPatternKeys() {
        String[] keys = {
                "durable_crowd",
                "massive_crowd",
                "random_crowd",
                "static_crowd",
                "slow_crowd",
                "new_neighbour",
                "neighbour_lost",
                "new_multihop",
                "lost_multihop",
                "also_neighbour",
                "only_multihop",
                "also_multihop",
                "only_neighbour",
                "new_route",
                "lost_route"};
        return keys;
    }
}
