package SearchSpace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import App.Edge;
import App.Graph;
import App.Vertex;
/**
 * This Class uses the Schedule Class and Processor Class 
 * to make schedules using the information from the Graph Variable which 
 * contains all the list of vertex and edges. 
 * This Class manages the created schedules. 
 * 
 * @author Alex Yoo
 *
 */
public class SearchSpace {
	private Graph _graph;
	private int _numberOfProcessors;				// Could ask how many processor the user want, but for now it is always 2 processors
	private List<Schedule> _schedules;				// _schedules variable contains all the possible schedules that are created
	private List<ScheduleEdge> _schedulesEdges;		// _schedulesEdges variable store the relationships between schedules
	private Vertex _startVertex;					//imaginary vertex with no weight used to initialize the searchSpace 
	private List<Schedule> _openSchedules; 			//stores the schedules that need to be checked
	private List<Schedule> _closedSchedules;		//stores the schedules that have been checked
	
	
	public SearchSpace(Graph g, int noOfProcessors) {
		_numberOfProcessors = noOfProcessors;
		_graph = g;
		_schedules = new ArrayList<Schedule>();
		_schedulesEdges = new ArrayList<ScheduleEdge>();
		
		
		_startVertex = new Vertex("START", 0);
		_graph.addVertex(_startVertex);

	}

	public void makeSearchSpace() {
		findRootVertices();

	}
	

	public String outputToPrint() {
		return null;
	}

	
	/**
	 * Helper method to find the root nodes of a graph. Sets the parent of the root
	 * node to the _startVertex. Updates the children of _startVertex to be the root nodes
	 */
	private void findRootVertices() {
		List<Vertex> rootVertices = new LinkedList<Vertex>();
		for(Vertex v: _graph.getVertices()){
			if(v.getParents().size()==0){
				_startVertex.addChild(v);
				v.addParent(_startVertex);
				_graph.addEdge(new Edge(_startVertex, v, 0));
			}
		}
	}

}
