package heuristics;

import scheduler.Processor;
import scheduler.Schedule;
/**
 * Cost Function heuristics for Cost Function = (Total Idle time in schedule + Weight of all used vertices) divided by number of processors
 * 
 * @author Alex Yoo
 *
 */
public class IdleAndComputation implements ICostFunction {
	
	@Override
	public int getCostFunction(Schedule currentSchedule) {
		return this.idleAndComputationTime(currentSchedule);
	}
	
	private int idleAndComputationTime(Schedule currentSchedule) {
		int costFunction = 0;
		int totalCostOfAllProcessors = 0;
		int numberOfProcessors = currentSchedule.getNumberOfProcessors();
		
		for(Processor p : currentSchedule.getAllProcessors()) {
			totalCostOfAllProcessors += p.getLatestTime();
		}
		costFunction = totalCostOfAllProcessors/numberOfProcessors;
		
		return costFunction;
	}

}
