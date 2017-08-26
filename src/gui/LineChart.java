package gui;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import data.Data;

/**
 * An example to show how we can create a dynamic chart.
 */
public class LineChart {

	/** The time series data. */
	private TimeSeries series;
	/** Timer to refresh graph after every 1/4th of a second */
	private Data _data;
	final ChartPanel chartPanel;

	/**
	 * Constructs a new dynamic chart application.
	 *
	 * @param title  the frame title.
	 */	
	public LineChart() {
		_data=Data.getInstance();
		this.series = new TimeSeries("Total schedule");
		final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
		final JFreeChart chart = createChart(dataset);
		chart.setBackgroundPaint(Color.WHITE);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
	}
	
	/**
	 * Creates a sample chart.
	 *
	 * @param dataset  the dataset.
	 *
	 * @return A sample chart.
	 */
	public JFreeChart createChart(final XYDataset dataset) {
		final JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
				"Schedules Created",
				"Schedules",
				"Value",
				dataset,
				false,
				true,
				false
				);

		final XYPlot plot = lineChart.getXYPlot();
		lineChart.setBackgroundPaint(new Color(0x382721));
		plot.setBackgroundPaint(new Color(0xffffff));
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.lightGray);
		
		ValueAxis xaxis = plot.getDomainAxis();
		xaxis.setTickLabelsVisible(false);
		xaxis.setAutoRange(true);
		ValueAxis yaxis = plot.getRangeAxis();
		yaxis.setTickLabelsVisible(false);
		yaxis.setAutoRange(true);

		return lineChart;
	}

	public void actionPerformed () {
		int totalCreated = _data.getTotalNumberOfCreatedSchedules();
		this.series.addOrUpdate(new Millisecond(), totalCreated);
	}
	
	public ChartPanel getChart(){
		return chartPanel;
	}
}  