package components;

import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import scheduler.Processor;
import scheduler.Schedule;

public class Pruning {
	
	public Pruning(){
	}
	
	public boolean checkUsingPruning(PriorityBlockingQueue<Schedule> openSchedules, List<Schedule> closedSchedules, Schedule currentSchedule){
		
		if(this.isThereDuplicates(openSchedules, closedSchedules, currentSchedule) 
				&& this.isThereEquivalentNodes(openSchedules, closedSchedules, currentSchedule) 
				&& this.checkNormalisation(openSchedules, closedSchedules, currentSchedule))
			return true;
		return false;
	}
	
	private boolean isThereDuplicates(PriorityBlockingQueue<Schedule> openSchedules, List<Schedule> closedSchedules, Schedule currentSchedule){
		for(Schedule schedule : openSchedules){
			for(int i = 0; i < schedule.getNumberOfProcessors(); i++){
				if(isProcessorSame(schedule.getProcessor(i), currentSchedule.getProcessor(i))){
					return false;
				}
			}
		}
		for(Schedule schedule : closedSchedules){
			for(int i = 0; i < schedule.getNumberOfProcessors(); i++){
				if(isProcessorSame(schedule.getProcessor(i), currentSchedule.getProcessor(i))){
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isThereEquivalentNodes(PriorityBlockingQueue<Schedule> openSchedules, List<Schedule> closedSchedules, Schedule currentSchedule){
		int numberOfProcessor = currentSchedule.getNumberOfProcessors();
		for(Schedule schedule : openSchedules){
			for(int i = 0; i < numberOfProcessor; i++){
				boolean foundSameProcessor = false;
				for(int j = 0; j < numberOfProcessor; j++){
					if(isProcessorSame(schedule.getProcessor(i), currentSchedule.getProcessor(j))){
						break;
					}
					if(j == numberOfProcessor-1){
						return false;
					}
				}
			}
		}
		for(Schedule schedule : closedSchedules){
			for(int i = 0; i < numberOfProcessor; i++){
				boolean foundSameProcessor = false;
				for(int j = 0; j < numberOfProcessor; j++){
					if(isProcessorSame(schedule.getProcessor(i), currentSchedule.getProcessor(j))){
						break;
					}
					if(j == numberOfProcessor-1){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private boolean checkNormalisation(PriorityBlockingQueue<Schedule> openSchedules, List<Schedule> closedSchedules, Schedule currentSchedule){
		
		return true;
	}

	
	private boolean isProcessorSame(Processor p1, Processor p2){
		if(p1.equals(p2)){
			return true;
		}
		return false;
	}
	
	
	// May do if enough time
	private boolean checkUsingUpperBound(){
		return true;
	}
	// May do if enough time
	private boolean checkPartialExpansion(){
		return true;
	}
}
