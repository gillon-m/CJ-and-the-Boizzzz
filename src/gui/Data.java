package gui;

import scheduler.Schedule;

/**
 * Data objects are used to store information regarding the current scheduler. 
 * @author Gillon Manalastas
 *
 */
public class Data {
	private Schedule _schedule=null;
	private int _numberOfSchedulesCreated=0;
	private boolean _isFinished=false;
	
	public void updateCurrentSchedule(Schedule currentSchedule) {
		_schedule=currentSchedule;
	}
	public void isFinished(boolean b) {
		_isFinished=b;
	}
	public boolean isFinished(){
		return _isFinished;
	}
	public Schedule getCurrentSchedule() {
		return _schedule;
	}
}
