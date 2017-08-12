package SearchSpace;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import App.Edge;
import App.Graph;
import App.Vertex;
/**
 * 
 * Class to represent Schedule of tasks
 * It manages arbitrary number of processors 
 * 
 * @author Alex Yoo & Gillon Manalastas
 *
 */
public class Schedule {
	private int _numberOfProcessors;		// Number of Processors that is going to be used for scheduling
	private List<Processor> _processors;	// List of Processors that contains the tasks
	private Graph _graph;					// graph representation of the input data
	
	private List<Vertex> _usedVertices;		// stores the used Vertices for this schedule
	private Vertex _lastUsedVertex;			// the last Vertex that was added onto this Schedule
	private List<Vertex> _childVertices;	// List of children vertices of the bottom-most vertices
	
	private List<Schedule> _childSchedules;	//stores the children of the schedule
	private List<Schedule> _parentSchedules;//stores the parents the schedule 
	
	public Schedule(Graph g, int numberOfProcessors){
		_graph=g;
		_processors = new ArrayList<Processor>();
		for(int i=0;i<numberOfProcessors;i++){
			_processors.add(new Processor(_graph));
		}
	}	
	public Schedule() {
	}

	public List<Schedule> makeChildSchedules(){
		Schedule s = this.copySchedule();
		
		return _childSchedules;
	}
	
	/**
	 * Copies the schedule to another schedule
	 * @return Schedule that has the same fields as the current schedule
	 */
	private Schedule copySchedule(){
		Schedule s = new Schedule();
		s._numberOfProcessors = this._numberOfProcessors;
		s._processors = new ArrayList<Processor>(this.copyProcessors());
		s._graph = this._graph;
		s._usedVertices = new LinkedList<Vertex>(this._usedVertices);
		s._lastUsedVertex = this._lastUsedVertex;
		s._childVertices = new LinkedList<Vertex>(this._childVertices);
		s._childSchedules = new LinkedList<Schedule>(this._childSchedules);
		s._parentSchedules = new LinkedList<Schedule>(this._parentSchedules);
		return s;
	}
	
	/**
	 * Copies the processors to another list of processors
	 * @return Schedule that has the same fields as the current schedule
	 */
	private List<Processor> copyProcessors(){
		List<Processor> processors = new ArrayList<Processor>();
		for(Processor p: _processors){
			processors.add(new Processor(p));
		}
		return processors;
	}
	
	/**
	 * Adds a vertex to a specified processor number
	 * @param v - Vertex to add
	 * @param processorNumber - number of the specified processor
	 */
	public void addVertexToProcessor(Vertex v, int processorNumber){
	}
	
	public void addChild(Schedule s){
		_childSchedules.add(s);
	}
	public void addParent(Schedule s){
		_parentSchedules.add(s);
	}
	
	//Getter Methods
	public List<Vertex> getUsedVertices(){
		return _usedVertices;
	}
	public List<Vertex> getChildVertices(){
		return _childVertices;
	}
}