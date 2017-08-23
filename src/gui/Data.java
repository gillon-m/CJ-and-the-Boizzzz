package gui;

import scheduler.Schedule;

/**
 * Data objects are used to store information regarding the current scheduler. 
 * @author Gillon Manalastas
 *
 */
public class Data {
	private Schedule _currentSchedule=null;
	private int _numberOfSchedulesCreated=0;
	private boolean _isFinished=false;
	private int _totalNumberOfCreatedSchedules=0;
	
	public void updateCurrentSchedule(Schedule currentSchedule) {
		_currentSchedule=currentSchedule;
	}
	public void isFinished(boolean b) {
		_isFinished=b;
	}
	public boolean isFinished(){
		return _isFinished;
	}
	public Schedule getCurrentSchedule() {
		return _currentSchedule;
	}
	public void updateTotalNumberOfCreatedSchedules(int i) {
		_totalNumberOfCreatedSchedules=i;
	}
	public int getTotalNumberOfCreatedSchedules(){
		return _totalNumberOfCreatedSchedules;
	}
}
