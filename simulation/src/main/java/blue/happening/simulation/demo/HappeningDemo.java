package blue.happening.simulation.demo;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import blue.happening.mesh.MeshHandler;
import blue.happening.simulation.entities.Connection;
import blue.happening.simulation.entities.Device;
import blue.happening.simulation.graph.MeshGraph;
import blue.happening.simulation.visualization.MeshVisualizerFrame;
import jsl.modeling.IterativeProcess;
import jsl.modeling.Replication;
import jsl.modeling.conditions.ConditionIfc;


@SuppressWarnings("WeakerAccess")
public abstract class HappeningDemo {

    int deviceCount;
    int messageDelay;
    int warmUpLength;
    float messageLoss;
    double txRadius;
    double rxRadius;
    double noopInterval;
    long noopSleep;

    private static ScheduledExecutorService runner;
    private static MeshGraph graph;
    private static MeshVisualizerFrame frame;
    private static String pattern = "random_crowd";
    private static String[] patternKeys;

    private static boolean loop;
    private static boolean pause;
    private static boolean interrupt;

    HappeningDemo() {
        MeshHandler.INITIAL_MESSAGE_TQ = 255;
        MeshHandler.INITIAL_MESSAGE_TTL = 5;
        MeshHandler.HOP_PENALTY = 15;
        MeshHandler.OGM_INTERVAL = 3;
        MeshHandler.PURGE_INTERVAL = 8;
        MeshHandler.NETWORK_STAT_INTERVAL = 1;
        MeshHandler.SLIDING_WINDOW_SIZE = 12;
        MeshHandler.DEVICE_EXPIRATION = 8;
        MeshHandler.INITIAL_MIN_SEQUENCE = 0;
        MeshHandler.INITIAL_MAX_SEQUENCE = 9999;

        this.deviceCount = 10;
        this.messageDelay = 240;
        this.warmUpLength = 0;
        this.messageLoss = 0.1F;
        this.txRadius = 100D;
        this.rxRadius = 100D;
        this.noopInterval = 1D;
        this.noopSleep = 50L;
    }

    abstract MeshGraph createGraph(String patternKey);

    abstract String[] createPatternKeys();

    ScheduledExecutorService createRunner() {
        ScheduledExecutorService runner = Executors.newSingleThreadScheduledExecutor();
        runner.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                while (pause) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
        return runner;
    }


    private void runReplication(int replicationLength) {
        Replication replication = new Replication(graph.getModel());
        replication.setLengthOfReplication(replicationLength);
        replication.setLengthOfWarmUp(warmUpLength);
        replication.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object object) {
                IterativeProcess ip = (IterativeProcess) observable;
                if (ip.isRunning() && pause) {
                    // TODO: prevent runner scheduling explosion after resume (ND)
                    while (pause) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (interrupt) {
                    interrupt = false;
                    if (ip.isRunning()) {
                        ip.setEndCondition(new ConditionIfc() {
                            @Override
                            public void setName(String s) {
                            }

                            @Override
                            public String getName() {
                                return "Interrupt";
                            }

                            @Override
                            public void evaluate() {
                            }

                            @Override
                            public boolean isSet() {
                                return true;
                            }

                            @Override
                            public void clear() {
                            }

                            @Override
                            public void set() {
                            }
                        });
                    }
                } else if (ip.isEnded()) {
                    for (Connection connection : graph.getEdges()) {
                        connection.destroy();
                    }
                }
            }
        });
        replication.runAll();
        graph = null;
    }

    public void start() {
        frame = new MeshVisualizerFrame();
        int replicationLength = 500;

        while (true) {
            runner = createRunner();
            if (pattern != null && pattern.contains("durable") && pattern.contains("massive")) {
                replicationLength = 50000;
            } else if (pattern != null && pattern.contains("crowd")) {
                replicationLength = 5000;
            }
            graph = createGraph(pattern);
            if (patternKeys == null) {
                patternKeys = createPatternKeys();
            }
            frame.init();
            for (Device device : graph.getVertices()) {
                device.setClicked(true);
                break;
            }
            if (!loop) {
                pattern = patternKeys[2 + new Random().nextInt(patternKeys.length - 2)];
            }
            runReplication(replicationLength);
            runner.shutdownNow();
            frame.destroy();
        }
    }

    public static void setInterrupt(boolean interrupt) {
        HappeningDemo.interrupt = interrupt;
    }

    public static void setPause(boolean pause) {
        HappeningDemo.pause = pause;
    }

    public static boolean isPaused() {
        return pause;
    }

    public static boolean isLoop() {
        return loop;
    }

    public static void setLoop(boolean loop) {
        HappeningDemo.loop = loop;
    }

    public static ScheduledExecutorService getRunner() {
        return runner;
    }

    public static MeshGraph getGraph() {
        return graph;
    }

    public static MeshVisualizerFrame getFrame() {
        return frame;
    }

    public static String[] getPatternKeys() {
        return patternKeys;
    }

    public static String getPattern() {
        return pattern;
    }

    public static void setPattern(String pattern) {
        HappeningDemo.pattern = pattern;
    }
}
