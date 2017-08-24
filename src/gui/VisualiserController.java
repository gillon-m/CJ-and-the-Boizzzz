package gui;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.HierarchicalLayout;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import graph.Vertex;
import scheduler.Schedule;

/**
 * VisualiserController controls the information displayed on the GUI from the scheduling algorithm
 * @author Gillon Manalastas
 */
public class VisualiserController implements ScheduleListener{
	private Visualiser _visualiser;
	private Calendar _calendar;
	private DateFormat _timeFormat;
	private Data _data;
	private Timer _timer;
	private org.graphstream.graph.Graph _taskGraph;
	
	/**
	 * Timer task object used to track the total elapsed time
	 */
	TimerTask _timerTask = new TimerTask() {
		@Override
		public void run() {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						if(!_data.isFinished()){ //check is the scheduler has finished
							_data.setCurrentTime();					
						}
						else{ //stop timer when scheduler has finished
							_timer.cancel();
							_timer.purge();
						}
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	public VisualiserController(Data data){
		_data=data;
		_visualiser = new Visualiser();
		initialiseTimer();
		createGraphVisual();
	}
	
	/**
	 * Initialises the timer
	 */
	private void initialiseTimer(){
		_calendar = Calendar.getInstance();
		_timeFormat = new SimpleDateFormat("mm:ss.SSS");
		_timer = new Timer();
		//set timer to 0;
		_calendar.set(Calendar.MILLISECOND, 0);
		_calendar.set(Calendar.SECOND, 0);
		_calendar.set(Calendar.MINUTE, 0);
		_calendar.set(Calendar.HOUR_OF_DAY, 10);
		_timer.scheduleAtFixedRate(_timerTask, 1, 1); //invoke timer every millisecond
	}
	
	
	/**
	 * Creates a graphstream Graph and converts the graph.Graph data structure into org.graphstream.graph.Graph
	 */
	private void createGraphVisual(){
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		_taskGraph = new SingleGraph("Task Graph");
		Viewer viewer = new Viewer(_taskGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		View view = viewer.addDefaultView(false);
		
		graph.Graph inputGraph = graph.Graph.getInstance();
		if(inputGraph!=null){
			for(Vertex v: inputGraph.getVertices()){
				String vertex = v.getName();
				if(!vertex.equals("-")){
					_taskGraph.addNode(vertex);
				}
			}
			for(graph.Edge e: inputGraph.getEdges()){
				String source = e.getSource().getName();
				String destination = e.getDestination().getName();
				if(!source.equals("-")&&!destination.equals("-")){
					_taskGraph.addEdge(source+destination, source, destination, true);

				}
			}
		}
		_visualiser.taskGraphPanel.add((Component) view);
	}
	
	
	@Override
	public void update() {
		_visualiser.schedulerText.setText("Vertex = " +_data.getCurrentSchedule().getLastUsedVertex().getName() + 
				"\t|Time Taken = " + _data.getCurrentSchedule().getTimeOfSchedule() + "\n" + _data.getCurrentSchedule().toString());
		
		_visualiser.scheduleCountLabel.setText("Schedules created: "+_data.getTotalNumberOfCreatedSchedules());
		_calendar.setTimeInMillis(_data.getElapsedTime());
		_visualiser.timeElapsedLabel.setText("Time Elapsed: "+_timeFormat.format(_calendar.getTimeInMillis()));
	}
}
