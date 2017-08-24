package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.HierarchicalLayout;
import org.graphstream.ui.swingViewer.ViewPanel;
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
	private int _usedVerticesListSize=0; //used to minimize checking and speed up visualization

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
		ViewPanel view = viewer.addDefaultView(false);
		 viewer.enableAutoLayout();
		//_taskGraph.display();
		_visualiser.taskGraphPanel.add(view, BorderLayout.CENTER);
		graph.Graph inputGraph = graph.Graph.getInstance();
		if(inputGraph!=null){
			for(Vertex v: inputGraph.getVertices()){
				String vertex = v.getName();
				if(!vertex.equals("-")){
					_taskGraph.addNode(vertex).addAttribute("ui.label", vertex);
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

	}

	/**
	 * Updates the colour of the nodes in the graph based on their status
	 */
	public void updateNodeColour(){
		for( Node n : _taskGraph.getEachNode() ){
			n.addAttribute("ui.style", "fill-color: grey; size: 20px, 20px;");
		}
		
		List<Vertex> allUsedVertices = _data.getAllUsedVertices();
		_usedVerticesListSize=_data.getAllUsedVertices().size();
		for(Vertex v: allUsedVertices){
			Node node = _taskGraph.getNode(v.getName());
			node.addAttribute("ui.style", "fill-color: green; size: 20px, 20px;");
		}

		String lastUsedVertexName = _data.getLastUsedVertex().getName();
		if(!lastUsedVertexName.equals("-")){
			Node lastUsedNode = _taskGraph.getNode(lastUsedVertexName);
			lastUsedNode.addAttribute("ui.style", "fill-color: red; size: 20px, 20px;");
			if(_data.isFinished()){
				lastUsedNode.addAttribute("ui.style", "fill-color: green; size: 20px, 20px;");
			}
		}
	}
	@Override
	public void update() {
		_visualiser.schedulerText.setText("Vertex = " +_data.getCurrentSchedule().getLastUsedVertex().getName() + 
				"\t|Time Taken = " + _data.getCurrentSchedule().getTimeOfSchedule() + "\n" + _data.getCurrentSchedule().toString());
		//update count
		_visualiser.scheduleCountLabel.setText("Schedules created: "+_data.getTotalNumberOfCreatedSchedules());
		//update time
		_calendar.setTimeInMillis(_data.getElapsedTime());
		_visualiser.timeElapsedLabel.setText("Time Elapsed: "+_timeFormat.format(_calendar.getTimeInMillis()));
		//update node checked
		updateNodeColour();
	}
}
