package heuristics;

import scheduler.Schedule;
/**
 * 
 * All Heuristic calculating classes implements this interface
 * To make sure it returns a cost function that the CostFunctionCalculator class can use
 * 
 * @author Alex Yoo
 *
 */
public interface ICostFunction {
	int getCostFunction(Schedule currentSchedule);
}
