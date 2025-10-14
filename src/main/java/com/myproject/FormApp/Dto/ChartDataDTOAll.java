package com.myproject.FormApp.Dto;

import java.util.List;

public class ChartDataDTOAll {
    private String title;
    private List<String> labels;        // Rating scale (1–5)
    private List<String> questions;     // Question titles
    private List<List<Integer>> values; // Each question’s rating counts

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }
    public List<String> getQuestions() { return questions; }
    public void setQuestions(List<String> questions) { this.questions = questions; }
    public List<List<Integer>> getValues() { return values; }
    public void setValues(List<List<Integer>> values) { this.values = values; }
}
