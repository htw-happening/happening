package blue.happening.chat.bluetooth.bluetoothEDR.gui;

/**
 * This class is a model for the MessageAdapter.
 * Use Log boolean to use this model as log-messages
 */
public class MessageModel {

    private String from;
    private String content;
    private boolean isLog;

    public MessageModel(String from, String content, boolean isLog) {
        this.from = from;
        this.content = content;
        this.isLog = isLog;
    }

    public MessageModel(String from, String content) {
        this.from = from;
        this.content = content;
        this.isLog = false;
    }

    public String getFrom() {
        return from;
    }

    public String getContent() {
        return content;
    }

    public boolean isLog() {
        return isLog;
    }
}
