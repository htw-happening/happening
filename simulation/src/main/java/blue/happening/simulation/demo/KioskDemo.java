package blue.happening.simulation.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

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
                patterns.add(new PredefinedMobilityPattern<>(false, waypointList));
                return this;
            }

            private List<MobilityPattern<V, E>> getPatterns() {
                List<MobilityPattern<V, E>> copy = new ArrayList<>(patterns);
                patterns.clear();
                return copy;
            }
        }

        MobilityFactory<Device, Connection> factory = new MobilityFactory<>();
        Map<String, List<MobilityPattern<Device, Connection>>> patterns = new TreeMap<>();

        patterns.put("1a new neighbour", factory
                .addWaypoints(" 0, 0, 0")
                .addWaypoints("50, 0, 0", "50, 0, 10", "20, 0, 20")
                .getPatterns());

        patterns.put("2a new multihop", factory
                .addWaypoints("10, 0, 0")
                .addWaypoints("30, 0, 0")
                .addWaypoints("70, 0, 0", "70, 0, 10", "50, 0, 20")
                .getPatterns());

        patterns.put("3a also neighbour", factory
                .addWaypoints("35,  5, 0")
                .addWaypoints("50, 10, 0")
                .addWaypoints("70, 10, 0", "70, 10, 10", "50, 0, 5")
                .getPatterns());

        patterns.put("4a also multihop", factory
                .addWaypoints(" 0, 10, 0")
                .addWaypoints("10,  0, 0", "10,  0, 10", "15,  5, 10")
                .addWaypoints("10, 20, 0", "10, 20, 10", "15, 15, 10")
                .getPatterns());

        patterns.put("5a new route", factory
                .addWaypoints(" 0, 10, 0")
                .addWaypoints("15,  2, 0")
                .addWaypoints("15, 18, 0")
                .addWaypoints("35, 18, 0", "35, 18, 10", "30, 10, 10")
                .getPatterns());

        patterns.put("1b neighbour lost", factory
                .addWaypoints(" 0, 0, 0")
                .addWaypoints("20, 0, 0", "20, 0, 10", "50, 0, 10", "20, 0, 10")
                .getPatterns());

        patterns.put("2b lost multihop", factory
                .addWaypoints("10, 0, 0")
                .addWaypoints("30, 0, 0")
                .addWaypoints("50, 0, 0", "50, 0, 10", "70, 0, 20", "50, 0, 10")
                .getPatterns());

        patterns.put("3b only multihop", factory
                .addWaypoints("35,  5, 0")
                .addWaypoints("50, 10, 0")
                .addWaypoints("50,  0, 0", "50, 0, 10", "70, 10, 5", "70, 10, 10", "50, 0, 10")
                .getPatterns());

        patterns.put("4b only neighbour", factory
                .addWaypoints(" 0, 10, 0")
                .addWaypoints("15,  5, 0", "15,  5, 10", "10,  0, 10", "10,  0, 20", "15,  5, 10")
                .addWaypoints("15, 15, 0", "15, 15, 10", "10, 20, 10", "10, 20, 20", "15, 15, 10")
                .getPatterns());

        patterns.put("5b lost route", factory
                .addWaypoints(" 0, 10, 0")
                .addWaypoints("15,  2, 0")
                .addWaypoints("15, 18, 0")
                .addWaypoints("30, 10, 0", "30, 10, 20", "35, 18, 10", "35, 18, 15", "30, 10, 10")
                .getPatterns());

        String[] choices = patterns.keySet().toArray(new String[patterns.size()]);
        String selection = (String) JOptionPane.showInputDialog(null, "Choose now...",
                "Select kiosk demo", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

        for (int i = 0; i < patterns.get(selection).size(); i++) {
            Device device = new Device("Device_" + i, getGraph(), postman, runner, messageDelay, messageLoss);
            MobilityPattern<Device, Connection> pattern = patterns.get(selection).get(i);
            DTWaypoint<Device, Connection> initalWaypoint = (DTWaypoint<Device, Connection>) pattern.nextWaypoint(getGraph(), device);
            getGraph().addVertex(device, initalWaypoint.getSxf(), initalWaypoint.getSyf(), pattern, txRadius, rxRadius);
        }
    }
}
