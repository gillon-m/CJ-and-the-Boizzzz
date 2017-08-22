package gui;

import scheduler.Schedule;

public class VisualiserController implements ScheduleListener{
	Visualiser _visualiser;
	
	public VisualiserController(Visualiser visualiser){
		_visualiser = visualiser;
	}
	
	@Override
	public void update(Schedule schedule) {
		_visualiser.getJTextArea().setText("Vertex = " + schedule.getLastUsedVertex().getName() + 
				"\t|Time Taken = " + schedule.getTimeOfSchedule() + "\n" + schedule.toString());
	}
}
