package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import javax.swing.border.LineBorder;

import java.awt.Color;
import javax.swing.JLabel;
import java.awt.BorderLayout;

@SuppressWarnings("serial")
public class Visualiser extends JFrame {
	JTextArea schedulerText = new JTextArea("0");
	JLabel timeElapsedLabel = new JLabel("New label");
	JLabel scheduleCountLabel = new JLabel("New label");
	JPanel schedulePanel = new JPanel();
	JPanel taskGraphPanel = new JPanel();
	JPanel lineChartPanel = new JPanel();
	
	public Visualiser() {
		super("Optimal Task Schedule Generator");
		getContentPane().setLayout(null);
		
		schedulePanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		schedulePanel.setBounds(290, 11, 284, 539);
		getContentPane().add(schedulePanel);
		schedulePanel.setLayout(null);
		schedulerText.setBounds(10, 11, 264, 517);
		schedulePanel.add(schedulerText);
		
		timeElapsedLabel.setBounds(10, 511, 226, 14);
		getContentPane().add(timeElapsedLabel);
		
		scheduleCountLabel.setBounds(10, 536, 206, 14);
		getContentPane().add(scheduleCountLabel);
		
		taskGraphPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		taskGraphPanel.setBounds(10, 11, 270, 239);
		getContentPane().add(taskGraphPanel);
		taskGraphPanel.setLayout(new BorderLayout(0, 0));
		
		lineChartPanel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		lineChartPanel.setBounds(10, 261, 270, 239);
		getContentPane().add(lineChartPanel);
		
		setSize(600,600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}
