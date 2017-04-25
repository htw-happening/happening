package blue.happening.dashboard.model;

public class DashboardModel {

    private String title;
    private String message;

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
