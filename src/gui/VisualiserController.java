package gui;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import data.Data;
import data.StopWatch;
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
	private StopWatch _stopWatch;
	private Schedule _currentSchedule;

	public VisualiserController(){
		_data=Data.getInstance();
		_visualiser = new Visualiser();
		_stopWatch=StopWatch.getInstance();
		//initialiseTimer();
		createGraphVisual();
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

		List<Vertex> allUsedVertices = _currentSchedule.getAllUsedVertices();
		for(Vertex v: allUsedVertices){
			if(!v.getName().equals("-")){//check if imaginary node
				Node node = _taskGraph.getNode(v.getName());
				node.addAttribute("ui.style", "fill-color: green; size: 20px, 20px;");
			}
		}

		String lastUsedVertexName = _currentSchedule.getLastUsedVertex().getName();
		if(!lastUsedVertexName.equals("-")){//check if imaginary node
			Node lastUsedNode = _taskGraph.getNode(lastUsedVertexName);
			lastUsedNode.addAttribute("ui.style", "fill-color: red; size: 20px, 20px;");
		}

	}

	/**
	 * Displays the schedule
	 */
	private void displaySchedule(){
		Schedule bestSchedule = _data.getBestSchedule();
		_visualiser.schedulerText.setText("Vertex = " +bestSchedule.getLastUsedVertex().getName() + 
				"\t|Time Taken = " + bestSchedule.getTimeOfSchedule() + "\n" + bestSchedule.toString());

	}

	/**
	 * Colour all nodes green
	 */
	private void finishAllNodes(){
		for( Node n : _taskGraph.getEachNode() ){
			n.addAttribute("ui.style", "fill-color: green; size: 20px, 20px;");
		}
	}
	@Override
	public void update() {
		_currentSchedule=_data.getCurrentSchedule();
		if(_currentSchedule!=null){
			displaySchedule();
			updateNodeColour();
		}
		else{
			finishAllNodes();
		}
		//update count
		_visualiser.scheduleCountLabel.setText("Schedules created: "+_data.getTotalNumberOfCreatedSchedules());
		//update time
		_visualiser.timeElapsedLabel.setText("Time Elapsed: "+_stopWatch.getElapsedTime());
		//update node checked
		
		_data.clearSchedules();
	}
}
