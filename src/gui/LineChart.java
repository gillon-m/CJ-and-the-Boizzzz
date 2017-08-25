package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

//import javax.swing.Timer;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
import data.StopWatch;

/**
 * An example to show how we can create a dynamic chart.
 */
@SuppressWarnings("serial")
//public class LineChart extends ApplicationFrame implements ActionListener {
public class LineChart{// implements ActionListener {

	/** The time series data. */
	private TimeSeries series;
	/** Timer to refresh graph after every 1/4th of a second */
	//private Timer timer = new Timer(250, this);
	private Data _data;
	private StopWatch _stopWatch;
	final ChartPanel chartPanel;

	/**
	 * Constructs a new dynamic chart application.
	 *
	 * @param title  the frame title.
	 */
	
	/* private TimerTask _timerTask = new TimerTask() {
		@Override
		public void run() {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						actionPerformed();
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	};*/
	
	public LineChart() {
		_data=Data.getInstance();
		this.series = new TimeSeries("Total schedule");
		final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
		final JFreeChart chart = createChart(dataset);
		//timer.setInitialDelay(1000);
		chart.setBackgroundPaint(Color.WHITE);
		chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
		//Timer timer = new Timer();
		//timer.scheduleAtFixedRate(_timerTask, 1, 1);
		//timer.start()
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

	//public void actionPerformed(final ActionEvent e) {
	public void actionPerformed() {
		int totalCreated = _data.getTotalNumberOfCreatedSchedules();
		this.series.add(new Millisecond(), totalCreated);
		//this.series.add(new FixedMillisecond(_stopWatch.getElapsedTimeDate()), totalCreated);
		//System.out.println("Current Time in Milliseconds = " + elapsedTime+", Current Value : "+totalCreated);
	}
	
	public ChartPanel getChart(){
		return chartPanel;
	}
}  