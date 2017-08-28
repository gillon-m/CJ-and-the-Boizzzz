package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

import data.Data;
import graph.Vertex;
import scheduler.Processor;

/**
 * A class representing a visualised version of a schedule.
 * @author Brad Miller
 *
 */
public class GanttChart {

	private Data _data;
	private int _numProcessors;
	private JFrame frame = new JFrame("Schedule");
	private ChartPanel _ganttChart;
	
	public GanttChart() {
		_data = Data.getInstance();
		_numProcessors = _data.getCurrentSchedule().getNumberOfProcessors();
		TaskSeriesCollection dataset = createInitialDataset();
        createVisualSchedule(dataset);
    }
	
	/**
	 * Updates the dataset for the current schedule
	 * @return dataset
	 */
	public TaskSeriesCollection updateDataset() {
		TaskSeriesCollection collection = new TaskSeriesCollection();
		
    	for (int i = 0; i < _numProcessors; i++) {
//    		Processor processor = _data.getCurrentSchedule().getProcessor(i);
    		Processor processor = _data.getBestSchedule().getProcessor(i);
    		TaskSeries taskSeries = new TaskSeries("Processor " + i);
    		
    		Task task = new Task("Processor " + i, new SimpleTimePeriod(0,processor.getLatestTime()));
    		int timeTakenForProcessor = 0;
    		for (Vertex v : processor.getScheduleOfProcessor()) {
    			if (!v.getName().equals("-")) {
    				Task subTask = new Task("Task " + v.getName(), new SimpleTimePeriod(timeTakenForProcessor,timeTakenForProcessor + v.getWeight()));
    				//task.addSubtask(subTask);
    				taskSeries.add(subTask);
    			}
    			timeTakenForProcessor += v.getWeight();
    		}
    		//taskSeries.add(task);
    		collection.add(taskSeries);
    	}
    	
    	return collection;
	}
	
	/**
	 * Creates an empty dataset initialised with each processor 
	 * @return dataset
	 */
    public TaskSeriesCollection createInitialDataset() {
    	TaskSeriesCollection collection = new TaskSeriesCollection();
    	
    	for (int i = 0; i < _numProcessors; i++) {
    		TaskSeries taskSeries = new TaskSeries("Processor " + i);
    		Task task = new Task("Processor " + i, new SimpleTimePeriod(0,0));
    		taskSeries.add(task);
    		collection.add(taskSeries);
    	}
    	
    	return collection;
    }

    /**
     * Creates chart from dataset
     * @param dataset
     * @return JFreeChart
     */
    private JFreeChart createChart(final TaskSeriesCollection dataset) {
        final JFreeChart chart = ChartFactory.createGanttChart("Best Current Schedule", "Tasks", "Time", dataset, true, true, false);
        CategoryPlot plot = chart.getCategoryPlot();
        DateAxis axis = (DateAxis) plot.getRangeAxis();
		plot.setRangeGridlinesVisible(true);

        axis.setDateFormatOverride(new SimpleDateFormat("S"));
        
        //Now set task colors to help distinguish tasks - Uncomment the following lines to use subtask coloring
        //MyRenderer renderer = new MyRenderer(dataset);
        //plot.setRenderer(renderer);
        
        return chart;
    }
	
    /**
     * Called from VisualiserController to update current schedule
     */
	public void actionPerformed() {
		TaskSeriesCollection dataset = updateDataset();
		createVisualSchedule(dataset);
	}
	
	public ChartPanel getChart(){
		return _ganttChart;
	}
	/**
	 * Sets JFrame panel for schedule
	 * @param dataset
	 */
	public void createVisualSchedule(TaskSeriesCollection dataset) {
		JFreeChart chart = createChart(dataset);
		final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        _ganttChart=chartPanel;
	}
}
