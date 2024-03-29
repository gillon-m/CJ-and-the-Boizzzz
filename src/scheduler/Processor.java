package scheduler;

import java.util.ArrayList;

import graph.Vertex;

/**
 * Processor class represents the individual processor in a schedule
 * 
 * @author Alex Yoo
 *
 */

public class Processor {
	private int _latestTime;							// Is the time this processor is currently up to
	private ArrayList<Vertex> _scheduleOfProcessor;		// Contains the list of tasks (includes empty tasks which fills the gaps)
			
	
	public Processor() {
		_latestTime = 0;
		_scheduleOfProcessor = new ArrayList<Vertex>();
	}
	public Processor(Processor p) {
		_latestTime = p.getLatestTime();
		_scheduleOfProcessor = new ArrayList<Vertex>(p.getScheduleOfProcessor());
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
	 * Adds an empty time slot into the processor's schedule
	 * 
	 * Empty name is '-'
	 * 
	 * @param emptyTimeLength is the time weight of the empty time slot
	 */
	public void addEmptyTimeSlots(int emptyTimeLength) {
		Vertex empty = new Vertex("-", emptyTimeLength);
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
	public ArrayList<Vertex> getScheduleOfProcessor(){
		return _scheduleOfProcessor;
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
	
	
	public boolean equalCheck(Object obj){
		if (obj == null || !(obj.getClass() == this.getClass())) {
	        return false;
	    }
		
		if(this._scheduleOfProcessor.size() != ((Processor)obj)._scheduleOfProcessor.size()) {
			return false;
		}
		
		for(int i = 0; i < _scheduleOfProcessor.size(); i++){
			if(!_scheduleOfProcessor.get(i).getName().equals(((Processor) obj)._scheduleOfProcessor.get(i).getName())){
				return false;
			}	
		}
		
		return true;
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
			process += "\t" + v.getName() + ":" + timeCount;
		}
	    return process;
	} 
}
