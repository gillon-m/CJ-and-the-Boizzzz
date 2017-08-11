package SearchSpace;

import java.util.ArrayList;
import java.util.List;

import App.Edge;
import App.Graph;
import App.Vertex;
/**
 * 
 * Class to represent Schedule of tasks
 * It manages arbitrary number of processors 
 * 
 * @author Alex Yoo
 *
 */
public class Schedule {
	private int _numberOfProcessors;		// Number of Processors that is going to be used for scheduling
	private List<Processor> _processors;	// List of Processors that contains the tasks
	private Graph _graph;					// input data
	private List<Vertex> _usedVertices;		// stores the used Vertices for this schedule
	private Vertex _lastUsedVertex;			// the last Vertex that was added onto this Schedule
	private List<Vertex> _childVertices;	// List of children vertices to the last used Vertex 
	
	public Schedule(Graph g, int numberOfProcessors){
	}
}
