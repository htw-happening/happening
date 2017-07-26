package blue.happening.simulation.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

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

    private Map<String, List<MobilityPattern<Device, Connection>>> patterns;

    public static void main(String[] args) throws InterruptedException {
        new KioskDemo().start();
    }

    private void initPatterns() {
        final double frameHeight = getFrame().getContentPane().getHeight();
        final double frameWidth = getFrame().getContentPane().getWidth() / 2;
        final double vPadding = frameHeight * 0.1f;
        final double vSpace = frameHeight - 2 * vPadding;
        final double hPadding = frameWidth * 0.1f;
        final double hSpace = frameWidth - 2 * hPadding;

        class MobilityFactory<V, E> {
            private List<MobilityPattern<V, E>> patterns = new ArrayList<>();
            private RectangularBoundary<V, E> bound = new RectangularBoundary<>(0, 0, frameWidth, frameHeight);

            private MobilityFactory<V, E> addPredefinedDevice(String... waypoints) {
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

            private MobilityFactory<V, E> addRandomDevices(int count) {
                final int root = (int) Math.ceil(Math.sqrt(count));
                int deviceIndex = 0;
                for (int i = 0; i < root; i++) {
                    for (int j = 0; j < root; j++) {
                        if (deviceIndex < count) {
                            List<Waypoint<V, E>> waypointList = new ArrayList<>();
                            double sxf = hPadding + (i * (hSpace / root));
                            double syf = vPadding + (j * (vSpace / root));
                            waypointList.add(new DTWaypoint<V, E>(sxf, syf, 10));
                            MobilityPattern<V, E> fallback = new RandomDSMobilityPattern<>(bound, speedMin, speedMax);
                            patterns.add(new PredefinedMobilityPattern<>(false, waypointList, fallback));
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

        MobilityFactory<Device, Connection> factory = new MobilityFactory<>();
        patterns = new TreeMap<>();

        patterns.put("0A endless crowd", factory
                .addRandomDevices(16)
                .getPatterns());

        patterns.put("0B random crowd", factory
                .addRandomDevices(4 + new Random().nextInt(12))
                .getPatterns());

        patterns.put("1A new neighbour", factory
                .addPredefinedDevice("  0, 0,  0")
                .addPredefinedDevice("120, 0, 20", "80, 0, 20")
                .getPatterns());

        patterns.put("1B neighbour lost", factory
                .addPredefinedDevice(" 0, 0,  0")
                .addPredefinedDevice("80, 0, 20", "120, 0, 20", "80, 0, 20")
                .getPatterns());

        patterns.put("2A new multihop", factory
                .addPredefinedDevice("  0, 0,  0")
                .addPredefinedDevice(" 80, 0,  0")
                .addPredefinedDevice("200, 0, 20", "160, 0, 20")
                .getPatterns());

        patterns.put("2B lost multihop", factory
                .addPredefinedDevice("  0, 0,  0")
                .addPredefinedDevice(" 80, 0,  0")
                .addPredefinedDevice("160, 0, 20", "200, 0, 20")
                .getPatterns());

        patterns.put("3A also neighbour", factory
                .addPredefinedDevice(" 0,   0,  0")
                .addPredefinedDevice(" 0,  80,  0")
                .addPredefinedDevice("80, 120, 20", "80, 40, 20")
                .getPatterns());

        patterns.put("3B only multihop", factory
                .addPredefinedDevice(" 0,  0,  0")
                .addPredefinedDevice(" 0, 80,  0")
                .addPredefinedDevice("80, 40, 20", "80, 120, 20")
                .getPatterns());

        patterns.put("4A also multihop", factory
                .addPredefinedDevice(" 0,  60,  0")
                .addPredefinedDevice("60,   0, 20", "80,  20, 20")
                .addPredefinedDevice("60, 120, 20", "80, 100, 20")
                .getPatterns());

        patterns.put("4B only neighbour", factory
                .addPredefinedDevice(" 0,  60,  0")
                .addPredefinedDevice("80,  20, 20", "60,   0, 20")
                .addPredefinedDevice("80, 100, 20", "60, 120, 20")
                .getPatterns());

        patterns.put("5A new route", factory
                .addPredefinedDevice(" 0, 40,  0")
                .addPredefinedDevice("80,  0,  0")
                .addPredefinedDevice("80, 80,  0")
                .addPredefinedDevice("160, 80, 20", "160, 40, 20")
                .getPatterns());

        patterns.put("5B lost route", factory
                .addPredefinedDevice("  0, 40,  0")
                .addPredefinedDevice(" 80,  0,  0")
                .addPredefinedDevice(" 80, 80,  0")
                .addPredefinedDevice("160, 40, 20", "160, 80, 20")
                .getPatterns());
    }

    @Override
    MeshGraph createGraph(String patternKey) {
        if (patterns == null) {
            initPatterns();
        }
        final MeshGraph graph = new MeshGraph(noopInterval, noopSleep);
        List<MobilityPattern<Device, Connection>> patternList;
        try {
            patternList = patterns.get(patternKey);
        } catch (NullPointerException e) {
            patternList = patterns.get("0B random crowd");
        }
        for (int i = 0; i < patternList.size(); i++) {
            Device device = new Device("Device_" + i, HappeningDemo.getRunner(), messageDelay, messageLoss);
            MobilityPattern<Device, Connection> p = patternList.get(i);
            DTWaypoint<Device, Connection> initial = (DTWaypoint<Device, Connection>) p.getStartpoint(graph, device);
            graph.addVertex(device, initial.getSxf(), initial.getSyf(), p, txRadius, rxRadius);
        }
        return graph;
    }

    @Override
    public String[] createPatternKeys() {
        return patterns.keySet().toArray(new String[patterns.size()]);
    }
}
