import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

/**
 * JAP - CS Academic Level 4
 * Controller logic for user input coordination
 * Course: CST8221-302 - Java Application Programming
 * @author Professor: Daniel Cormier
 * @author Aiden Mackean: 040707105
 * @author Greg Rowat: 041001565
 */
public class PiccrossController {

	/** static variable to control single instantiation */
	private static PiccrossController contSingleton = null;

	/** controller object passed in through config method */
	private PiccrossView view;

	/** model object passed in through config method */
	private PiccrossModel model;

	/** timer object passed in through config method */
	private ControllableTimer timer;

	/** connection class */
	private PiccrossNetworkModalVC connection;

	/** Reference to Piccross client used to pass to the networkModel*/
	private PiccrossClient client;

	/** 
	 * private constructor to be instantiated by facilitator method
	 */
	private PiccrossController() {

		// this is called in the helper method 
		// due to the nature of singleton initialization

	}

	/** 
	 * method called to instantiate new controller singleton
	 * @return singleton initialized Controller object
	 */
	public static PiccrossController instantiate() {

		// check for null val, instantiate new object if so
		if (contSingleton == null) contSingleton = new PiccrossController();

		// return instantiated value 
		return contSingleton;

	}

	/**
	 *  configuration method, sets class fields to object params passed in from main 
	 * @param view view component of the game ui
	 * @param model backend logic of the game
	 * @param timer timer object of the game lofic
	 * @param client reference to client object
	 */
	public void configObjects(PiccrossView view, PiccrossModel model, ControllableTimer timer, PiccrossClient client) {
		this.view = view;
		this.model = model;
		this.timer = timer;
		this.client = client;

		// adds the listener to the ui
		view.addPiccrossListener(new PiccrossListener());

	}

	/**
	 * Inner class implementation of Actionlistener interface and methods
	 * @author Aiden Mackean
	 * @author Greg Rowat
	 */
	private class PiccrossListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// outputs to the text box 
			//view.outputText(e.getActionCommand());

			// the order of precedence in this call is important as the resetting features
			// need to be checked first to ensure their functionality when the game is over

			if (e.getActionCommand().equals("send")) {

				JTextField source = (JTextField) e.getSource();

				// check that connection is made
				if(client.getConnectionStatus()) {
					client.sendMessage(source.getText());
				}

				source.setText("");

			}

			// selection for new
			if (e.getActionCommand().equals("New")) {

				// get new model
				model.newModel();

				// reset the view
				view.resetView();

				// gotta reset the timer. If stopped on game over
				// needs to be started again (option 1)
				timer.setStatus(1);
				timer.setStatus(3);


			}

			// selection for reset
			if (e.getActionCommand().equals("Reset")) {

				// get new model
				model.resetModel();

				// reset the view
				view.resetView();

				// gotta reset the timer. If stopped on game over
				// needs to be started again (option 1)
				timer.setStatus(1);
				timer.setStatus(3);


			}

			// generate debug full condition
			if (e.getActionCommand().equals("Full")) {
				// gotta reset the model
				model.debugModel("Full");

				// reset the view
				view.resetView();

				// gotta reset the timer. If stopped on game over
				// needs to be started again (option 1)
				timer.setStatus(1);
				timer.setStatus(3);
			}


			// Generate debug edges filled condition
			if (e.getActionCommand().equals("Edges")) {
				// gotta reset the model
				model.debugModel("Edges");

				// reset the view
				view.resetView();

				// gotta reset the timer. If stopped on game over
				// needs to be started again (option 1)
				timer.setStatus(1);
				timer.setStatus(3);
			}

			// generate debug center filled 
			if (e.getActionCommand().equals("Center")) {
				// gotta reset the model
				model.debugModel("Center");

				// reset the view
				view.resetView();

				// gotta reset the timer. If stopped on game over
				// needs to be started again (option 1)
				timer.setStatus(1);
				timer.setStatus(3);
			}

			// generate debug empty condition
			// SHOULD WIN AUTOMATICALLY
			if (e.getActionCommand().equals("Win")) {

				// reset timer
				timer.setStatus(3);
				// stop timer
				timer.setStatus(2);


				// gotta reset the model
				model.debugModel("Win");

				// reset the view
				view.resetView();

				// show game over
				view.gameOver();
			}

			// generate debug empty condition
			// UNWINNABLE
			if (e.getActionCommand().equals("noWin")) {
				// gotta reset the model
				model.debugModel("noWin");

				// reset the view
				view.resetView();

				// gotta reset the timer. If stopped on game over
				// needs to be started again (option 1)
				timer.setStatus(1);
				timer.setStatus(3);
			}

			// selection for network connect option
			if (e.getActionCommand().equals("Connect")) {

				// stop timer
				timer.setStatus(2);

				view.setConnection();

				connection = new PiccrossNetworkModalVC(view.sendFrameReference(),client, view);

				// check if user cancelled out of frame instead of connecting. Resets network menu
				// connection option to enabled, disconnect disabled
				if (connection.pressedConnect()==false) {
					view.setConnection();
				}

				// start timer
				timer.setStatus(1);

			}

			// selection for disconnecting
			if (e.getActionCommand().equals("Disconnect")) {

				//if the client is currently connected when the user selects disconnect
				//set client flag to begin client shutdown process
				if(client.getConnectionStatus()) {

					//call method to manually disconnect the client socket when user selected
					//Disconnect from network dialogue. further graceful shutdown done in client
					client.Disconect();
				}
			}


			// check for gameOver boolean in model. 
			// If set, breaks method sequence, no code will be called
			// after this line. Kind of messy but working so yay
			if (model.isGameOver()) {
				return;
			}

			// selection for clicking a game tile button
			if(e.getActionCommand().substring(0, 2).equals("Bu")) {

				// pulls the ascii char val out of the action command string
				// converts this to integer value with (- '0')
				int first = e.getActionCommand().charAt(8) - '0';
				int second = e.getActionCommand().charAt(10) - '0';		

				// if button is clicked nothing should happen
				if(view.getButtonStatus(first, second)) {
					// update the square. Specifies box item, mark condition, correct move or not
					view.setButtonStatus(first, second, model.getMark(), model.checkIfRight(first, second));
				}

				// sets the game over logic if backend boolean is triggered
				if (model.isGameOver()) {
					// stop timer
					timer.setStatus(2);
					// disable mark button and rules button / launch game over
					view.gameOver();

				}

			}

			// selection for clicking mark option
			if (e.getActionCommand().equals("Mark")) {
				model.updateMark();
			}

			// selection for rules tab
			if (e.getActionCommand().equals("rules")) {
				// pause timer
				timer.setStatus(2);
				// call rules timer
				view.rulesWindow();
				// continue timer
				timer.setStatus(1);
			}

			// selection for menu item exit
			if (e.getActionCommand().equals("Exit")) {
				System.exit(0);
			}

			// about selection
			if (e.getActionCommand().equals("About")) {
				// pause timer
				timer.setStatus(2);
				// call the about window
				view.about();

				// continue timer
				timer.setStatus(1);		

			}

			// selection for solution squares
			if (e.getActionCommand().equals("Solution")) {

				view.getSolution();	
			}


		} // end of actionPerformed method

	} // end of interface implementation 


}

