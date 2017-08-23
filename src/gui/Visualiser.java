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

public class Visualiser extends JFrame {
	private JTextArea _countLabel1 = new JTextArea("0");

	public Visualiser() {
		super("Optimal Task Schedule Generator");
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 5));
		panel.setBounds(10, 11, 464, 237);
		getContentPane().add(panel);
		panel.add(_countLabel1);
		
		setSize(500,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public JTextArea getJTextArea() {
		return _countLabel1;
	}
}
