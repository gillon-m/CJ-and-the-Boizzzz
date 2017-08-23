package gui;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import scheduler.Schedule;

/**
 * VisualiserController controls the information displayed on the GUI from the scheduling algorithm
 * @author Gillon Manalastas
 *
 */
public class VisualiserController implements ScheduleListener{
	Visualiser _visualiser;
	Schedule _schedule;
	Calendar _calendar = Calendar.getInstance();
	DateFormat _timeFormat = new SimpleDateFormat("mm:ss.SSS");
	Data _data;
	Timer _timer = new Timer();

	public VisualiserController(Visualiser visualiser, Data data){
		_data=data;
		_visualiser = visualiser;
		
		//set timer to 0;
		_calendar.set(Calendar.MILLISECOND, 0);
		_calendar.set(Calendar.SECOND, 0);
		_calendar.set(Calendar.MINUTE, 0);
		_calendar.set(Calendar.HOUR_OF_DAY, 10);
		_timer.scheduleAtFixedRate(_timerTask, 1, 1); //invoke timer every millisecond
	}
	
	/**
	 * Timer task object used to track the total elapsed time
	 */
	TimerTask _timerTask = new TimerTask() {
		@Override
		public void run() {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						if(!_data.isFinished()){ //check is the scheduler has finished
							_data.setCurrentTime();
							System.out.println(_data.getElapsedTime());						}
						else{
							_timer.cancel();
							_timer.purge();
						}
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void update() {
		_visualiser.getJTextArea().setText("Vertex = " +_data.getCurrentSchedule().getLastUsedVertex().getName() + 
				"\t|Time Taken = " + _data.getCurrentSchedule().getTimeOfSchedule() + "\n" + _data.getCurrentSchedule().toString());
		_visualiser.setScheduleCount(_data.getTotalNumberOfCreatedSchedules());
		_calendar.setTimeInMillis(_data.getElapsedTime());
		_visualiser.setTimeElapsed(_timeFormat.format(_calendar.getTimeInMillis()));
		//System.out.println(_timeFormat.format(_calendar.getTimeInMillis()));
	}
}
