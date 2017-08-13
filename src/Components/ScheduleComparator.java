package Components;

import java.util.Comparator;

import Scheduler.Schedule;

public class ScheduleComparator implements Comparator<Schedule> {

	@Override
	public int compare(Schedule o1, Schedule o2) {
		return o1.getTimeOfSchedule()-o2.getTimeOfSchedule();
	}

}
