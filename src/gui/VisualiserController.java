package gui;

import java.awt.BorderLayout;
import java.util.List;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import graph.Vertex;
import gui.chart.Chart;
import gui.chart.GanttChart;
import gui.chart.LineChart;
import gui.data.Data;
import gui.data.StopWatch;
import scheduler.Schedule;

/**
 * VisualiserController controls the information displayed on the GUI from the scheduling algorithm
 * @author Gillon Manalastas, Brad Miller
 */
public class VisualiserController{
	private Visualiser _visualiser;
	private org.graphstream.graph.Graph _taskGraph;
	private StopWatch _stopWatch;
	private Data _data;
	private Schedule _currentSchedule;
	private Schedule _bestSchedule;
	private Schedule _previousBestSchedule;
	private Chart _lineChart;
	private Chart _ganttChart;
	private boolean firstSchedule = true;

	public VisualiserController(){
		_visualiser = new Visualiser();
		_data=Data.getInstance();
		_stopWatch=StopWatch.getInstance();
		_lineChart=new LineChart();
		_visualiser.lineChartPanel.add(_lineChart.getChart(), BorderLayout.CENTER);
		_visualiser.lineChartPanel.validate();
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
	 * Colour all nodes green
	 */
	private void finishAllNodes(){
		for( Node n : _taskGraph.getEachNode() ){
			n.addAttribute("ui.style", "fill-color: green; size: 20px, 20px;");
		}
	}

	private void displaySchedule() {

		if (firstSchedule) {
			_ganttChart = new GanttChart();
			firstSchedule = false;
		}

		if(_previousBestSchedule==null){
			_previousBestSchedule=_bestSchedule;
		}
		else if(!_previousBestSchedule.equals(_bestSchedule)){
			_ganttChart.updateChart();
			_visualiser.schedulePanel.removeAll();
			_visualiser.schedulePanel.add(_ganttChart.getChart(), BorderLayout.CENTER);
			_visualiser.schedulePanel.validate();
			_previousBestSchedule=_bestSchedule;
		}
	}
	public void update(boolean isOptimal) {
		_currentSchedule=_data.getCurrentSchedule();
		_bestSchedule=_data.getBestSchedule();
		if (_currentSchedule != null) {
			if (isOptimal) {
				finishAllNodes();
			} else {
				updateNodeColour();
			}
			displaySchedule();
		}
		//update timer
		_visualiser.timeElapsedLabel.setText(_stopWatch.getElapsedTime());
		//update count
		_visualiser.scheduleCountLabel.setText(""+_data.getTotalNumberOfCreatedSchedules());
		//update line chart
		_lineChart.updateChart();
	}
}
