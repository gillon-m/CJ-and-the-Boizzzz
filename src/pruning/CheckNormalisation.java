package pruning;

import scheduler.Processor;
import scheduler.Schedule;
/**
 * Checks for if the current schedule should be normalized
 * 
 * @author Alex Yoo
 *
 */
public class CheckNormalisation implements IPruning {

	@Override
	public boolean isScheduleNeeded(Schedule currentSchedule, Schedule existingSchedule) {
		return !this.isThereNormalisation(currentSchedule, existingSchedule);
	}
	/**
	 * Goes through all the processors to check if they all have matching processors 
	 * for both schedules
	 * 
	 * @param currentSchedule
	 * @param existingSchedule
	 * @return True if there are schedules that should be normalized (so should not be added)
	 * 			False if there is no schedules that should be normalized (so added)
	 */
	private boolean isThereNormalisation(Schedule currentSchedule, Schedule existingSchedule){
		int numberOfProcessor = currentSchedule.getNumberOfProcessors();
		for(int i = 0; i < numberOfProcessor; i++){
			boolean foundSameProcessor = false;
			for(int j = 0; j < numberOfProcessor; j++){
				if(isProcessorSame(existingSchedule.getProcessor(i), currentSchedule.getProcessor(j))){
					foundSameProcessor = true;
					break;
				}
			}
			if(!foundSameProcessor){
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
