package ai.megaworks.ema.api;

import java.util.List;

public class ReportData {

    private String monday;
    private List<UserData> userReport;

    public ReportData(String monday, List<UserData> userReport) {
        this.monday = monday;
        this.userReport = userReport;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public List<UserData> getUserReport() {
        return userReport;
    }

    public void setUserReport(List<UserData> userReport) {
        this.userReport = userReport;
    }
}
