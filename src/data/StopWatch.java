package data;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import org.jfree.data.time.RegularTimePeriod;

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
	public void start(){
		_startTime = System.currentTimeMillis();
		_timerTask = new TimerTask() {
			@Override
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							_currentTime=System.currentTimeMillis();	
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
		cancel();
		purge();
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
	
	/**
	 * Returns the elapsed time in the representation of a Date object
	 * @return Returns the elapsed time in a date
	 */
	public Date getElapsedTimeDate(){
		long elapsedTime = _currentTime-_startTime;
		_calendar.setTimeInMillis(elapsedTime);
		Date elapsedTimeDate = _calendar.getTime();
		return elapsedTimeDate;
	}
}
