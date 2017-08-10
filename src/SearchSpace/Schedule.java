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
	
	
	/**
	 * If not specified, the program assumes there are two processors
	 * 
	 * @param g is for input data
	 */
	public Schedule(Graph g) {
		this(g,2);
	}
	public Schedule(Graph g, int n) {
		_graph = new Graph(new ArrayList<Vertex>(g.getVertices()), new ArrayList<Edge>(g.getEdges()));
		_numberOfProcessors = n;
		_usedVertices = new ArrayList<Vertex>();
		_childVertices = new ArrayList<Vertex>();
		_processors = new ArrayList<Processor>();
		for(int i = 0; i < n; i++) {
			_processors.add(new Processor(_graph));
		}
	}
	public Schedule(Schedule s) {
		_numberOfProcessors = s.getNumberOfProcessors();
		_processors = new ArrayList<Processor>();
		for(Processor p :  s.getAllProcessors()) {
			Processor processor = new Processor(p);
			_processors.add(processor);
		}
		_graph = new Graph(new ArrayList<Vertex>(s.getGraph().getVertices()), new ArrayList<Edge>(s.getGraph().getEdges()));
		_usedVertices = new ArrayList<Vertex>(s.getAllUsedVertices());
		_childVertices = new ArrayList<Vertex>(s.getChildVertices());
	}
	
	

	/**
	 * This method generates all possible Schedule for the input vertex
	 * After generating each schedule, 
	 * 
	 * @param v
	 * @return
	 */
	public Schedule[] generateAllPossibleSchedule(Vertex v) {
		Schedule[] allSchedule = new Schedule[_numberOfProcessors];
		for(int i = 0; i < _numberOfProcessors; i++) {
			allSchedule[i] = makeScheduleAndAddToSpecifiedProcessor(i,v); 
			allSchedule[i].updateUsedVertices(v);
			allSchedule[i].updateChildVertices(v);
		}
		return allSchedule;
	}
	
	private void updateUsedVertices(Vertex v) {
		_lastUsedVertex = new Vertex(v.getName(),v.getWeight());
		_usedVertices.add(v);
	}
	/**
	 * Updates the list of child vertices with the new vertex
	 * 
	 * @param v
	 */
	private void updateChildVertices(Vertex v) {
		_childVertices.remove(v);
		List<Edge> allEdges = new ArrayList<Edge>(_graph.getEdges());
		for(Edge e : allEdges) {
			if(e.getSource().equals(v) && !_childVertices.contains(e.getDestination())) {
				if(this.checkChildVertexDependency(e.getDestination())) {
					_childVertices.add(e.getDestination());
				}
			}
		}
	}
	/**
	 * This method checks for dependencies of the specified vertex
	 * that still needs to be gone through before tasking this vertex
	 * 
	 * @param childVertex
	 * @return
	 */
	private boolean checkChildVertexDependency(Vertex childVertex) {
		List<Edge> allEdges = new ArrayList<Edge>(_graph.getEdges());
		for(Edge e : allEdges) {
			if(e.getDestination().equals(childVertex) && !_usedVertices.contains(e.getSource())) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 	This method copies the Parent Schedule to be used as a base for 
	 * 	creating child Vertex's schedules
	 * 	
	 *  It checks the Dependencies of the vertex to see the most efficient
	 *  place to place the input Vertex's task in the specified processor
	 *  
	 *  If there are dependencies that must be settled checkIfEmptyTimeSlotsAreNeededFromDepenedencies
	 *  method will handle it
	 *  
	 * @param processor
	 * @param v
	 * @return
	 */
	private Schedule makeScheduleAndAddToSpecifiedProcessor(int processor, Vertex v) {
		Schedule s = new Schedule(this);
		s.checkIfEmptyTimeSlotsAreNeededFromDepenedencies(s,v, processor);
		s.addVertexToSpecifiedProcessor(processor, v);
		return s;
	}
	/**
	 * Hands over the task of adding the task into the processor
	 * to the Processor class
	 * 
	 * @param processor
	 * @param v
	 */
	private void addVertexToSpecifiedProcessor(int processor, Vertex v) {
		_processors.get(processor).addTimeSlotIntoProcessor(v);
	}
	
	/**
	 * Checks dependencies of the vertex and determine the best place to 
	 * place the task in the specified processor.
	 * If required it will generate empty spaces so that tasks can be 
	 * placed in easily
	 * 
	 * @param v
	 */
	private void checkIfEmptyTimeSlotsAreNeededFromDepenedencies(Schedule s, Vertex v, int processor) {
		List<Vertex> dependentVertices = new ArrayList<Vertex>(getDependentVertices(v));
		int maxTime = 0;
		int maxTimeProcessor = -1;
		Vertex maxTimeVertex = null; 
		
		for(Vertex vertex : dependentVertices) {
			for(Processor p : new ArrayList<Processor>(s.getAllProcessors())) {
				int timeTakenToThisVertex = p.getTime(vertex);
				// if dependent vertex and specified vertex is in different processor 
				if(!p.equals(s.getProcessor(processor)) && timeTakenToThisVertex!=0) {		//timeTakenToThisVertex equals zero when vertex is not found
					// check switch cost from dependent vertex is bigger than the largest time in the processor
					if(timeTakenToThisVertex+this.switchProcessorCost(vertex, v) > maxTime) {
						maxTime = timeTakenToThisVertex +this.switchProcessorCost(vertex, v);
						maxTimeProcessor = s.getAllProcessors().indexOf(p);
						maxTimeVertex = vertex;
					}
				} else {
					if(timeTakenToThisVertex > maxTime) {
						maxTime = timeTakenToThisVertex;
						maxTimeProcessor = s.getAllProcessors().indexOf(p);
						maxTimeVertex = vertex;
					}
				}
			}
		}
		Processor p = s.getProcessor(processor);
		if(maxTime > p.getLatestTime()) {
			p.addEmptyTimeSlots(maxTime-p.getLatestTime());
		}
	}
	
	/**
	 * Get a list of vertexes that the input v is dependent on
	 * @param v
	 * @return list of vertices that input Vertex is dependent on
	 */
	private List<Vertex> getDependentVertices(Vertex v){
		List<Vertex> dependentVertices = new ArrayList<Vertex>();
		List<Vertex> ver = new ArrayList<Vertex>(_graph.getVertices());
		List<Edge> edge = new ArrayList<Edge>(_graph.getEdges());
		
		for(Edge e : edge) {
			if(e.getDestination().equals(v)) {
				dependentVertices.add(e.getSource());
			}
		}
		return dependentVertices;
	}
	
	/**
	 * This method returns back the cost of switching processors
	 * for a given source and destination task
	 * 
	 * 
	 * @param fromVertex
	 * @param toVertex
	 * @return
	 */
	private int switchProcessorCost(Vertex fromVertex, Vertex toVertex) {
		List<Edge> allEdges = new ArrayList<Edge>(_graph.getEdges());
		int weight = -9000;		
		for(Edge e : allEdges) {
			if(e.getDestination().equals(toVertex) && e.getSource().equals(fromVertex)) {
				weight = e.getWeight();
				break;
			}
		}
		return weight;
	}
	
	
	/**
	 * get methods
	 */
	public int getTimeOfSchedule() {
		int timeTakesToDoSchedule = 0;
		for(Processor p : _processors) {
			if(p.getLatestTime() > timeTakesToDoSchedule) {
				timeTakesToDoSchedule = p.getLatestTime();
			}
		}
		return timeTakesToDoSchedule;
	}
	public int getNumberOfProcessors() {
		return _numberOfProcessors;
	}
	public List<Processor> getAllProcessors() {
		return _processors;
	}
	public Processor getProcessor(int n) {
		return _processors.get(n);
	}
	public List<Vertex> getAllUsedVertices(){
		return _usedVertices;
	}
	private Graph getGraph() {
		return  _graph;
	}
	public Vertex getLastUsedVertex() {
		return _lastUsedVertex;
	}
	public List<Vertex> getChildVertices(){
		return _childVertices;
	}
	
	/**
	 * To adjust the format and the data that should be returned
	 */
	@Override
	public String toString() {
		String schedule = "";
		for(int i = 0; i < _numberOfProcessors; i++) {
			schedule += "Processor " + i + ": " + _processors.get(i).toString()  + "\n";
		}
		return schedule;
	}
}
