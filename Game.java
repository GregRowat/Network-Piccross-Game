import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Dimension;



/**
 * JAP - CS Academic Level 4
 * Main driver for piccross implementation
 * Course: CST8221-302 - Java Application Programming
 * @author Professor: Daniel Cormier
 * @author Aiden Mackean: 040707105
 * @author Greg Rowat: 041001565
 */
public class Game {
	
	private static final boolean debugMode = false;
	/**
	 * Main driver of program, instantiates frame / view objects runs the event Queue
	 * @param args argument parameters of main driver
	 */
	public static void main(String[] args) {
		
		
		//instantiate the load screen object
		PiccrossSplashScreen loadScreen = new PiccrossSplashScreen(3000);
		
		PiccrossModel model = new PiccrossModel();
		
		model.run();
		
		//instantiate a view object that contains all elements of UI
		PiccrossView view = new PiccrossView(model, debugMode);
		
		ControllableTimer timer = new ControllableTimer(view);
				
		PiccrossController controller = PiccrossController.instantiate();
				
		PiccrossClient networkClient = PiccrossClient.instantiate(view, model, false, timer);
				
		view.getClientReference(networkClient);
				
		controller.configObjects(view, model, timer, networkClient);
				
		// run splash screen
		loadScreen.showSplash();
		

		/**
		 * Method to control the event queue of program by appending runnable objects into the queue.
		 * instantiates an anonymous inner class to override the interface runnable to pass to EventQueue.
		 */
		EventQueue.invokeLater(new Runnable(){

			/**
			 * method to instantiate the main JFrame thread of application and pass to EventQueue method
			 */
			@Override
			public void run() {

				//Instantiate main frame of program that all other components will be painted on
				JFrame frame = new JFrame();
				
				// pass frame reference to view for PiccrossNetworkModalVC
				view.passFrame(frame);
				
				//instantiate display icon to match Piccross game
				ImageIcon frameIcon = new ImageIcon("PiccrossNameMin.jpg");

				//set properties of Main frame
				frame.setTitle("Piccross Client");
				frame.setIconImage(frameIcon.getImage());
				
				//Static frame size is designed for 1920x1080 at 100% scale. 
				frame.setMinimumSize(new Dimension(1000,1000));

				//pass View constructor to Content Pane to load UI components
				frame.setContentPane(view);
				
				// set the menu bars
				frame.setJMenuBar(view.addMenu());

				// Lock frame size and display in center of screen
				frame.setResizable(false);
				frame.setLocationRelativeTo(null);
				
				/**
				 * Inner class to listen for  the closing of the main frame window and shut down gracefully
				 * Overrides the WindowClosing method of WindowAdapter interface to close program
				 */
				WindowListener wl = new WindowAdapter()
				{
					@Override 
					public void windowClosing(WindowEvent e)
					{
						System.exit(0);
					}
				};
				
				//add window listener to frame
				frame.addWindowListener(wl);

				// Display Frame only once everything has loaded
				frame.setVisible(true);

			}//end run inner class
		});//end event Queue outer Method
		
		// initiate the timer so it is running. Potentially get this on its own thread
		timer.run();	

	}//end main

}//end class