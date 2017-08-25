package data;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

/**
 * StopWatch is a singleton object that track the time elapsed given to the nearest millisecond.
 * Instantiate a StopWatch object using the getInstance() method
 * @author Gillon
 *
 */
public class StopWatch extends Timer{
	private Calendar _calendar;
	private DateFormat _timeFormat;
	private boolean _isFinished;
	private TimerTask _timerTask;
	private long _currentTime;
	private long _startTime;
	private Timer _timer;
	private static StopWatch _stopWatch=new StopWatch();

	private StopWatch(){
		_calendar = Calendar.getInstance();
		_timeFormat = new SimpleDateFormat("mm:ss.SSS");
		//_timer = new Timer();
		//set timer to 0;
		_calendar.set(Calendar.MILLISECOND, 0);
		_calendar.set(Calendar.SECOND, 0);
		_calendar.set(Calendar.MINUTE, 0);
		_calendar.set(Calendar.HOUR_OF_DAY, 10);
	}
	
	/**
	 * Returns the singleton StopWatch instance
	 * @return StopWatch instance
	 */
	public static StopWatch getInstance(){
		return _stopWatch;
	}
	
	/**
	 * Start the timer
	 */
	public void startTimer(){
		_startTime = System.currentTimeMillis();
		_timerTask = new TimerTask() {
			@Override
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							//if(!_isFinished){ //check is the scheduler has finished
								_currentTime=System.currentTimeMillis();	
								System.out.println(getElapsedTime());
							//}
							//else{ //stop timer when scheduler has finished
							//	cancel();
							//	purge();
							//}
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		scheduleAtFixedRate(_timerTask, 1, 1); //invoke timer every millisecond	
	}
	
	/**
	 * Stops the timer
	 */
	public void stop(){
		_timer.cancel();
		_timer.purge();
	}
	
	/**
	 * Returns the time elapsed to the nearest millisecond
	 * @return Formatted string of the time elapsed
	 */
	public String getElapsedTime(){
		long elapsedTime = _currentTime-_startTime;
		_calendar.setTimeInMillis(elapsedTime);
		return _timeFormat.format(_calendar.getTimeInMillis());
	}
}