package components;

import java.util.Comparator;

import scheduler.Schedule;

public class ScheduleComparator implements Comparator<Schedule> {

	@Override
	public int compare(Schedule o1, Schedule o2) {
		/*Returns schedules based on h(n) value only. 
		 *Does not return optimal schedule,
		but returns an arbitrary valid schedule.
		Used for MileStone 1 Demo purposes only.*/
		
		return (int) (o1.getAllUsedVertices().size()*0.1-o2.getAllUsedVertices().size()*0.1);
		
		
		/*Return based on g(n) value only.
		 *Returns an optimal schedule,
		 *however does not work for Nodes_11_OutTree due to memory error. 
		 *Heuristic function will be added to this return statement (todo).
		*/
		
//		return o1.getTimeOfSchedule()-o2.getTimeOfSchedule();
	}

}
