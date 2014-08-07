package models.zcreport;

import java.util.Map;

public class ReportDetailsEntry {

    private Map<String, Double> data;
    public String appId;
    public boolean error = false;

    public ReportDetailsEntry(String appId, Map<String, Double> data) {
        this.appId = appId;
        this.data = data;
    }

    public Double getValue(String date) {
        return data.get(date);
    }
}
