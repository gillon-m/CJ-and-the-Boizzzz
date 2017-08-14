package components;

import java.util.Comparator;

import scheduler.Schedule;

public class ScheduleComparator implements Comparator<Schedule> {

	@Override
	public int compare(Schedule o1, Schedule o2) {
		CostFunctionCalculator c1 = new CostFunctionCalculator(o1);
		CostFunctionCalculator c2 = new CostFunctionCalculator(o2);
		return c1.getTotalCostFunction() - c2.getTotalCostFunction();
	}
	
	

}
