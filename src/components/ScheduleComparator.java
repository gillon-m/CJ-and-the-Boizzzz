package components;

import java.util.Comparator;

import heuristics.CostFunctionCalculator;
import scheduler.Schedule;

/**
 * Class that compares two different schedule objects by using properties
 * of Schedules. The good combination of properties will give the best cost function 
 * that leads to the right optimal schedule when using the a* algorithm. PriorityQueue 
 * used to store the open schedules requires this class to prioritise schedules.
 * 
 * @author CJ Bang, Alex Yoo
 *
 */
public class ScheduleComparator implements Comparator<Schedule> {
	/**
	 * Compares two different schedules. If the first schedule is closer to 
	 * the optimal schedule than the second, returns positive integer,
	 * if same, returns 0
	 * if worse (or further) then returns negative integer.
	 * @return int
	 */
//	@Override
//	public int compare(Schedule o1, Schedule o2) {
//		CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();
//		return costFunctionCalculator.getTotalCostFunction(o1) - costFunctionCalculator.getTotalCostFunction(o2);
//	}
	@Override
	public int compare(Schedule o1, Schedule o2) {
		CostFunctionCalculator costFunctionCalculator = new CostFunctionCalculator();
		if (o1.hasSetCost() && o2.hasSetCost()) {
			return o1.getCost() - o2.getCost();
		} else if (o1.hasSetCost() && !o2.hasSetCost()) {
			return o1.getCost() - costFunctionCalculator.getTotalCostFunction(o2);
		} else if (o2.hasSetCost() && !o1.hasSetCost()) {
			return costFunctionCalculator.getTotalCostFunction(o1) - o2.getCost();
		}
		return costFunctionCalculator.getTotalCostFunction(o1) - costFunctionCalculator.getTotalCostFunction(o2);
	}
}
