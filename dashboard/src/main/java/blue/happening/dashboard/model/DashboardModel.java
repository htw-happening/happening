package blue.happening.dashboard.model;

public class DashboardModel {

    private final String title;
    private final String message;

    @SuppressWarnings("SameParameterValue")
    public DashboardModel(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }
}
