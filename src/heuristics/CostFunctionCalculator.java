package heuristics;

import scheduler.Schedule;
/**
 * This class calculates the cost function used in ScheduleComparator for A* Algorithm
 * 
 * It returns the highest cost function from the heuristics	
 * 
 * @author Alex Yoo
 *
 */

public class CostFunctionCalculator {
	/**
	 * returns the highest cost out of these 3 heuristic calculator
	 * @return 
	 */
	public int getTotalCostFunction(Schedule currentSchedule) {
		ICostFunction p1 = new IdleAndComputation();
		ICostFunction p2 = new MaxMinimalDataReadyTime();
		ICostFunction p3 = new MaxStartTimeAndBottomLevel();
		return Math.max(p1.getCostFunction(currentSchedule), Math.max(p2.getCostFunction(currentSchedule), p3.getCostFunction(currentSchedule)));
	}
	
}
