package pruning;

import scheduler.Processor;
import scheduler.Schedule;
/**
 * Check if the two schedules are a duplicate
 * 
 * @author Alex Yoo
 *
 */
public class CheckDuplicates implements IPruning {

	@Override
	public boolean isScheduleNeeded(Schedule currentSchedule, Schedule existingSchedule) {
		return !this.isThereDuplicates(currentSchedule, existingSchedule);
	}
	/**
	 * Goes through all the processors to check if they all have matching processors 
	 * for both schedules
	 * 
	 * @param currentSchedule
	 * @param existingSchedule
	 * @return True if there are duplicate schedules (so should not be added)
	 * 			False if there are no duplicate schedules (so added)
	 */
	private boolean isThereDuplicates(Schedule currentSchedule, Schedule existingSchedule){
		int numberOfProcessor = currentSchedule.getNumberOfProcessors();
		for(int i = 0; i < numberOfProcessor; i++){
			if(!isProcessorSame(existingSchedule.getProcessor(i), currentSchedule.getProcessor(i))){
				return false;
			}
		}
		return true;
	}
	/**
	 * checks if processor is the same
	 * @param p1
	 * @param p2
	 * @return
	 */
	private boolean isProcessorSame(Processor p1, Processor p2){
		if(p1.equalCheck(p2)){
			return true;
		}
		return false;
	}

}
