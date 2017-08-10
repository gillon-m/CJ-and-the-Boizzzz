package SearchSpace;

import java.util.ArrayList;
import java.util.HashMap;
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
	private final int _numberOfProcessors = 2;		// Could ask how many processor the user want, but for now it is always 2 processors
	private List<Schedule> _schedules;				// _schedules variable contains all the possible schedules that are created
	private List<ScheduleEdge> _schedulesEdges;		// _schedulesEdges variable store the relationships between schedules
	
	public SearchSpace(Graph g) {
		_graph = new Graph(new ArrayList<Vertex>(g.getVertices()), new ArrayList<Edge>(g.getEdges()));
		_schedules = new ArrayList<Schedule>();
		_schedulesEdges = new ArrayList<ScheduleEdge>();
	}
	
	/**
	 * This method is called to initiate the process of creating all the possible schedules
	 * Implementation may change depending on algorithm used.
	 * 
	 * Caution: This method currently assumes the first Vertex in the list is the root Vertex 
	 * 
	 */
	public void makeSearchSpace() {
		List<Vertex> allVertices = new ArrayList<Vertex>(_graph.getVertices());
		Schedule s = new Schedule(_graph, _numberOfProcessors);
		Schedule[] schedules = new Schedule[_numberOfProcessors];
		
		// Generates all the possible schedules for the Root Vertex
		schedules = s.generateAllPossibleSchedule(allVertices.get(0));
		
		// Using this line will create schedules for all possible root Vertex schedule variations 
		// e.g. 'A' vertex on processor 1 and processor 2 and so on
		// seemed redundant to me so I am only checking for Root Vertex on first processor
		
		// for(int i = 0; i < _numberOfProcessors; i++) {  
		for(int i = 0; i < 1; i++) {
			_schedules.add(schedules[i]);
			this.generateChildSchedulesForThisSchedule(schedules[i], schedules[i].getChildVertices());
		}
	}
	/**
	 * Temporary method, I am just using it to check if the schedules are all there
	 */
	public void tempPrintOutSchedules() {
		Map<Integer, Schedule> schedule = new TreeMap<Integer, Schedule>();
		Map<String, Vertex> verticesName = new TreeMap<String, Vertex>();
		for(Vertex v : _graph.getVertices()) {
			verticesName.put(v.getName(),v);
		}
		for(String vertexName : verticesName.keySet()) {
			Map<Integer, Schedule> tempScheduleForSpecifiedVertex = new TreeMap<Integer, Schedule>();
			tempScheduleForSpecifiedVertex = this.tempGetScheduleForSpecifiedVertex(verticesName.get(vertexName));
			this.tempPrintItOut(tempScheduleForSpecifiedVertex,verticesName.get(vertexName));
		}
	}
	public void tempPrintOutLastNodeScheduleInTimeOrder() {
		Map<Integer, Schedule> scheduleForLastNodeInTimeOrder = new TreeMap<Integer, Schedule>();
		Vertex lastVertex = this.tempGetLastVertex();
		if(lastVertex == null) {
			this.tempPrintOutSchedules();
			return;
		}
		scheduleForLastNodeInTimeOrder = this.tempGetScheduleForSpecifiedVertex(lastVertex);
		this.tempPrintItOut(scheduleForLastNodeInTimeOrder,lastVertex);
	}
	private Vertex tempGetLastVertex() {
		List<Vertex> allVertices = new ArrayList<Vertex>(_graph.getVertices());
		List<Edge> allEdges = new ArrayList<Edge>(_graph.getEdges());
		Vertex lastVertex = null;
		for(Vertex vertex : allVertices) {
			boolean hasChild = false;
			for(Edge edge : allEdges) {
				if(edge.getSource().equals(vertex)) {
					hasChild = true;
					lastVertex = vertex;
					break;
				}
			}
			if(!hasChild) {
				return vertex;
			}
		}
		return lastVertex;
	}
	private Map<Integer, Schedule> tempGetScheduleForSpecifiedVertex(Vertex vertex){
		Map<Integer, Schedule> scheduleForSpecifiedVertex = new TreeMap<Integer, Schedule>();
		for(Schedule schedule : _schedules) {
			if(schedule.getLastUsedVertex().getName().equals(vertex.getName())) {
				scheduleForSpecifiedVertex.put(schedule.getTimeOfSchedule(),schedule);
			}
			//if(schedule.getLastUsedVertex().getName().equals("c")) {
			//	System.out.println("Schedule c\tMax Time: " + schedule.getTimeOfSchedule() + "\n" + schedule.toString());
			//}
		}
		return scheduleForSpecifiedVertex;
	}
	private void tempPrintItOut(Map<Integer, Schedule> scheduleToPrint, Vertex vertex) {
		System.out.println("\n\nSchedules for the vertex: " + vertex.getName() + "\n");
		int count = 0;
		for(Integer scheduleTime : scheduleToPrint.keySet()) {
			System.out.println("Schedule " + scheduleToPrint.get(scheduleTime).getLastUsedVertex().getName() + count + "\tMax Time: " + scheduleTime + "\n" + scheduleToPrint.get(scheduleTime).toString());
			count++;
		}
	}
	public void tempPrintVertices() {
		System.out.println("Vertices: ");
		for(Vertex v : _graph.getVertices()) {
			System.out.print(v.getName() + " ");
		}
		System.out.println();
	}
	public void tempPrintEdges() {
		System.out.println("Edges: ");
		for(Edge e : _graph.getEdges()) {
			System.out.println(e.getSource().getName() + "->" + e.getDestination().getName() + " weight:" + e.getWeight());
		}
	}

	/**
	 * This method uses the parent schedule to make schedules for its children Vertices
	 * It then adds the child schedule to _schedules variable
	 * and also forms a relationship between its parent through _scheduleEdge 
	 * 
	 * 
	 * @param parentSchedule is the Schedule where the children schedule will base off
	 * @param childVertices is the list of children Vertices of the parentSchedule's Vertex
	 */
	public void generateChildSchedulesForThisSchedule(Schedule parentSchedule, List<Vertex> childVertices){
		List<Schedule> childSchedules = new ArrayList<Schedule>();
		for(Vertex v : childVertices) {
			Schedule s = new Schedule(parentSchedule);
			Schedule[] schedules = new Schedule[_numberOfProcessors];
			schedules = s.generateAllPossibleSchedule(v);
			for(int i = 0; i < _numberOfProcessors; i++) {
				childSchedules.add(schedules[i]);
				_schedules.add(schedules[i]);
			}
		}
		ScheduleEdge scheduleEdge = new ScheduleEdge(parentSchedule, childSchedules);
		_schedulesEdges.add(scheduleEdge);
		for(Schedule s : childSchedules) {
			if(!s.getChildVertices().isEmpty()) {
				this.generateChildSchedulesForThisSchedule(s,s.getChildVertices());
			}
		}
	}
	
}
