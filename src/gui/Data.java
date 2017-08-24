package gui;

import java.util.List;

import graph.Graph;
import graph.Vertex;
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
	private Vertex _lastUsedVertex;
	private List<Vertex> _lastUsedVertexChildren;
	private List<Vertex> _allUsedVertices;
	
	/**
	 * Updates which schedule is currently being check at the head
	 * @param the current schedule
	 */
	public void updateCurrentSchedule(Schedule currentSchedule) {
		_currentSchedule=currentSchedule;
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
		return _currentSchedule;
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
	 * Sets the last used vertex used to create a schedule
	 * @param last used vertex
	 */
	public void setLastUsedVertex(Vertex v){
		_lastUsedVertex=v;
	}
	
	/**
	 * Gets the last used vertex used to create a schedule
	 * @return last used vertex
	 */
	public Vertex getLastUsedVertex(){
		return _lastUsedVertex;
	}
	
	/**
	 * Sets the children of the last use vertex used to create a schedule
	 * @param children of last used vertex
	 */
	public void setLastUsedVertexChildren(List<Vertex> v){
		_lastUsedVertexChildren=v;
	}
	
	/**
	 * Sets the children of the last use vertex used to create a schedule
	 * @param children of last used objects
	 */
	public List<Vertex> getLastUsedVertexChildren(){
		return _lastUsedVertexChildren;
	}

	/**
	 * Sets the list of used vertices in a schedule
	 * @param All the used vertices in a schedule
	 */
	public void setAllUsedVertices(List<Vertex> v) {
		_allUsedVertices = v;
	}
	/**
	 * Gets the list of used vertices in a schedule
	 * @param All the used vertices in a schedule
	 */
	public List<Vertex> getAllUsedVertices() {
		return _allUsedVertices;
	}
	
}
