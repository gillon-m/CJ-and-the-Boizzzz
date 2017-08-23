package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import scheduler.Schedule;
import java.awt.FlowLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.JLabel;

public class Visualiser extends JFrame {
	private JTextArea _schedulerText = new JTextArea("0");
	private JLabel _timeLabel = new JLabel("New label");
	private JLabel _countLabel = new JLabel("New label");
	
	public Visualiser() {
		super("Optimal Task Schedule Generator");
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 5));
		panel.setBounds(10, 11, 464, 237);
		getContentPane().add(panel);
		panel.setLayout(null);
		_schedulerText.setBounds(10, 10, 444, 216);
		panel.add(_schedulerText);
		
		
		_timeLabel.setBounds(20, 259, 454, 14);
		getContentPane().add(_timeLabel);
		
		
		_countLabel.setBounds(20, 284, 454, 14);
		getContentPane().add(_countLabel);
		
		setSize(500,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public JTextArea getJTextArea() {
		return _schedulerText;
	}
	public JLabel getCountLabel(){
		return _countLabel;
	}

	public void setTimeElapsed(String time) {
		_timeLabel.setText("Time Elapsed: "+time);
	}
	public void setScheduleCount(int totalNumberOfCreatedSchedules) {
		_countLabel.setText("Total schedules created: "+totalNumberOfCreatedSchedules);
	}
}
