package Scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.PriorityBlockingQueue;

import App.Edge;
import App.Graph;
import App.Vertex;
import Components.ScheduleComparator;
/**
 * This Class uses the Schedule Class and Processor Class 
 * to make schedules using the information from the Graph Variable which 
 * contains all the list of vertex and edges. 
 * 
 * The schedules are created using A* algorithm.
 * 
 * This Class manages the created schedules and returns the optimal schedule
 * 
 * @author Alex Yoo
 *
 */
public class Scheduler {
	private int _numberOfProcessors;					
	private PriorityBlockingQueue<Schedule> _openSchedules;
	//private List<Schedule> _closedSchedules;
	
	public Scheduler(int numberOfProcessors) {
		_openSchedules = new PriorityBlockingQueue<Schedule>(Graph.getInstance().getVertices().size(), new ScheduleComparator());
		//_closedSchedules = new ArrayList<Schedule>();
		_numberOfProcessors = numberOfProcessors;
	}
	
	/**
	 * This method returns the optimal schedule
	 * @return optimal schedule
	 * @throws Exception
	 */
	public Schedule getOptimalSchedule() throws Exception {
		this.addRootVerticesSchedulesToOpenSchedule();
		
		Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();
		
		return optimalSchedule;
	}
	/**
	 * This method uses the A* algorithm to create schedules
	 * It only returns back once it finds an optimal schedule
	 * It throws an exception if the openschedule queue is empty because that is not suppose to happen
	 * 
	 * @return optimal schedule
	 * @throws Exception
	 */
	private Schedule makeSchedulesUsingAlgorithm() throws Exception {
		while(!_openSchedules.isEmpty()) {
			Schedule currentSchedule = _openSchedules.peek();
			
			//System.out.println("Vertex = " + currentSchedule.getLastUsedVertex().getName() +"\t|Time Taken = "+currentSchedule.getTimeOfSchedule()
			//		+"\n"+ currentSchedule.toString());
			
			_openSchedules.remove(currentSchedule);
			//_closedSchedules.add(currentSchedule);
			
			if(this.hasScheduleUsedAllPossibleVertices(currentSchedule)) {
				return currentSchedule;
			}
			
			this.addCurrentSchedulePossibleSuccessorsToOpenSchedule(currentSchedule);
			
		}
		throw new Exception("Tried to access empty openschedules :(");
	}
	
	/**
	 * For the current schedule we are processing,
	 * it tries to find successor schedules that are available
	 * Those successors schedules are then added to the open schedule if it 
	 * passes the conditions required in checkScheduleOnOpenSchedule method
	 * 
	 * @param currentSchedule
	 */
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
	}
	/**
	 * This method checks if the successor schedules have the conditions required to
	 * get added into the openschedule.
	 * It also checks inside the openschedule if there are any schedules that can be taken out
	 * that was made redundant by successor schedule
	 * 
	 * returns true if it passes
	 * otherwise returns false
	 * 
	 * @param childSchedule
	 * @return
	 */
	private boolean checkScheduleOnOpenSchedule(Schedule childSchedule) {
		boolean passesCondition = true;
		for(Schedule schedule : _openSchedules) {
			if(schedule.getLastUsedVertex().getName().equals(childSchedule.getLastUsedVertex().getName())){
				if(childSchedule.getTimeOfSchedule() <= schedule.getTimeOfSchedule()) {
					if(childSchedule.getAllUsedVertices().contains(schedule.getAllUsedVertices())) {
						_openSchedules.remove(schedule);
						passesCondition = true;
					}
				}else {
					if (schedule.getAllUsedVertices().contains(childSchedule.getAllUsedVertices())) {
						passesCondition = false;
						break;
					} 
				}
			}
		}
		return passesCondition;
	}
	/**
	 * This method adds root schedules to openschedules
	 * since at the start of schedule the first task is the same no matter which processor 
	 * it is put on so only one variation of root schedule is added.
	 *  
	 */
	private void addRootVerticesSchedulesToOpenSchedule(){
		for(Vertex rootVertex : Graph.getInstance().getRootVertices()) {
			Schedule emptySchedule = new Schedule(_numberOfProcessors);
			Schedule[] rootSchedules = new Schedule[_numberOfProcessors];
			
			rootSchedules = emptySchedule.generateAllPossibleScheduleForSpecifiedVertex(rootVertex);
			
			_openSchedules.add(rootSchedules[0]);
		}
	}
	/**
	 * This method checks if the current schedule is a finished schedule
	 * 
	 * returns true if it is
	 * otherwise returns false
	 * 
	 * @param currentSchedule
	 * @return
	 */
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
