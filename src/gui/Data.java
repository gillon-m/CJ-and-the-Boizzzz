package gui;

import scheduler.Schedule;

/**
 * Data objects are used to store information regarding the current state of the scheduler. 
 * @author Gillon Manalastas
 *
 */
public class Data {
	private Schedule _currentSchedule=null;
	private int _numberOfSchedulesCreated=0;
	private boolean _isFinished=false;
	private int _totalNumberOfCreatedSchedules=0;
	long _startTime;
	long _endTime;
	long _elapsed;
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
	public void setStartTime() {
		_startTime = System.currentTimeMillis();
	}
	public void setCurrentTime() {
		_endTime = System.currentTimeMillis();
	}
	public long getElapsedTime(){
		long elapsedTime=_endTime-_startTime;
		return elapsedTime;
	}
}
