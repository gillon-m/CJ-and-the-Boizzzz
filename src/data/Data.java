package data;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import graph.Graph;
import graph.Vertex;
import scheduler.Schedule;

/**
 * Data objects are used to store information regarding the current state of the scheduler. 
 * Data is a singleton so used getInstance() method to get a Data object
 * @author Gillon Manalastas
 *
 */
public class Data {
	private boolean _isFinished=false;	
	private int _totalNumberOfCreatedSchedules=0;
	private long _startTime;
	private long _endTime;
	private Graph _graph;
	private static Data _data = new Data();
	private ConcurrentLinkedQueue<Schedule> _allSchedules = new ConcurrentLinkedQueue<Schedule>();
	private Schedule _bestSchedule;
	
	private Data(){
	}

	/**
	 * Returns the singleton Data object
	 * @return Data object
	 */
	public static Data getInstance(){
		return _data;
	}
	
	/**
	 * Set whether of not the scheduler has finished
	 * @param true if finished, false if not
	 */
	public void isFinished(boolean b) {
		_isFinished=b;
	}
	
	/**
	 * Return whether or not the scheduler has finished
	 * @return true if scheduler has finished, false if not
	 */
	public boolean isFinished(){
		return _isFinished;
	}
	
	/**
	 * Returns the current schedule
	 * @return current schedule
	 */
	public Schedule getCurrentSchedule() {
		return _allSchedules.peek();
	}
	
	/**
	 * Clears the schedule in the schedule list
	 */
	public void clearSchedules(){
		_allSchedules.clear();
	}
	
	/**
	 * Updates the total number of schedules created
	 * @param total number of schedules created 
	 */
	public void updateTotalNumberOfCreatedSchedules(int i) {
		_totalNumberOfCreatedSchedules=i;
	}
	
	/**
	 * Returns the total number of schedules created
	 * @return total number of schedules
	 */
	public int getTotalNumberOfCreatedSchedules(){
		return _totalNumberOfCreatedSchedules;
	}
	
	/**
	 * set the start time of the scheduler
	 */
	public void setStartTime() {
		_startTime = System.currentTimeMillis();
	}
	
	/**
	 * set the current time
	 */
	public void setCurrentTime() {
		_endTime = System.currentTimeMillis();
	}
	
	/**
	 * Calculate the time elapsed since the start time in milliseconds
	 * @return elapsed time
	 */
	public long getElapsedTime(){
		long elapsedTime=_endTime-_startTime;
		return elapsedTime;
	}
	
	/**
	 * Sets the graph representation of the task graph
	 * @param graph 
	 */
	public void setGraph(Graph g){
		_graph = g;
	}
	
	/**
	 * Gets the graph representation of the task graph
	 * @return Graph
	 */
	public Graph getGraph(){
		return _graph;
	}
	
	/**
	 * Adds the current schedule to the list of concurectSchedules. Determines which schedule is the best
	 * @param The currentSchedule
	 */
	public void addSchedules(Schedule currentSchedule){
		_allSchedules.add(currentSchedule);
		if(_bestSchedule==null){
			_bestSchedule=currentSchedule;
		}
		else{
			List<Vertex> currentScheduleVertices = currentSchedule.getAllUsedVerticesWithoutEmpty();
			List<Vertex> bestScheduleVertices = _bestSchedule.getAllUsedVerticesWithoutEmpty();
			int currentScheduleTime = currentSchedule.getTimeOfSchedule();
			int bestScheduleTime=_bestSchedule.getTimeOfSchedule();
			if(currentScheduleVertices.size()>bestScheduleVertices.size()){
				_bestSchedule=currentSchedule;
			}
			else if(currentScheduleVertices.size()==bestScheduleVertices.size()){
				if(currentSchedule.getTimeOfSchedule()<_bestSchedule.getTimeOfSchedule()){
					_bestSchedule=currentSchedule;
				}
			}
		}
	}

	/**
	 * Returns the most completed schedule with the fastest time
	 * @return Schedule with the most completed nodes and fastest time
	 */
	public Schedule getBestSchedule() {
		return _bestSchedule;
	}
}
