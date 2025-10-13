package com.myproject.FormApp.Dto;

import java.util.List;
import java.util.Map;

public class ChartDataDTO {
    private String chartType; // "bar" or "pie"
    private Map<String, Object> meta;
    private List<String> labels;   // student names
    private List<Double> data;     // rating or sentiment numeric
    private List<String> backgroundColors; // optional

    public String getChartType() { return chartType; }
    public void setChartType(String chartType) { this.chartType = chartType; }

    public Map<String, Object> getMeta() { return meta; }
    public void setMeta(Map<String, Object> meta) { this.meta = meta; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public List<Double> getData() { return data; }
    public void setData(List<Double> data) { this.data = data; }

    public List<String> getBackgroundColors() { return backgroundColors; }
    public void setBackgroundColors(List<String> backgroundColors) { this.backgroundColors = backgroundColors; }
}
