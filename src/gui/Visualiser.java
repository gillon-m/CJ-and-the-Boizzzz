package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import java.awt.Color;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.GridLayout;

@SuppressWarnings("serial")
public class Visualiser extends JFrame {
	JLabel timeElapsedLabel = new JLabel("",SwingConstants.RIGHT);
	JLabel scheduleCountLabel = new JLabel("",SwingConstants.RIGHT);
	JPanel schedulePanel = new JPanel();
	JPanel taskGraphPanel = new JPanel();
	JPanel lineChartPanel = new JPanel();
	JPanel textPanel = new JPanel();
	private final JLabel scheduleCountHeaderLabel = new JLabel("Schedules Created:");
	private final JLabel timeElapsedHeaderLabel = new JLabel("Time Elapsed:");
	
	public Visualiser() {
		super("Optimal Task Schedule Generator");
		getContentPane().setBackground(Color.WHITE);
		getContentPane().setLayout(null);
		schedulePanel.setBackground(Color.WHITE);
		
		schedulePanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		schedulePanel.setBounds(26, 22, 594, 244);
		getContentPane().add(schedulePanel);
		schedulePanel.setLayout(new BorderLayout(0,0));
		taskGraphPanel.setBackground(Color.WHITE);
		
		taskGraphPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		taskGraphPanel.setBounds(340, 292, 280, 244);
		getContentPane().add(taskGraphPanel);
		taskGraphPanel.setLayout(new BorderLayout(0, 0));
		lineChartPanel.setBackground(Color.WHITE);
		
		lineChartPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		lineChartPanel.setBounds(26, 292, 280, 244);
		lineChartPanel.setLayout(new BorderLayout(0, 0));
		getContentPane().add(lineChartPanel);
		textPanel.setBackground(Color.WHITE);
		
		lineChartPanel.add(textPanel, BorderLayout.SOUTH);
		textPanel.setLayout(new GridLayout(2, 2, 0, 0));
		textPanel.add(scheduleCountHeaderLabel);
		textPanel.add(scheduleCountLabel);
		
		textPanel.add(timeElapsedHeaderLabel);
		textPanel.add(timeElapsedLabel);
		
		setSize(650,600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}
