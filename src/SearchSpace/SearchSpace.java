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
	
	private final static String START_NODE_NAME = "IMAGINARY_STARTING_NODE";
	
	public SearchSpace(Graph g, int noOfProcessors) {
		_numberOfProcessors = noOfProcessors;
		_graph = g;
		_schedules = new ArrayList<Schedule>();
		_schedulesEdges = new ArrayList<ScheduleEdge>();
		
		_startVertex = new Vertex(START_NODE_NAME, 0);
		_graph.addVertex(_startVertex);

	}

	public void makeSearchSpace() {
		findRootVertices();
		Schedule s = new Schedule(_graph, _numberOfProcessors);
	}
	
	/**
	 * Helper method to find the root nodes of a graph. Sets the parent of the root
	 * nodes to be the _startVertex. Updates the children of _startVertex to be the root nodes. Also updates the edges of the graph
	 */
	private void findRootVertices() {
		List<Vertex> rootVertices = new LinkedList<Vertex>();
		List<Vertex> vertices = _graph.getVertices();
		for(int i = 0; i<vertices.size()-1;i++){ //looks at all vertices except the last vertex with is the START
			Vertex v = vertices.get(i);
			if(v.getParents().size()==0){
				_startVertex.addChild(v);
				v.addParent(_startVertex);
				_graph.addEdge(new Edge(_startVertex, v, 0));
			}
		}
	}

	public String outputToPrint() {
		// TODO Auto-generated method stub
		return null;
	}
}
