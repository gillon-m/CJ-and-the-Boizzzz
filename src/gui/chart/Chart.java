package gui.chart;

import org.jfree.chart.ChartPanel;

/**
 * Charts represent different type of charts that can be created and updated at any time
 * @author Gillon Manalastas
 *
 */
public interface Chart {
	/**
	 * Gets the panel where the chart is drawn
	 * @return The chart panel
	 */
	public ChartPanel getChart();
	
	/**
	 * Update values in the chart
	 */
	public void updateChart();
}
