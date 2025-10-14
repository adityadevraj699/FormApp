package com.myproject.FormApp.Dto;

import java.util.List;
import java.util.Map;

public class ChartDataDTO {
    private String chartType;
    private Map<String, Object> meta;
    private List<String> labels;
    private List<Double> data;
    private List<String> backgroundColors;
    private List<String> questions;
    private List<List<Integer>> values; // ✅ added for grouped data
    private String title; // ✅ add this line

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

    public List<String> getQuestions() { return questions; }
    public void setQuestions(List<String> questions) { this.questions = questions; }

    public List<List<Integer>> getValues() { return values; }
    public void setValues(List<List<Integer>> values) { this.values = values; }
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
    
    
}
