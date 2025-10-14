package com.myproject.FormApp.Dto;

import java.util.List;

public class ChartVisualizationDTO {
    private String chartType;    // "bar", "line", "pie"
    private String title;
    private List<String> labels; // x-axis or categories
    private List<Double> values; // numeric values

    public ChartVisualizationDTO() {}

    public ChartVisualizationDTO(String chartType, String title, List<String> labels, List<Double> values) {
        this.chartType = chartType;
        this.title = title;
        this.labels = labels;
        this.values = values;
    }

    // getters / setters
    public String getChartType() { return chartType; }
    public void setChartType(String chartType) { this.chartType = chartType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public List<Double> getValues() { return values; }
    public void setValues(List<Double> values) { this.values = values; }
}
