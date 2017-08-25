package scheduler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

import javax.swing.Timer;

import graph.Graph;
import graph.Vertex;
import gui.ScheduleListener;
import gui.VisualiserController;
import heuristics.CostFunctionCalculator;
import pruning.ListScheduling;
import pruning.Pruning;
import components.ScheduleComparator;
import data.Data;
import data.StopWatch;

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
	private ConcurrentLinkedQueue<Schedule> _closedSchedules;
	private List<Schedule> _partialExpanded;
	private List<Schedule> _finalSchedule;
	private int _upperBoundCost;
	private int _numberOfCores;
	private boolean _visualisation;
	private Timer _timer;
	private ActionListener action;
	private int timerCount;
	private VisualiserController _visualiserController;
	private StopWatch _stopWatch;
	private Schedule _bestSchedule;
	private Data _data;
	
	public Scheduler(int numberOfProcessors, int numberOfCores, boolean visualisation) {
		_openSchedules = new PriorityBlockingQueue<Schedule>(Graph.getInstance().getVertices().size(), new ScheduleComparator());
		_closedSchedules = new ConcurrentLinkedQueue<Schedule>();
		_partialExpanded = new ArrayList<Schedule>();
		_finalSchedule = new ArrayList<Schedule>();
		_numberOfProcessors = numberOfProcessors;
		_numberOfCores = numberOfCores;
		ListScheduling ls = new ListScheduling(_numberOfProcessors);
		_upperBoundCost = ls.getUpperBoundCostFunction();
		_visualisation = visualisation;
		_stopWatch=StopWatch.getInstance();
		_data=Data.getInstance();
		if (_visualisation) {
			_visualiserController = new VisualiserController();
			setUpTimer();
		}
	}
	private void setUpTimer() {
		action = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (timerCount == 0) {
					Schedule schedule = _openSchedules.peek();
					_data.setCurrentSchedule(schedule);
					_data.updateTotalNumberOfCreatedSchedules(_openSchedules.size()+_closedSchedules.size());
					_visualiserController.update(false);
				} else {
					timerCount--;
				}
			}

			
		};
		_timer = new Timer(1, action);
		_timer.setInitialDelay(0);
		_timer.start();
	}
	
	/**
	 * This method returns the optimal schedule
	 * @return void
	 * @throws Exception
	 */
	public Schedule getOptimalSchedule() {
		this.addRootVerticesSchedulesToOpenSchedule();
		_stopWatch.start();
		Schedule optimalSchedule = this.makeSchedulesUsingAlgorithm();
		if (_visualisation) {
			_timer.stop();
			_data.setCurrentSchedule(optimalSchedule);
			_data.updateTotalNumberOfCreatedSchedules(_openSchedules.size()+_closedSchedules.size());
			_visualiserController.update(true);
		}
		_stopWatch.stop();
		return optimalSchedule;			
	}

	/**
	 * This method uses the A* algorithm to create schedules 
	 * It only returns back once it finds an optimal schedule
	 * It throws an exception if the openschedule queue is empty because that is not suppose to happen
	 * 
	 * @return optimal schedule
	 */
	private Schedule makeSchedulesUsingAlgorithm() {
		while (_finalSchedule.isEmpty()) {
			searchAndExpand();
		}
		return _finalSchedule.get(0);			
	}
	private void searchAndExpand() {
		Schedule currentSchedule = _openSchedules.poll();
		_closedSchedules.add(currentSchedule);
		if (this.hasScheduleUsedAllPossibleVertices(currentSchedule)) {
			_finalSchedule.add(currentSchedule);
		}
		this.addCurrentSchedulePossibleSuccessorsToOpenSchedule(currentSchedule);		
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
		for(Vertex childVertex : currentVertexSuccessors) {
			// Preparation to make schedules for child vertex
			Schedule currentScheduleCopy = new Schedule(currentSchedule);
			Schedule[] currentChildVertexSchedules = new Schedule[_numberOfProcessors];
			// all possible schedules of child vertex on the current schedule
			currentChildVertexSchedules = currentScheduleCopy.generateAllPossibleScheduleForSpecifiedVertex(childVertex);
			
			CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();
			int parentScheduleCost = costFunctionCalculator.getTotalCostFunction(currentSchedule);
			
			if(_partialExpanded.contains(currentSchedule)) {
				for(int i = 0; i < _numberOfProcessors; i++ ) {
					int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);
					// Check Upper Bound
					if(childScheduleCost <= _upperBoundCost) {
						if(parentScheduleCost > costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i])) {
							if(this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) {
								_openSchedules.add(currentChildVertexSchedules[i]);
							}
						}
					}
				}
			} else {
				boolean partialExpanded = false;
				for(int i = 0; i < _numberOfProcessors; i++ ) {
					int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);
					if(parentScheduleCost >= childScheduleCost) {
						if(this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) {
							_openSchedules.add(currentChildVertexSchedules[i]);
							partialExpanded = true;
						}
					}
				}
				if(!partialExpanded) {
					for(int i = 0; i < _numberOfProcessors; i++ ) {
						int childScheduleCost = costFunctionCalculator.getTotalCostFunction(currentChildVertexSchedules[i]);
						// Check Upper Bound
						if(childScheduleCost <= _upperBoundCost) {
							if(this.checkScheduleThroughPruning(currentChildVertexSchedules[i])) {
								_openSchedules.add(currentChildVertexSchedules[i]);
							}
						}
					}
				} else {
					_openSchedules.add(currentSchedule);
					_closedSchedules.remove(currentSchedule);
					_partialExpanded.add(currentSchedule);
				}
			}
		}
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
	 * since at the start of schedule the first _timerTask is the same no matter which processor
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
}