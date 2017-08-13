package Scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import App.Edge;
import App.Graph;
import App.Vertex;
import Components.ScheduleComparator;
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
	/*
	private int _numberOfProcessors;				// Could ask how many processor the user want, but for now it is always 2 processors
	private List<Schedule> _schedules;				// _schedules variable contains all the possible schedules that are created
	private List<ScheduleEdge> _schedulesEdges;		// _schedulesEdges variable store the relationships between schedules
	
	public SearchSpace(int noOfProcessors) {
		_numberOfProcessors = noOfProcessors;
		_schedules = new ArrayList<Schedule>();
		_schedulesEdges = new ArrayList<ScheduleEdge>();
	}
	
	/**
	 * This method is called to initiate the process of creating all the possible schedules
	 * Implementation may change depending on algorithm used.
	 * 
	 * Caution: This method currently assumes the first Vertex in the list is the root Vertex 
	 * 
	 *//*
	public void makeSearchSpace() {
		List<Vertex> allVertices = new ArrayList<Vertex>(Graph.getInstance().getVertices());
		Schedule s = new Schedule(_numberOfProcessors);
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
	public String outputToPrint() {
		Map<Integer, Schedule> scheduleForLastNodeInTimeOrder = new TreeMap<Integer, Schedule>();
		Vertex lastVertex = this.tempGetLastVertex();
		if(lastVertex == null) {
			String output = "";
			Map<Integer, Schedule> schedule = new TreeMap<Integer, Schedule>();
			Map<String, Vertex> verticesName = new TreeMap<String, Vertex>();
			for(Vertex v : Graph.getInstance().getVertices()) {
				verticesName.put(v.getName(),v);
			}
			for(String vertexName : verticesName.keySet()) {
				Map<Integer, Schedule> tempScheduleForSpecifiedVertex = new TreeMap<Integer, Schedule>();
				tempScheduleForSpecifiedVertex = this.tempGetScheduleForSpecifiedVertex(verticesName.get(vertexName));
				output += firstOutput(tempScheduleForSpecifiedVertex,verticesName.get(vertexName));
			}
			return output;
		}
		scheduleForLastNodeInTimeOrder = this.tempGetScheduleForSpecifiedVertex(lastVertex);
		return firstOutput(scheduleForLastNodeInTimeOrder,lastVertex);
	}
	private String firstOutput(Map<Integer, Schedule> scheduleToPrint, Vertex vertex) {
		int shortestTime = ((TreeMap<Integer, Schedule>) scheduleToPrint).firstKey();
		return scheduleToPrint.get(shortestTime).toString();
	}
	
	/**
	 * Temporary method, I am just using it to check if the schedules are all there
	 *//*
	private void tempPrintItOut(Map<Integer, Schedule> scheduleToPrint, Vertex vertex) {
		System.out.println("\n\nSchedules for the vertex: " + vertex.getName() + "\n");
		int count = 0;
		for(Integer scheduleTime : scheduleToPrint.keySet()) {
			System.out.println("Schedule " + scheduleToPrint.get(scheduleTime).getLastUsedVertex().getName() + count + "\tMax Time: " + scheduleTime + "\n" + scheduleToPrint.get(scheduleTime).toString());
			count++;
		}
	}
	public void tempPrintOutSchedules() {
		Map<Integer, Schedule> schedule = new TreeMap<Integer, Schedule>();
		Map<String, Vertex> verticesName = new TreeMap<String, Vertex>();
		for(Vertex v : Graph.getInstance().getVertices()) {
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
		List<Vertex> allVertices = new ArrayList<Vertex>(Graph.getInstance().getVertices());
		List<Edge> allEdges = new ArrayList<Edge>(Graph.getInstance().getEdges());
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
	public void tempPrintVertices() {
		System.out.println("Vertices: ");
		for(Vertex v : Graph.getInstance().getVertices()) {
			System.out.print(v.getName() + " ");
		}
		System.out.println();
	}
	public void tempPrintEdges() {
		System.out.println("Edges: ");
		for(Edge e : Graph.getInstance().getEdges()) {
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
	 *//*
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
	}*/
	/**
	 * 
	 * Get root nodes schedules
	 * add to _openSchedules
	 * 
	 * while 
	 * 
	 * 		find Current with lowest cost in _openSchedules
	 * 		remove from _openSchedules and add to _closedSchedules
	 * 
	 * 		if Current has all used vertices
	 * 			return Current 
	 * 
	 * 		Get all child vertices for Current that is available
	 * 			if no child vertices do this from start of while with next queue
	 * 		for each child vertex create all possible schedules
	 * 			for each schedule, check if used vertex is in openschedules
	 * 				if exist, then check if old schedule usedvertices are contained in new vertex usedVertices
	 * 				if true = remove oldvertices and add used vertices
	 * 				if else old vertices has lower cost and contains all new schedule usedvertices
	 * 					then don't add new schedule
	 * 			
	 */
	private int _numberOfProcessors;				
	private PriorityQueue<Schedule> _openSchedules;
	private List<Schedule> _closedSchedules;
	
	public SearchSpace(int numberOfProcessors) {
		_openSchedules = new PriorityQueue<Schedule>(Graph.getInstance().getVertices().size(), new ScheduleComparator());
		_closedSchedules = new ArrayList<Schedule>();
		_numberOfProcessors = numberOfProcessors;
	}
	
	public Schedule getOptimalSchedule() throws Exception {
		this.addRootVerticesSchedulesToOpenSchedule();
		
		Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();
		
		return optimalSchedule;
	}
	
	private Schedule makeSchedulesUsingAlgorithm() throws Exception {
		while(true) {
			Schedule currentSchedule = _openSchedules.peek();
			if (currentSchedule == null) {
				throw new Exception("Tried to access empty openschedules :(");
			}
			_openSchedules.remove(currentSchedule);
			_closedSchedules.add(currentSchedule);
			
			if(this.hasScheduleUsedAllPossibleVertices(currentSchedule)) {
				return currentSchedule;
			}
			
			this.addCurrentSchedulePossibleSuccessorsToOpenSchedule(currentSchedule);
			
		}
		
		// Get child schedules for current that is available 
		// 		if no child schedules pick the next one in priority queue
		// for all child schedules
		// check if child vertex is in openschedules
		// if yes and child schedule has lower cost than old schedule
		// check if new schedule contains all used vertices of old schedule
		// if true, get rid of old schedule
		
		// add child schdules to open schdule
		// repeat until if condition is good!
	}
	
	private void addCurrentSchedulePossibleSuccessorsToOpenSchedule(Schedule currentSchedule) {
		List<Vertex> currentVertexSuccessors = currentSchedule.getChildVertices();
		for(Vertex childVertex : currentVertexSuccessors) {
			// Preparation to make schedules for child vertex
			Schedule currentScheduleCopy = new Schedule(currentSchedule);
			Schedule[] currentChildVertexSchedules = new Schedule[_numberOfProcessors];
			// all possible schedules of child vertex on the current schedule
			currentChildVertexSchedules = currentScheduleCopy.generateAllPossibleScheduleForSpecifiedVertex(childVertex);
			
			for(int i = 0; i < _numberOfProcessors; i++ ) {
				if(this.checkScheduleOnOpenSchedule(currentChildVertexSchedules[i])) {
					_openSchedules.add(currentChildVertexSchedules[i]);
				}
			}
		}

		// get child schedules of current
		// or get schedules of nodes if new
		// put them into openschedules (auto sorted by f _cost)
		// if parentschedule existed, remove from openschedules
		// and add to closedschedules
		
		// New schedule vertex, already in closed vertex
		// if new have lower cost than old
		// then check if used vertices in old schedule is contained in new schedule
		// if yes then remove old schedule in closed and add in new
		
	}
	private boolean checkScheduleOnOpenSchedule(Schedule childSchedule) {
		boolean temp = true;
		/*
		for(Schedule schedule : _openSchedules) {
			if(schedule.getLastUsedVertex().getName().equals(childSchedule.getLastUsedVertex().getName())){
				if(childSchedule.getTimeOfSchedule() <= schedule.getTimeOfSchedule()) {
					if(childSchedule.getAllUsedVertices().contains(schedule.getAllUsedVertices())) {
						_openSchedules.remove(schedule);
						// add?
						temp = true;
					}
				}else {
					if (schedule.getAllUsedVertices().contains(childSchedule.getAllUsedVertices())) {
						
					} 
				}
			}
		}*/
		return temp;
	}
	private void addRootVerticesSchedulesToOpenSchedule(){
		for(Vertex rootVertex : Graph.getInstance().getRootVertices()) {
			Schedule emptySchedule = new Schedule(_numberOfProcessors);
			Schedule[] rootSchedules = new Schedule[_numberOfProcessors];
			
			rootSchedules = emptySchedule.generateAllPossibleScheduleForSpecifiedVertex(rootVertex);
			
			_openSchedules.add(rootSchedules[0]);
		}
	}
	private boolean hasScheduleUsedAllPossibleVertices(Schedule currentSchedule) {
		List<Vertex> currentScheduleUsedVertices = currentSchedule.getAllUsedVertices();
		for(Vertex vertex : Graph.getInstance().getVertices()) {
			if(!currentScheduleUsedVertices.contains(vertex)) {
				return false;
			}
		}
		return true;
	}
}
