package data;

import java.util.concurrent.ConcurrentLinkedQueue;

import graph.Graph;
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
	private ConcurrentLinkedQueue<Schedule> _concurrentSchedules = new ConcurrentLinkedQueue<Schedule>();
	
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
		return _concurrentSchedules.peek();
	}
	
	public void clearSchedules(){
		_concurrentSchedules.clear();
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
	 * Adds a schedule to the list of concurectSchedules
	 * @param The currentSchedule
	 */
	public void addSchedules(Schedule s){
		_concurrentSchedules.add(s);
	}
}
