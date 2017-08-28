package gui.data;

import java.util.List;

import graph.Vertex;
import scheduler.Schedule;

/**
 * Data objects are used to store information regarding the current state of the scheduler. 
 * Data is a singleton so used getInstance() method to get a Data object
 * @author Gillon Manalastas
 *
 */
public class Data {
	private int _totalNumberOfCreatedSchedules=0;
	private static Data _data = new Data();
	private Schedule _bestSchedule;
	private Schedule _currentSchedule;
	
	private Data(){
	}

	/**
	 * Returns the singleton Data object
	 * @return Data object
	 */
	public static Data getInstance(){
		return _data;
	}
	
	/**
	 * Returns the current schedule
	 * @return current schedule
	 */
	public Schedule getCurrentSchedule() {
		return _currentSchedule;
	}
	
	/**
	 * Updates the total number of schedules created
	 * @param total number of schedules created 
	 */
	public void updateTotalNumberOfCreatedSchedules(int i) {
		_totalNumberOfCreatedSchedules=i;
	}
	
	/**
	 * Returns the total number of schedules created
	 * @return total number of schedules
	 */
	public int getTotalNumberOfCreatedSchedules(){
		return _totalNumberOfCreatedSchedules;
	}
	
	/**
	 * Adds the current schedule to the list of concurectSchedules. Determines which schedule is the best
	 * @param The currentSchedule
	 */
	public void setCurrentSchedule(Schedule currentSchedule){
		_currentSchedule=currentSchedule;
		if(_bestSchedule==null){
			_bestSchedule=currentSchedule;
		}
		else{
			List<Vertex> currentScheduleVertices = currentSchedule.getAllUsedVerticesWithoutEmpty();
			List<Vertex> bestScheduleVertices = _bestSchedule.getAllUsedVerticesWithoutEmpty();
			if(currentScheduleVertices.size()>bestScheduleVertices.size()){
				_bestSchedule=currentSchedule;
			}
			else if(currentScheduleVertices.size()==bestScheduleVertices.size()){
				if(currentSchedule.getTimeOfSchedule()<_bestSchedule.getTimeOfSchedule()){
					_bestSchedule=currentSchedule;
				}
			}
		}
	}

	/**
	 * Returns the most completed schedule with the fastest time
	 * @return Schedule with the most completed nodes and fastest time
	 */
	public Schedule getBestSchedule() {
		return _bestSchedule;
	}
}
