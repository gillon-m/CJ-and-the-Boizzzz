package scheduler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import graph.Graph;
import graph.Vertex;
import gui.ScheduleListener;
import gui.Visualiser;
import gui.VisualiserController;
import heuristics.CostFunctionCalculator;
import pruning.ListScheduling;
import pruning.Pruning;
import components.ScheduleComparator;
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
	private List<Schedule> _closedSchedules;
	private List<Schedule> _partialExpanded;
	private List<ScheduleListener> _listeners;
	private boolean _visualisation;
	private int _upperBoundCost;
	private Timestamp _timestampper;
	final static AtomicLong seq = new AtomicLong();

	public Scheduler(int numberOfProcessors, boolean visualisation) {
		_openSchedules = new PriorityBlockingQueue<Schedule>(Graph.getInstance().getVertices().size(), new ScheduleComparator());
		_closedSchedules = new ArrayList<Schedule>();
		_partialExpanded = new ArrayList<Schedule>();
		_numberOfProcessors = numberOfProcessors;
		ListScheduling ls = new ListScheduling(_numberOfProcessors);
		_upperBoundCost = ls.getUpperBoundCostFunction();
		_timestampper = new Timestamp(System.currentTimeMillis());
		_visualisation = visualisation;
		if (_visualisation) {
			Visualiser visualiser = new Visualiser();
			VisualiserController visualiserController = new VisualiserController(visualiser);
			_listeners = new ArrayList<ScheduleListener>();
			_listeners.add(visualiserController);
		}
	}

	/**
	 * This method returns the optimal schedule
	 * @return void
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
			if (_visualisation) {
				fireScheduleChangeEvent(currentSchedule);
			}
			System.out.println("Vertex = " + currentSchedule.getLastUsedVertex().getName() +"\t|Time Taken = "+currentSchedule.getTimeOfSchedule()
					+"\n"+ currentSchedule.toString());

			_openSchedules.remove(currentSchedule);
			_closedSchedules.add(currentSchedule);
//			System.out.println("Size: "+_openSchedules.size());

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
	 * passes the conditions required 
	 *
	 * @param currentSchedule
	 */
	private void addCurrentSchedulePossibleSuccessorsToOpenSchedule(Schedule currentSchedule) {
		List<Vertex> currentVertexSuccessors = currentSchedule.getChildVertices();
		List<Integer> intList = new ArrayList<Integer>();
		for(Vertex childVertex : currentVertexSuccessors) {
			// Preparation to make schedules for child vertex
			Schedule currentScheduleCopy = new Schedule(currentSchedule);
			Schedule[] currentChildVertexSchedules = new Schedule[_numberOfProcessors];
			// all possible schedules of child vertex on the current schedule
			currentChildVertexSchedules = currentScheduleCopy.generateAllPossibleScheduleForSpecifiedVertex(childVertex);
			int parentScheduleCost;
			CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();

			if (currentSchedule.hasSetCost()) {
				parentScheduleCost = currentSchedule.getCost();
			} else {
				parentScheduleCost = costFunctionCalculator.getTotalCostFunction(currentSchedule);
			}
			//
//			for (int i = 0; i < _numberOfProcessors;i++) {
//				int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);
//				if (_upperBoundCost > childScheduleCost) {
//					if(this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) {
//						if (parentScheduleCost == childScheduleCost) {
//							System.out.println(childScheduleCost + " " + parentScheduleCost);
//							System.out.println("ADDED" +"\t|Time Taken = "+currentChildVertexSchedules[i].getTimeOfSchedule()
//							+"\n"+ currentChildVertexSchedules[i].toString());
////							currentChildVertexSchedules[i].setTimeStamp(_timestampper.getTime());
//							currentChildVertexSchedules[i].setTimeStamp(seq.getAndIncrement());
//							_openSchedules.add(currentChildVertexSchedules[i]);
//						} else {
//							System.out.println(childScheduleCost + " " + parentScheduleCost);
//							System.out.println("NOT ADDED" +"\t|Time Taken = "+currentChildVertexSchedules[i].getTimeOfSchedule()
//							+"\n"+ currentChildVertexSchedules[i].toString());
//							intList.add(childScheduleCost);
//						}
//						
////						System.out.println(childScheduleCost + " " + parentScheduleCost);
//					}
//				}
//
//			}
			//
			if(_partialExpanded.contains(currentSchedule)) {
				for(int i = 0; i < _numberOfProcessors; i++ ) {
					int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);
//					System.out.println(childScheduleCost + " " + parentScheduleCost);
					// Check Upper Bound
					if(childScheduleCost == _upperBoundCost) {
						if(parentScheduleCost > costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i])) {
							if(this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) {
								currentChildVertexSchedules[i].setTimeStamp(_timestampper.getTime());
								_openSchedules.add(currentChildVertexSchedules[i]);
							} 
						}
					} else {
//						System.out.println("DOHERE");
//						intList.add(childScheduleCost);
					}
				}
			} else {
				boolean partialExpanded = false;
				for(int i = 0; i < _numberOfProcessors; i++ ) {
					int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);
//					System.out.println(childScheduleCost + " " + parentScheduleCost);
					if(parentScheduleCost >= childScheduleCost) {
						if(this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) {
							currentChildVertexSchedules[i].setTimeStamp(_timestampper.getTime());
							_openSchedules.add(currentChildVertexSchedules[i]);
							partialExpanded = true;
						}
					} else {
						intList.add(childScheduleCost);
					}
				}
				if(!partialExpanded) {
					for(int i = 0; i < _numberOfProcessors; i++ ) {
						int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);
						// Check Upper Bound
						if(childScheduleCost <= _upperBoundCost) {
							if(this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) {
								currentChildVertexSchedules[i].setTimeStamp(_timestampper.getTime());
								_openSchedules.add(currentChildVertexSchedules[i]);
							} else {
								intList.add(childScheduleCost);
							}
						}
					}
				} else {
					
					_closedSchedules.remove(currentSchedule);
					if (intList.size() != 0) {
							currentSchedule.setCost(Collections.min(intList));
							currentSchedule.setTimeStamp(_timestampper.getTime());
							_openSchedules.add(currentSchedule);
					}

					_partialExpanded.add(currentSchedule);
				}
			}

		}

//		System.out.println(intList);
//		if (intList.size() != 0) {
//			currentSchedule.setCost(Collections.min(intList));
//			System.out.println("New minimum is: " + Collections.min(intList));
////			currentSchedule.setTimeStamp(_timestampper.getTime());
//			currentSchedule.setTimeStamp(seq.getAndIncrement());
//			_openSchedules.add(currentSchedule);
//		
//		}
//		System.out.println("================================");
	}
	/**
	 * This method checks if the successor schedules have the conditions required to
	 * get added into the openschedule.
	 * It also checks inside the openschedule and closedschedule if there are any schedules that can be taken out
	 * that was made redundant by predecessor schedules
	 *
	 * returns true if it passes
	 * otherwise returns false
	 *
	 * @param childSchedule
	 * @return
	 */
	private boolean checkScheduleThroughPruning(Schedule childSchedule) {
		Pruning pruning = new Pruning();
		if(pruning.isCurrentScheduleNeeded(_openSchedules, _closedSchedules, childSchedule)){
			return true;
		}
		return false;

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
			//
			rootSchedules[0].setTimeStamp(_timestampper.getTime());
			
			_openSchedules.add(rootSchedules[0]);
		}
	}
	private void fireScheduleChangeEvent(Schedule currentSchedule) {
		for (ScheduleListener listener : _listeners) {
			listener.update(currentSchedule);
		}
	}
}