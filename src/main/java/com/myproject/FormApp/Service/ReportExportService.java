package com.myproject.FormApp.Service;

import com.myproject.FormApp.Dto.ChartVisualizationDTO;
import com.myproject.FormApp.Dto.ChartDataDTO;
import org.springframework.stereotype.Service;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.*;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.jfree.chart.ChartUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;

@Service
public class ReportExportService {

    /**
     * Export all charts to a professional PDF
     */
   public byte[] exportProfessionalPDF(List<ChartVisualizationDTO> visuals, ChartDataDTO rangeChartDTO) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
    PdfWriter.getInstance(document, out);
    document.open();

    Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(0, 70, 140));
    Font chartTitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);

    document.add(new Paragraph("📊 Feedback Report Dashboard", titleFont));
    document.add(new Paragraph(" ")); // spacing

    // Use 1-column table for single chart per row
    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(100);
    table.setSpacingBefore(10f);

    // Add all visual charts
    for (ChartVisualizationDTO chart : visuals) {
        Image chartImage = generateChartImage(chart);
        chartImage.setAlignment(Element.ALIGN_CENTER); // center the chart

        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingBottom(20f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER); // center the cell content
        cell.addElement(new Paragraph(chart.getTitle(), chartTitleFont));
        cell.addElement(chartImage);

        table.addCell(cell);
        document.add(table);
        table.deleteBodyRows(); // clear for next chart
    }

    // Add range distribution chart
    if (rangeChartDTO != null) {
        Image rangeChartImage = generateRangeDistributionChartImage(rangeChartDTO);
        rangeChartImage.setAlignment(Element.ALIGN_CENTER);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingBottom(20f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.addElement(new Paragraph(rangeChartDTO.getTitle(), chartTitleFont));
        cell.addElement(rangeChartImage);

        table.addCell(cell);
        document.add(table);
    }

    document.close();
    return out.toByteArray();
}


    /**
     * Generate chart image for bar, line, or pie chart
     */
    private Image generateChartImage(ChartVisualizationDTO chart) throws Exception {
        JFreeChart jFreeChart;

        switch (chart.getChartType().toLowerCase()) {
            case "bar":
                jFreeChart = createBarChart(chart);
                break;
            case "line":
                jFreeChart = createLineChartWithQuestions(chart);
                break;
            case "pie":
                jFreeChart = createPieChart(chart);
                break;
            default:
                jFreeChart = createBarChart(chart);
                break;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, jFreeChart, 600, 350);

        Image img = Image.getInstance(baos.toByteArray());
        img.scaleToFit(600, 350);
        img.setAlignment(Element.ALIGN_CENTER);
        return img;
    }

    /**
     * Range distribution chart image
     */
    public Image generateRangeDistributionChartImage(ChartDataDTO rangeChart) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < rangeChart.getLabels().size(); i++) {
            String rating = rangeChart.getLabels().get(i);
            for (int j = 0; j < rangeChart.getQuestions().size(); j++) {
                dataset.addValue(rangeChart.getValues().get(j).get(i), "Rating " + rating, rangeChart.getQuestions().get(j));
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                rangeChart.getTitle(),
                "Questions",
                "Number of Students",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        customizePlot(chart.getCategoryPlot());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(baos, chart, 700, 350);

        Image img = Image.getInstance(baos.toByteArray());
        img.scaleToFit(700, 350);
        img.setAlignment(Element.ALIGN_CENTER);
        return img;
    }

    private JFreeChart createBarChart(ChartVisualizationDTO dto) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < dto.getLabels().size(); i++) {
            dataset.addValue(dto.getValues().get(i), "Score", dto.getLabels().get(i));
        }
        JFreeChart chart = ChartFactory.createBarChart(
                dto.getTitle(),
                "Label",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );
        customizePlot(chart.getCategoryPlot());
        return chart;
    }

    private JFreeChart createLineChartWithQuestions(ChartVisualizationDTO dto) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < dto.getLabels().size(); i++) {
            dataset.addValue(dto.getValues().get(i), "Average", dto.getLabels().get(i));
        }
        JFreeChart chart = ChartFactory.createLineChart(
                dto.getTitle(),
                "Questions",
                "Average Rating",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        customizePlot(plot);

        // Highlight line with shapes
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesPaint(0, new Color(255, 99, 132));
        plot.setRenderer(renderer);

        return chart;
    }

    private JFreeChart createPieChart(ChartVisualizationDTO dto) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < dto.getLabels().size(); i++) {
            dataset.setValue(dto.getLabels().get(i), dto.getValues().get(i));
        }
        JFreeChart chart = ChartFactory.createPieChart(dto.getTitle(), dataset, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        return chart;
    }

    private void customizePlot(CategoryPlot plot) {
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setOutlineVisible(false);
    }
}
