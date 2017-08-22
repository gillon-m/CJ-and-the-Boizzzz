package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import scheduler.Schedule;

public class Visualiser extends JFrame implements ScheduleListener {
	private JTextArea countLabel1 = new JTextArea("0");
	public Visualiser() {
		super("Optimal Task Schedule Generator");
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.fill = GridBagConstraints.NONE;
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		add(countLabel1, gc);
		
		setSize(200,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	@Override
	public void update(Schedule schedule) {
		countLabel1.setText("Vertex = " + schedule.getLastUsedVertex().getName() + 
				"\t|Time Taken = " + schedule.getTimeOfSchedule() + "\n" + schedule.toString());
	}
}
