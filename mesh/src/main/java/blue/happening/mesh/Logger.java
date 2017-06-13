package blue.happening.mesh;

public class Logger {

    private ILogger logger;

    public Logger() {
        try {
            Object simulator = Class.forName("android.util.Log").newInstance();
            logger = (ILogger) simulator;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    interface ILogger {
        void debug(String string);
    }

    void debug(String string) {
        if (logger != null)
            logger.debug(string);
    }
}
