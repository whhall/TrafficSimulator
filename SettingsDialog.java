package traffic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * {@code SettingsDialog} is class that implements displaying
 *  a dialog to change the percentages of various vehicle types.
 *  @version 20181130
 *  @author William Hall
 */
public class SettingsDialog extends JDialog {

	private JTextField			carPercentage;
	private JTextField			motorcyclePercentage;

	/*
	 *  This is the action to perform when the OK button
	 *  is activated.
	 */
	private class OKaction implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			int		newCarPercentage;
			int		newMotorcyclePercentage;
			String	userInput;

			/*
			 *  Get what the user entered for each field and
			 *  convert it to an integer.
			 */
			 userInput = carPercentage.getText().trim();
			 newCarPercentage = Integer.parseInt(userInput);

			 userInput = motorcyclePercentage.getText().trim();
			 newMotorcyclePercentage = Integer.parseInt(userInput);
			 /*
			  *  Check to make sure the user input is greater than 0
			  *  but less than 100.
			  */
			 if ((newCarPercentage < 0) ||
			    (newMotorcyclePercentage < 0) ||
			    ((newCarPercentage + newMotorcyclePercentage) > 100)){
				setValues();
				return;
			}
			/*
			 *  Set the new percentages in each class,
			 *  then close the settings dialog
			 */
			Car.setRandomPercentage(newCarPercentage);
			Motorcycle.setRandomPercentage(newMotorcyclePercentage);
			setVisible(false);
		}
	}

	/**
	 *  This method fetches the value to be displayed in the text
	 *  fields of the setting dialog.
	 */
	 public void setValues()
	 {
		 carPercentage.setText("" + Car.getRandomPercentage());
		 motorcyclePercentage.setText("" + Motorcycle.getRandomPercentage());
	 }

	/**
	 *  Create a setting dialog box that may be reused.
	 *  @param ourFrame The frame that contains our dialog.
	 */
	public SettingsDialog(JFrame ourFrame)
	{
		/**
		 * Section 12.7.2, page 742.
		 */
		super(ourFrame, "Settings", true);

		JPanel				panel;
		GridBagLayout		gridBag;
		GridBagConstraints	gridBagConstraints;
		JLabel				vehiclePercentageLabel;
		JLabel				carLabel;
		JLabel				motorcycleLabel;
		JButton				OKButton;
		JButton				cancelButton;
		Rectangle			frameBounds;

		/*
		 * Get a panel to put everything in.
		 */
		panel = new JPanel();

		/*
		 * Section 12.6.1, pages 701-712
		 * Section 12.6.1.5, page 705
		 *
		 * Set up our GridBagLayout and initialize
		 * some of our constraints
		 *
		 */
		 gridBag = new GridBagLayout();
		 gridBagConstraints = new GridBagConstraints();
		 gridBagConstraints.insets = new Insets(10, 10, 10, 10);
		 gridBagConstraints.ipadx = 5;
		 panel.setLayout(gridBag);

		 /*
		  * Section 12.3.1, pages 649-651
		  *
		  * Put a label that spans the dialog box at the top
		  */
		 vehiclePercentageLabel = new JLabel("Vehicle Percentages",
		 									 SwingConstants.CENTER);
		 gridBagConstraints.gridx	  = 0;
		 gridBagConstraints.gridwidth = 2;
		 gridBagConstraints.gridy     = 0;
		 gridBag.setConstraints(vehiclePercentageLabel,
		 						gridBagConstraints);
		 panel.add(vehiclePercentageLabel);

		 /*
		  * Put the next line, put labels for the vehicles with
		  * car on the left.
		  */
		 carLabel = new JLabel(Car.getLabel(), SwingConstants.CENTER);
		 gridBagConstraints.gridx	  = 0;
		 gridBagConstraints.gridwidth = 1;
		 gridBagConstraints.gridy     = 1;
		 gridBag.setConstraints(carLabel,
		 						gridBagConstraints);
		 panel.add(carLabel);
		 /*
		  * Put motorcycle on the right
		  */
		 motorcycleLabel = new JLabel(Motorcycle.getLabel(),
		 							  SwingConstants.CENTER);
		 gridBagConstraints.gridx	  = 1;
		 gridBagConstraints.gridy     = 1;
		 gridBag.setConstraints(motorcycleLabel,
		 						gridBagConstraints);
		 panel.add(motorcycleLabel);
		 /*
		  * Section 12.3.1, pages 649-651
		  *
		  * Text fields on next row with car on the left
		  */
		 carPercentage = new JTextField(2);
		 carPercentage.setHorizontalAlignment(SwingConstants.CENTER);
		 gridBagConstraints.gridx	  = 0;
		 gridBagConstraints.gridy     = 2;
		 gridBag.setConstraints(carPercentage,
		 						gridBagConstraints);
		 panel.add(carPercentage);
		 /*
		  * Section 12.3.1, pages 649-651
		  *
		  * Text fields on next row with car on the left
		  */
		 motorcyclePercentage = new JTextField(2);
		 motorcyclePercentage.setHorizontalAlignment(SwingConstants.CENTER);
		 gridBagConstraints.gridx	  = 1;
		 gridBagConstraints.gridy     = 2;
		 gridBag.setConstraints(motorcyclePercentage,
		 						gridBagConstraints);
		 panel.add(motorcyclePercentage);

		 /*
		  *  Add our OK button on the next line on the left
		  */
		 OKButton = new JButton("OK");
		 OKButton.addActionListener(new OKaction());
		 gridBagConstraints.gridx = 0;
		 gridBagConstraints.gridy = 3;
		 gridBag.setConstraints(OKButton, gridBagConstraints);

		 panel.add(OKButton);

		 /*
		  * Add our Cancel button on the right
		  */
		 cancelButton = new JButton("Cancel");
		 cancelButton.addActionListener(event -> {
			 	/*
			 	 *  Section 12.7, page 743
			 	 *
			 	 *  Hide the dialog box
			 	 */
			 	 setVisible(false);
			 });
		 gridBagConstraints.gridx = 1;
		 gridBagConstraints.gridy = 3;
		 gridBag.setConstraints(cancelButton, gridBagConstraints);

		 panel.add(cancelButton);

		 /*
		  *  Add our panel to dialog.
		  */
		  add(panel);
		  pack();

		  setValues();
		  /*
		   * Finds the size of the frame
		   */
		  frameBounds = ourFrame.getBounds();

		  /*
		   *  Center the dialog
		   */

		  setLocation((int)(frameBounds.getX() +
		  				   ((frameBounds.getWidth() - getWidth()) / 2)),
		  			  (int)(frameBounds.getY() +
		  			       ((frameBounds.getHeight() - getHeight()) / 2)));

	}
}