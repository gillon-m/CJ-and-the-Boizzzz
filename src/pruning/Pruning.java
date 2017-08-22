package pruning;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import scheduler.Processor;
import scheduler.Schedule;
/**
 * Pruning Techniques to optimize new schedules being put into the openschedule
 * 
 * Goes through both open and closed schedules to see if it can be optimized
 * 
 * @author SuhoN
 *
 */
public class Pruning {
	
	public boolean isCurrentScheduleNeeded(PriorityBlockingQueue<Schedule> openSchedules, List<Schedule> closedSchedules, Schedule currentSchedule){
		List<IPruning> p = new ArrayList<IPruning>();
		IPruning p1 = new CheckDuplicates();
		IPruning p2 = new CheckNormalisation();
		p.add(p1);
		//p.add(p2);
		
		for(Schedule schedule : closedSchedules){
			for(IPruning pruning : p) {
				if(!pruning.isScheduleNeeded(currentSchedule, schedule)) {
					return false;
				}
			}
		}
		for(Schedule schedule : openSchedules){
			for(IPruning pruning : p) {
				if(!pruning.isScheduleNeeded(currentSchedule, schedule)) {
					return false;
				}
			}
		}
		return true;
	}
}
