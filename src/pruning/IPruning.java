package pruning;

import scheduler.Schedule;

/**
 * Interface for pruning methods
 * Must return if the schedule is needed or not needed
 * 
 * @author Alex Yoo
 *
 */
public interface IPruning {
	/**
	 * True means it is needed
	 * False means it is not needed
	 * 
	 * @return
	 */
	boolean isScheduleNeeded(Schedule currentSchedule, Schedule existingSchedule);
}
