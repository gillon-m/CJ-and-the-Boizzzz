package Scheduler;

import java.util.List;

/**
 * ScheduleEdge Class represents the relationship between Schedules in 
 * the form of parent and list of children (or source and list of destinations)
 * 
 * @author Alex Yoo
 *
 */
public class ScheduleEdge {
	private Schedule _source;
	private List<Schedule> _destination;
	
	public ScheduleEdge(Schedule source, List<Schedule> destination) {
		_source = source;
		_destination = destination;
	}
	
	public Schedule getSources() {
		return _source;
	}
	
	public List<Schedule> getDestinations() {
		return _destination;
	}
	public void setDestinations(List<Schedule> destination) {
		_destination = destination;
	}
}
