package blue.happening.simulation.demo;


import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.mobility.MobilityPattern;
import blue.happening.simulation.mobility.RandomDSMobilityPattern;
import blue.happening.simulation.mobility.RectangularBoundary;

public class CrowdDemo extends HappeningDemo {

    public static void main(String[] args) throws InterruptedException {
        new CrowdDemo().start();
    }

    @Override
    void populateGraph() {
        final double frameHeight = getFrame().getVisualizerPanel().getHeight();
        final double frameWidth = getFrame().getVisualizerPanel().getWidth();
        final int root = (int) Math.ceil(Math.sqrt(deviceCount));

        final double verticalStep = Math.min(minRadius, frameHeight / (root + 1));
        final double horizontalStep = Math.min(minRadius, frameWidth / (root + 1));
        final double verticalPadding = (frameHeight - (verticalStep * (root - 1))) / 2;
        final double horizontalPadding = (frameWidth - (horizontalStep * (root - 1))) / 2;
        final RectangularBoundary<Device, Connection> bound = new RectangularBoundary<>(0, 0, frameWidth, frameHeight);

        int deviceIndex = 0;
        for (int i = 0; i < root; i++) {
            for (int j = 0; j < root; j++) {
                if (deviceIndex < deviceCount) {
                    Device device = new Device("Device_" + deviceIndex, getGraph(), postman, runner, messageDelay, messageLoss);
                    double sx = horizontalPadding + (i * horizontalStep);
                    double sy = verticalPadding + (j * verticalStep);
                    MobilityPattern<Device, Connection> pattern = new RandomDSMobilityPattern<>(bound, speedMin, speedMax);
                    getGraph().addVertex(device, sx, sy, pattern, txRadius, rxRadius);
                    deviceIndex++;
                }
            }
        }
    }
}
