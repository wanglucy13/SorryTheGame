package sorryclient;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import customUI.ClearPanel;
import customUI.PaintedButton;
import customUI.PaintedLabel;
import customUI.PaintedPanel;
import library.FontLibrary;
import library.ImageLibrary;
import networking.MessageConstants;

public class ColorSelector extends PaintedPanel{
	
	private static final long serialVersionUID = 1900724217285760485L;
	
	private String selection;
	
	private final int numOptions = 4;
	private final PaintedButton[] optionButtons;
	
	private final PaintedButton confirmButton;
	private JButton exitButton;
	
	public JLabel timeLabel;
	
	public boolean confirmSelected;
	
	private final static String selectColorString = "Select your color";
	
	private final static String[] colorNames = {"Red", "Blue", "Green", "Yellow"};
//	private final static Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
	
	private final static Insets spacing = new Insets(60,80,60,80);
	
	public String getColor(){
		return selection;
	}
	
//	public Color getPlayerColor() {
//		return selection;
//	}
	
	public ColorSelector(ActionListener confirmAction, ActionListener exitAction, Image inImage) {
		super(inImage,true);
		confirmButton = new PaintedButton(
				"Confirm",
				ImageLibrary.getImage("images/buttons/grey_button00.png"),
				ImageLibrary.getImage("images/buttons/grey_button01.png"),
				22
				);
		confirmButton.addActionListener(confirmAction);
		confirmButton.setEnabled(false);
		
		exitButton = new JButton();
		exitButton.addActionListener(exitAction);
		
		
		JLabel selectColorLabel = new JLabel(selectColorString);
		selectColorLabel.setFont(FontLibrary.getFont("fonts/kenvector_future_thin.ttf", Font.PLAIN, 28));
		
		timeLabel = new JLabel();
		timeLabel.setOpaque(false);
		timeLabel.setBackground(new Color(0, 0, 0, 0));
		timeLabel.setFont(FontLibrary.getFont("fonts/kenvector_future_thin.ttf", Font.PLAIN, 28));
		
		ClearPanel topPanel = new ClearPanel();
		topPanel.setLayout(new GridBagLayout());
		topPanel.setBorder(new EmptyBorder(spacing));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		topPanel.add(timeLabel, gbc);
		gbc.gridy = 1;
		gbc.insets = new Insets(30,0,0,0);
		topPanel.add(selectColorLabel, gbc);
		
		ClearPanel centerPanel = new ClearPanel();
		centerPanel.setLayout(new GridLayout(2,2,10,10));
		Font buttonFont = new Font("",Font.BOLD,22);
		optionButtons = new PaintedButton[numOptions];
		for(int i = 0; i < numOptions; ++i) {
			optionButtons[i] = new PaintedButton(
					colorNames[i],
					ImageLibrary.getImage("images/buttons/"+colorNames[i].toLowerCase()+"_button00.png"),
					ImageLibrary.getImage("images/buttons/"+colorNames[i].toLowerCase()+"_button01.png"),
					22
					);
			final int buttonSelection = i;
			optionButtons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					OutMail.send(MessageConstants.BROADCAST, MessageConstants.COLOR_SELECT + colorNames[buttonSelection]);
					
					//selectionIndex = buttonSelection;
					selection = colorNames[buttonSelection];
					//for(JButton button : optionButtons) button.setEnabled(true);
					//optionButtons[buttonSelection].setEnabled(false);
					//confirmButton.setEnabled(true);
				}
			});
			optionButtons[i].setFont(buttonFont);
			centerPanel.add(optionButtons[i]);
		}
		centerPanel.setBorder(new EmptyBorder(new Insets(0, 80, 0, 80)));
		
		ClearPanel bottomPanel = new ClearPanel();
		bottomPanel.setLayout(new GridLayout(1,3));
		bottomPanel.setBorder(new EmptyBorder(spacing));
		bottomPanel.add(Box.createGlue());
		bottomPanel.add(Box.createGlue());
		bottomPanel.add(confirmButton);
		
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		add(topPanel);
		add(centerPanel);
		add(bottomPanel);
	}
	
	public void colorReserve(String colors) {
		for(int i = 0; i < colorNames.length; i++) {
			if(colors.contains(colorNames[i])) {
				optionButtons[i].setEnabled(false);
			}
		}
	}

	public void colorSwap(String msg) {
		String[] split = msg.split(MessageConstants.SPLIT);
		if(msg.contains(MessageConstants.SUCCESS)) {
			confirmButton.setEnabled(true);
			return;
		}
		for(int i = 0; i < colorNames.length; i++) {
			if(split[1].startsWith(colorNames[i])) {
				optionButtons[i].setEnabled(true);
			}
			if(split[1].endsWith(colorNames[i])) {
				optionButtons[i].setEnabled(false);
			}
		}
	}

	public void free(String color) {
		for(int i = 0; i < colorNames.length; i++) {
			if(color.equals(colorNames[i])) {
				optionButtons[i].setEnabled(true);
			}
		}
	}
	
	public void startTimer(){
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
            int counter = 30;
            public void run() {
            	if(confirmSelected) timer.cancel();
            	if(counter > 9) timeLabel.setText("00:"+counter--);
            	else timeLabel.setText("00:0"+counter--);
                if (counter < 0){
                	timer.cancel();
                	System.exit(0);
                }  
                repaint();
            }
        }, 0, 1000);
	}

}
