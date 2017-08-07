package SearchSpace;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import App.Edge;
import App.Graph;
import App.Vertex;

/**
 * Processor class represents the individual processor in a schedule
 * 
 * @author Alex Yoo
 *
 */

public class Processor {
	private int _latestTime;							// Is the time this processor has currently used up to
	private ArrayList<Vertex> _scheduleOfProcessor;		// Contains the list of tasks (includes empty tasks which fills the gaps)
	private int _countEmptySlots;					
	
	public Processor(Graph g) {
		_latestTime = 0;
		_scheduleOfProcessor = new ArrayList<Vertex>();
		_countEmptySlots = 0;
	}
	public Processor(Processor p) {
		_latestTime = p.getLatestTime();
		_scheduleOfProcessor = new ArrayList<Vertex>(p.getScheduleOfProcessor());
		_countEmptySlots = p.getEmptySlots();
	}
	
	/**
	 * Add normal Vertex to schedule
	 * 
	 * @param v
	 */
	public void addTimeSlotIntoProcessor(Vertex v) {
		this.addTimeSlots(v);
	}
	
	
	/**
	 * Adds an EMPTY time slot into the processor's schedule
	 * 
	 * @param emptyTimeLength is the time weight of the empty time slot
	 */
	public void addEmptyTimeSlots(int emptyTimeLength) {
		Vertex empty = new Vertex("EMPTY", emptyTimeLength);
		_countEmptySlots++;
		this.addTimeSlots(empty);
	}

	/**
	 * Adds the Vertex into the processors's schedule
	 * 
	 * @param v is the Vertex being requested into the processor's schedule 
	 */
	private void addTimeSlots(Vertex v) {
		_scheduleOfProcessor.add(v);
		this.addTime(v.getWeight());
	}
	/**
	 * Updates how far the processor's time schedule is up to
	 * @param t is the time that needs to bee added
	 */
	private void addTime(int t) {
		_latestTime+=t;
	}

	/**
	 * 	get methods
	 */
	public int getLatestTime() {
		return _latestTime;
	}
	private ArrayList<Vertex> getScheduleOfProcessor(){
		return _scheduleOfProcessor;
	}
	private int getEmptySlots() {
		return _countEmptySlots;
	}
	/**
	 * gets back the time including the specified vertex's time is on 
	 * in this processor
	 * 
	 * @param v
	 * @return
	 */
	public int getTime(Vertex v) {
		int time = 0;
		int index = _scheduleOfProcessor.indexOf(v);
		for(int i = 0; i < index+1; i++) {
			Vertex vertex = _scheduleOfProcessor.get(i);
			time  += vertex.getWeight();
		}
		return time;
	}
	
	
	/**
	 * To adjust the format and the data that should be returned
	 */
	@Override
	public String toString() { 
		String process = "";
		int timeCount = 0;
		for(Vertex v : _scheduleOfProcessor) {
			timeCount  += v.getWeight();
			process += " " + v.getName() + ":" + timeCount + " ";
		}
		process+="\nTotal time: " + this.getLatestTime();
	    return process;
	} 
}
