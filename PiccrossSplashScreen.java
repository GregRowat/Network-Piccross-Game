

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * JAP - CS Academic Level 4
 * Constructor and logic for splash screen
 * Course: CST8221-302 - Java Application Programming
 * @author Professor: Daniel Cormier
 * @author Aiden Mackean: 040707105
 * @author Greg Rowat: 041001565
 */
public class PiccrossSplashScreen extends JWindow{
	
	/** default serializable number */
	private static final long serialVersionUID = 1L;
	/** amount of time to display the splash screen for */ 
	private final int time;
	
	/**
	 * parameterized constructor for a splash screen window
	 * @param time represents the time to be displayed for
	 */
	public PiccrossSplashScreen(int time) {
		this.time = time;
	}
	
	/**
	 * Method to define and display a preDetermined Splash screen before the program is loaded
	 */
	public void showSplash() {
		
		//instantiate a JPanel to be the content pane of splash screen
		JPanel content = new JPanel(new BorderLayout());
		content.setBackground(Color.GRAY);
		
		// this code will change the cursor to busy
		Cursor busy = new Cursor(3);
		this.setCursor(busy);
		
		//set size properties to image size. set position based on the screen
		int w = 600 + 10;
		int h = 300 + 10;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - w)/2;
		int y = (screen.height - h)/2;
		setBounds(x,y,w,h);
		
		//instantiate labels for text and logo image
		JLabel label = new JLabel(new ImageIcon("piccross.png"));
		JLabel text = new JLabel("Greg Rowat 041001565 -- Aiden Mackean 040707105 \n Our Piccross Game!", JLabel.CENTER);
		
		//set properties and add to parent panel
		content.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		content.add(label, BorderLayout.CENTER);
		content.add(text, BorderLayout.SOUTH);
		content.setBorder(BorderFactory.createLineBorder(Color.black, 10));
		
		//set content pane of the frame
		setContentPane(content);
		
		//display only after everything is loaded
		setVisible(true);
		
		try {
			Thread.sleep(time);
		}
		
		catch (InterruptedException e){
			e.printStackTrace();
		}
		
		//dispose instance of class at completion
		dispose();
			
	}
	
	
}

