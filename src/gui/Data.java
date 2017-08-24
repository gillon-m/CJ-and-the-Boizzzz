package gui;

import graph.Graph;
import scheduler.Schedule;

/**
 * Data objects are used to store information regarding the current state of the scheduler. 
 * @author Gillon Manalastas
 *
 */
public class Data {
	private Schedule _currentSchedule=null;
	private boolean _isFinished=false;	
	private int _totalNumberOfCreatedSchedules=0;
	private long _startTime;
	private long _endTime;
	private long _elapsed;
	private Graph _graph;
	
	/**
	 * Updates which schedule is currently being check at the head
	 * @param currentSchedule
	 */
	public void updateCurrentSchedule(Schedule currentSchedule) {
		_currentSchedule=currentSchedule;
	}
	
	/**
	 * Set whether of not the scheduler has finished
	 * @param b
	 */
	public void isFinished(boolean b) {
		_isFinished=b;
	}
	
	/**
	 * Return whether or not the scheduler has finished
	 * @return
	 */
	public boolean isFinished(){
		return _isFinished;
	}
	
	/**
	 * Returns the current schedule
	 * @return current schedule
	 */
	public Schedule getCurrentSchedule() {
		return _currentSchedule;
	}
	
	/**
	 * Updates the total number of schedules created
	 * @param i
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
	 * @param Graph
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
	
}
