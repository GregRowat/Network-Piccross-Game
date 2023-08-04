import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * JAP - CS Academic Level 4
 * Backend logic for the piccross model
 * Course: CST8221-302 - Java Application Programming
 * @author Professor: Daniel Cormier
 * @author Aiden Mackean: 040707105
 * @author Greg Rowat: 041001565
 */
public class PiccrossView extends JPanel{

	/** default serial version ID - Swing components implement the Serializable interface */
	private static final long serialVersionUID = 1L;
	/** option for running debug options*/
	private boolean debugMode = false;
	/** Constant board size for a 5x5 play area */
	private int boardSize = 5;

	/** arrays of panels for the hints.  = horizontal hints Y = Vertical hints */
	private JPanel[] Yhints = new JPanel[boardSize];
	private JPanel[] Xhints = new JPanel[boardSize];

	/** 2d array of game pieces */
	private JButton[][] gameSquares = new JButton[boardSize][boardSize];

	/** define global colors for use in Controller actions */ 
	private Color themeColorBlue = new Color(230,236,255);
	private Color gamePieceColor = new Color(192,192,192);

	/** Font setting for all labels and buttons */
	private Font textFont = new Font("SansSerif", Font.BOLD, 20);
	private Font hintFont = new Font("SansSerif", Font.BOLD, 12);

	/** main game panel */ 
	private JPanel gameBoard;

	/** Chat window panel */ 
	private JPanel chatWindow;

	/** output text area */ 
	private JTextArea outputBox;
	/** chat output text area */
	private JTextArea chatBox;
	/** chat input text field */
	private JTextField chatInput;

	/** reference to current frame. Required to get the NetworkPanel constructor provided functioning. */
	private JFrame frameReference;

	/***************************************************************************************
	 * 	FOLLOWING FIELDS MOVED HERE FROM CONSTRUCTOR TO FACILITATE VIEW HELPER METHOD ACCESS
	 **************************************************************************************/

	/** interrupt dialogue box for rules selection */
	private JButton rules;
	/** reset button */
	private JButton reset;
	/** mark button */
	private JCheckBox mark;
	/** the text of the timer object */
	private JTextField timer;
	/** text of the score object */
	private JTextField score;

	/** model object reference. Required for hint generation */
	private PiccrossModel model;

	/** color tile for correct selection */
	private Color correctColor = new Color(179, 198,255);

	/*********************************************************
	 * MENU OBJECTS INSTANTIATED HERE FOR CLASS WIDE ACCESS
	 *********************************************************/

	/** menu bar object */
	private JMenuBar bar = new JMenuBar();
	/** game tab option */
	private JMenu gameMenu = new JMenu("Game");
	/** new game option */
	private JMenuItem newMenu = new JMenuItem("New", new ImageIcon("piciconnew.gif"));
	/** debug tab */
	private JMenu debug = new JMenu("Debug");
	/** selection to fill only edges */
	private JMenuItem fillEdges = new JMenuItem("Fill Edges");
	/** selection to fill entire grid */
	private JMenuItem fullGrid = new JMenuItem("Fill Grid");
	/** selection to fill only the exact middle */
	private JMenuItem fillCenter = new JMenuItem("Fill Center");
	/** empty tab submenu */
	private JMenu empty = new JMenu("Empty");
	/** empty with immediate win */
	private JMenuItem emptyWin = new JMenuItem("Immediate Win");
	/** empty with no win */
	private JMenuItem noWin = new JMenuItem("No Solution");
	/** exit */
	private JMenuItem exit = new JMenuItem("Exit", new ImageIcon("piciconext.gif"));
	/** help menu option */	
	private JMenu helpMenu = new JMenu("Help");
	/** immediately see the solution, board still playable */
	private JMenuItem solution = new JMenuItem("Solution", new ImageIcon("piciconsol.gif"));
	/** about information */
	private JMenuItem about = new JMenuItem("About", new ImageIcon("piciconabt.gif"));
	/** network menu tab */
	private JMenu network = new JMenu("Network");
	/** new connection option */
	private JMenuItem connect = new JMenuItem("New Connection");
	/** disconnect option */
	private JMenuItem disconnect = new JMenuItem("Disconnect");


	/** ref to the controller for network client upload functionality */
	private PiccrossClient client;



	/**
	 * Constructor to instantiate and set properties of all elements of the UI to be passed to the content pane
	 * @param model - reference to model of game
	 * @param debugMode - flag for programmer debug engagement 
	 */
	public PiccrossView(PiccrossModel model, boolean debugMode) {

		this.debugMode = debugMode;
		// store model object reference
		this.model = model;

		/*******************************************************************************************************************
		 * MAIN PANELS SET
		 *******************************************************************************************************************/

		//set parent content panel specifications
		setBorder(new EmptyBorder(20, 10, 10, 10));
		setLayout(new BorderLayout(20, 20));

		//create main panels 
		JPanel menuInterface = new JPanel();
		JPanel display = new JPanel();
		gameBoard = new JPanel(new GridLayout(0, (boardSize + 1)));

		//set main panel layouts
		menuInterface.setLayout(new BoxLayout(menuInterface, BoxLayout.Y_AXIS));
		display.setLayout(new BorderLayout());

		/********************************************************************************************************************
		 * MENU INTERFACE set & Load 
		 ********************************************************************************************************************/

		//Button box to house functional buttons and logo
		JPanel menuButtonBox = new JPanel();
		menuButtonBox.setLayout(new BoxLayout(menuButtonBox, BoxLayout.Y_AXIS));
		menuButtonBox.setBackground(Color.BLACK);
		menuButtonBox.setBorder(BorderFactory.createLineBorder(Color.BLUE, 5, true));

		//create and set all features of menu components

		//create logo banner
		JLabel logo = new JLabel(new ImageIcon("piccrossLogo.png"));
		logo.setAlignmentX(CENTER_ALIGNMENT);
		logo.setBorder(BorderFactory.createMatteBorder(5,5,5,5,Color.BLUE));

		//create rules button
		rules = new JButton();
		JLabel rulesLabel = new JLabel("Rules");
		rulesLabel.setFont(textFont);
		rules.add(rulesLabel);
		rules.setAlignmentX(CENTER_ALIGNMENT);
		rules.setBackground(themeColorBlue);
		rules.setContentAreaFilled(true);
		rules.setOpaque(true);
		rules.setActionCommand("rules");

		//create mark box to house check box and label for mark button
		JPanel markBox = new JPanel();
		markBox.setBorder(BorderFactory.createMatteBorder(0, 28, 0, 30, Color.BLACK));
		markBox.setBackground(themeColorBlue);

		//create the check box to be housed in markBox panel
		mark = new JCheckBox();
		mark.setBackground(Color.WHITE);
		mark.setForeground(Color.WHITE);

		//create the check box label to be housed in markBox panel
		JLabel markLabel = new JLabel("Mark");
		markLabel.setFont(textFont);

		//set properties and controller of check box
		mark.setAlignmentX(CENTER_ALIGNMENT);		
		mark.setBackground(themeColorBlue);
		mark.setActionCommand("Mark");

		//add check box and label to parent container
		markBox.add(mark, BorderLayout.WEST);
		markBox.add(markLabel, (BorderLayout.EAST));

		//create reset button
		reset = new JButton();
		JLabel resetLabel = new JLabel("Reset");
		resetLabel.setFont(textFont);
		reset.add(resetLabel);
		reset.setAlignmentX(CENTER_ALIGNMENT);
		reset.setBackground(themeColorBlue);
		reset.setActionCommand("Reset");

		//create timer display Box
		timer = new JTextField();
		timer.setAlignmentX(CENTER_ALIGNMENT);
		timer.setHorizontalAlignment(JTextField.CENTER);
		timer.setBorder(BorderFactory.createMatteBorder(1, 5, 5, 5, Color.GRAY));
		timer.setFont(textFont);
		timer.setEditable(false);

		//create label banner for Timer display Box
		JLabel timerName = new JLabel("TIMER");
		timerName.setFont(textFont);
		timerName.setBackground(Color.GRAY);
		timerName.setBorder(BorderFactory.createMatteBorder(0, 46, 0, 46, Color.GRAY));
		timerName.setOpaque(true);
		timerName.setAlignmentX(CENTER_ALIGNMENT);

		//create score display box
		score = new JTextField("0");
		score.setAlignmentX(CENTER_ALIGNMENT);
		score.setHorizontalAlignment(JTextField.CENTER);
		score.setBorder(BorderFactory.createMatteBorder(1, 5, 5, 5, Color.GRAY));
		score.setFont(textFont);
		score.setEditable(false);

		//create label banner for score display Box
		JLabel scoreName = new JLabel("SCORE");
		scoreName.setFont(textFont);
		scoreName.setBackground(Color.GRAY);
		scoreName.setBorder(BorderFactory.createMatteBorder(0, 42, 0, 42, Color.GRAY));
		scoreName.setOpaque(true);
		scoreName.setAlignmentX(CENTER_ALIGNMENT);

		/***************************************************************************************************************
		 * INVISIBLE OBJECTS FOR SPACING
		 ***************************************************************************************************************/
		JButton space1 = new JButton();
		space1.setOpaque(false);
		space1.setContentAreaFilled(false);
		space1.setBorderPainted(false);

		JButton space2 = new JButton();
		space2.setOpaque(false);
		space2.setContentAreaFilled(false);
		space2.setBorderPainted(false);

		JButton space3 = new JButton();
		space3.setOpaque(false);
		space3.setContentAreaFilled(false);
		space3.setBorderPainted(false);

		JButton space4 = new JButton();
		space4.setOpaque(false);
		space4.setContentAreaFilled(false);
		space4.setBorderPainted(false);

		JButton space5 = new JButton();
		space5.setOpaque(false);
		space5.setContentAreaFilled(false);
		space5.setBorderPainted(false);

		JButton space6 = new JButton();
		space6.setOpaque(false);
		space6.setContentAreaFilled(false);
		space6.setBorderPainted(false);

		JButton space7 = new JButton();
		space7.setOpaque(false);
		space7.setContentAreaFilled(false);
		space7.setBorderPainted(false);

		JButton space8 = new JButton();
		space8.setOpaque(false);
		space8.setContentAreaFilled(false);
		space8.setBorderPainted(false);

		JTextArea bigSpc1 = new JTextArea();
		bigSpc1.setForeground(new Color(0, 0, 0, 0));
		bigSpc1.setOpaque(false);
		bigSpc1.setEditable(false);

		JTextArea bigSpc2 = new JTextArea();
		bigSpc2.setForeground(new Color(0, 0, 0, 0));
		bigSpc2.setOpaque(false);
		bigSpc2.setEditable(false);

		JTextArea bigSpc3 = new JTextArea();
		bigSpc3.setForeground(new Color(0, 0, 0, 0));
		bigSpc3.setOpaque(false);
		bigSpc3.setEditable(false);

		JTextArea bigSpc4 = new JTextArea();
		bigSpc4.setForeground(new Color(0, 0, 0, 0));
		bigSpc4.setOpaque(false);
		bigSpc4.setEditable(false);

		JTextArea bigSpc5 = new JTextArea();
		bigSpc5.setForeground(new Color(0, 0, 0, 0));
		bigSpc5.setOpaque(false);
		bigSpc5.setEditable(false);

		// end of invisible objects

		//load Menu Box for functional Buttons
		menuButtonBox.add(space1);
		menuButtonBox.add(space2);
		menuButtonBox.add(logo);
		menuButtonBox.add(bigSpc5);
		menuButtonBox.add(rules);
		menuButtonBox.add(space3); 
		menuButtonBox.add(space7);
		menuButtonBox.add(markBox);
		menuButtonBox.add(space4);
		menuButtonBox.add(space8);
		menuButtonBox.add(reset);
		menuButtonBox.add(bigSpc4);

		//Load Menu Interface with components
		menuInterface.add(menuButtonBox);
		menuInterface.add(bigSpc1);
		menuInterface.add(timerName);
		menuInterface.add(timer);		
		menuInterface.add(bigSpc2);
		menuInterface.add(scoreName);
		menuInterface.add(score);	
		menuInterface.add(bigSpc3);

		// end of adding menu components

		/********************************************************************************************************************
		 * DISPLAY INTERFACE SET & LOAD
		 ********************************************************************************************************************/

		//create border panel to place labels
		JPanel displayLabels = new JPanel(new BorderLayout());

		//create a panel to contain Chat Input/Output areas
		chatWindow = new JPanel(new BorderLayout());
		chatWindow.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.GRAY));

		//create labels for border Label panel
		JLabel outputLabel = new JLabel("Output");
		outputLabel.setFont(textFont);
		JLabel chatLabel = new JLabel("Chat");
		chatLabel.setFont(textFont);

		//align labels to be over their corresponding text box
		chatLabel.setHorizontalAlignment(SwingConstants.CENTER);
		outputLabel.setHorizontalAlignment(SwingConstants.LEFT);

		//create output text box that wraps text within bounds
		outputBox = new JTextArea("", 7, 40);
		outputBox.setLineWrap(true);
		outputBox.setEditable(false);
		outputBox.setBorder(BorderFactory.createMatteBorder(3, 3, 3, 3, Color.GRAY));

		//create chat output text box that wraps text within bounds
		chatBox = new JTextArea("", 12, 40);
		chatBox.setLineWrap(true);
		chatBox.setEditable(false);

		//create chat input field
		chatInput = new JTextField();
		chatInput.setActionCommand("send");
		chatInput.setEditable(true);
		chatInput.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.GRAY));

		//add labels to displayBox Layout
		displayLabels.add(outputLabel, BorderLayout.WEST);
		displayLabels.add(chatLabel, BorderLayout.CENTER);

		//add input and output text elements to the chatWindow Panel
		chatWindow.add(new JScrollPane(chatBox), BorderLayout.CENTER);
		chatWindow.add(chatInput, BorderLayout.SOUTH);

		//add elements to display panel
		display.add(displayLabels, BorderLayout.NORTH);
		//add scroll bar capability for outputs
		display.add(new JScrollPane(outputBox), BorderLayout.WEST);
		display.add(new JScrollPane(chatWindow), BorderLayout.EAST);

		/******************************************************************************************************************
		 * GAME BOARD SET (HINTS AND BORDERS)
		 ******************************************************************************************************************/

		//create all Horizontal hints as panels and add labels to them for hints
		for (int i = 0; i < boardSize; i++) {

			//if last panel being created add the right border
			if(i == boardSize-1) {

				//create new hint panel with theme color & top/left/right border
				JPanel newXHint;
				newXHint = new JPanel();
				newXHint.setBackground(themeColorBlue);
				newXHint.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 2, Color.BLACK));

				//create new hint label for panel
				JLabel text = new JLabel();
				//set text as html tag in order to leverage it line breaks
				//text.setText(model.getXhint(i));
				text.setText(this.formatHint(model.getXhint(i), 'x'));
				text.setFont(hintFont);

				//add label to newly created panel
				newXHint.add(text);

				//save panel to array for adding to gameBoard
				Xhints[i] = newXHint;
			}

			//create all other hint panels with top & left border only
			else {

				//create new hint panel with theme color and top/left border only
				JPanel newXHint;
				newXHint = new JPanel();
				newXHint.setBackground(themeColorBlue);
				newXHint.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 0, Color.BLACK));

				//create new hint label for panel
				JLabel text = new JLabel();
				// html allows for newline chars
				text.setText(this.formatHint(model.getXhint(i), 'x'));
				text.setFont(hintFont);

				//add label to newly created panel
				newXHint.add(text);

				//save panel to array for adding to gameBoard
				Xhints[i] = newXHint;

				//god forgive me for the sins i've committed in this code 
			}

		}// end create horizontal hints

		// create the vertical hints as panels with hint labels
		for (int i = 0; i < boardSize; i++) {

			//for last hint panel create a bottom border
			if(i == (boardSize-1)) {

				//create new panel with theme color, and top/left/bottom border
				JPanel newYHint;
				newYHint = new JPanel();
				newYHint.setBackground(themeColorBlue);
				newYHint.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 0, Color.BLACK));

				// gotta make this a method
				JLabel text = new JLabel(this.formatHint(model.getYhint(i), 'y'));

				text.setFont(hintFont);

				//add label to new panel
				newYHint.add(text);

				//save panel to array for adding to gameBoard
				Yhints[i] = newYHint;
			}

			//otherwise create a hint panel with only a top border
			else {
				//create new panel with theme color, and top border
				JPanel newYHint;
				newYHint = new JPanel();
				newYHint.setBackground(themeColorBlue);
				newYHint.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 0, Color.BLACK));

				//create hint label for panel
				JLabel text = new JLabel(this.formatHint(model.getYhint(i), 'y'));
				text.setFont(hintFont);

				//add label to new panel
				newYHint.add(text);

				//save panel to array for adding to gameBoard
				Yhints[i] = newYHint;
			}

		}

		/******************************************************************************************************************
		 * GAME BOARD SET (GAME PIECES)
		 ******************************************************************************************************************/

		// Create the game square buttons
		for (int i = 0; i < gameSquares.length; i++) {
			for (int j = 0; j < gameSquares[i].length; j++) {
				JButton square = createButton( i, j);
				gameSquares[i][j] = square;
			}
		}

		//create a blank panel for the space between the hints. 
		JPanel blank;
		blank = new JPanel();
		blank.setBackground(themeColorBlue);
		//set top left corner border to match the rest of the pieces
		blank.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 0, Color.BLACK));
		// adds it first to be in top left
		gameBoard.add(blank);

		/******************************************************************************************************************
		 * GAME BOARD LOAD (HINTS & GAME PIECES)
		 ******************************************************************************************************************/

		// add the top level hints as first row. 
		for (int i = 0; i < boardSize; i++) {
			gameBoard.add(Xhints[i]);
		}

		// add the remaining rows, placing hints before game pieces.
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				switch (j) {

				//if first iteration of i, add hint instead of game piece
				case 0:
					gameBoard.add(Yhints[i]);
				default:
					gameBoard.add(gameSquares[i][j]);
				}
			}
		}

		/******************************************************************************************************************
		 * LOAD ALL PANELS INTO CONTENT PANE
		 ******************************************************************************************************************/

		add(menuInterface, BorderLayout.EAST);
		add(gameBoard, BorderLayout.CENTER);
		add(display, BorderLayout.SOUTH);

	}//end PiccrossViewCont Constructor

	/**
	 * Method used to create all buttons as pieces of the game board. Uses the array parameters to set the
	 * Coordinates and borders appropriately
	 * @param hn represents the Button Handler controller object
	 * @param i represents the rows of the game board
	 * @param j represents the columns of the game board
	 * @return newly instantiated button to be added to game board
	 */
	private JButton createButton(int i, int j) {

		//declare a new button
		JButton newButton;

		//set the row and column coordinates to match the array of gamePieces
		int row = j;
		int column = i;

		//instantiate new button and set properties
		newButton = new JButton();
		newButton.setActionCommand("Button: " + (i+1) + " " + (j+1));
		newButton.setBackground(gamePieceColor);
		newButton.setOpaque(true);

		//if grid piece is 5,5 set the border for bottom right corner of the game board
		if(column == (boardSize-1) && row == (boardSize-1)) {
			newButton.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
			return newButton;
		}

		//if game piece is the last of a column set the bottom border of the game board
		if(column == (boardSize-1)) {
			newButton.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 0, Color.BLACK));
			return newButton;
		}

		//if game piece is the last of a row set the right border of the game board
		if(row == (boardSize-1)) {			
			newButton.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 2, Color.BLACK));
			return newButton;			
		}

		//all other game pieces set with the default borders to fill the game board
		newButton.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 0, Color.BLACK));

		return newButton;

	}//end create button method

	/**
	 * Method to add listener object to specific view objects
	 * @param listenForPiccross actionlistener to be attached to specified ui component
	 */
	public void addPiccrossListener(ActionListener listenForPiccross) {

		// attaches listener passed in to the specified objects
		rules.addActionListener(listenForPiccross);
		reset.addActionListener(listenForPiccross);
		chatInput.addActionListener(listenForPiccross);
		mark.addActionListener(listenForPiccross);

		// add listener to menu objects
		newMenu.addActionListener(listenForPiccross);
		debug.addActionListener(listenForPiccross);
		exit.addActionListener(listenForPiccross);
		solution.addActionListener(listenForPiccross);
		about.addActionListener(listenForPiccross);
		fillEdges.addActionListener(listenForPiccross);
		fullGrid.addActionListener(listenForPiccross);
		fillCenter.addActionListener(listenForPiccross);

		noWin.addActionListener(listenForPiccross);
		emptyWin.addActionListener(listenForPiccross);

		// assignment 4 network connection listeners
		connect.addActionListener(listenForPiccross);
		disconnect.addActionListener(listenForPiccross);

		// iterate through the array of grid buttons, add listener to each on
		for (int i = 0; i < gameSquares.length; i++) {
			for (int j = 0; j < gameSquares[i].length; j++) {
				gameSquares[i][j].addActionListener(listenForPiccross);
			}
		}
	}

	/**
	 * method to output string to output box
	 * @param text to be output to ui
	 */
	public synchronized void outputText(String text) {

		outputBox.append(text + "\n");
	}

	/**
	 * method to update the chat window with server messages
	 * @param message - chat message from server
	 */
	public synchronized void outputChat(String message) {

		chatBox.append(message + "\n");
	}

	/**
	 * Method checks if specified grid item has already been played, 
	 * denying user interaction if so
	 * @param i row of gridLayout
	 * @param j column of gridLayout
	 * @return boolean check of button activity status
	 */
	public boolean getButtonStatus(int i, int j) {

		// checks that the text is null for non played object. 
		if (gameSquares[i-1][j-1].getBackground() != gamePieceColor) return false;
		return true;

	}

	/**
	 * Method to set button activity status
	 * @param i row coordinate
	 * @param j column coordinate
	 * @param mark button selected or not
	 * @param correct selection correct game piece or not
	 */
	public void setButtonStatus(int i, int j, boolean mark, boolean correct) {

		// main logic of method: sets the buttons on click view as well as the timer
		// Four potential states:
		//		1) Correct active piece = blue square
		//		2) Incorrect mark active piece = blue square with 'X'
		//		3) Correct mark inactive piece = gray square
		//		4) Incorrect select empty piece = gray square with 'x'


		// set correct or incorrect based on boolean value from backend model 
		if (correct) {

			// check if mark value or active piece selection
			if (mark) {

				// this could be removed depending on preference
				gameSquares[i-1][j-1].setBackground(Color.GRAY);
			}

			// active game piece
			else {
				gameSquares[i-1][j-1].setBackground(correctColor);
			}

			// create temp object to hold current score
			int temp = Integer.parseInt(score.getText());
			// increment temp
			temp++;
			// output temp as new score
			score.setText(Integer.toString(temp));


		}

		// evaluates incorrect selection vs mark conditional
		else {

			// correct selection as mark was checked
			if(mark) {
				gameSquares[i-1][j-1].setIcon(new ImageIcon("xmark.png"));
				gameSquares[i-1][j-1].setBackground(correctColor);
			}

			else {
				// set the square to represent error
				gameSquares[i-1][j-1].setIcon(new ImageIcon("xmark.png"));
				gameSquares[i-1][j-1].setBackground(Color.GRAY);

				// create temp object to hold current score
				int temp = Integer.parseInt(score.getText());



				// check if 0, can't do anything if so
				if(temp!=0) temp--;



				// output temp as new score
				score.setText(Integer.toString(temp));	
			}	
		}	
	}//end set button status

	/** 
	 * Method to output rules interrupt window to user
	 */
	public void rulesWindow() {

		JFrame rulesFrame = new JFrame();

		//fill pop - up window with default message for now
		JOptionPane.showMessageDialog(rulesFrame, "Welcome to Piccross!"
				+ "\n\nIn Piccross the User's goal is to determine which grid squares are filled "
				+ "\nbased on the hints provided."
				+ "\n\nHints correspond to either the row they are beside or the column they are above,"
				+ "\nrepresenting a sequential number of correct squares"
				+ "\n\nGrouped hints (For example, 1 3) represent active squares with at least one"
				+ "\nspace seperating the contiguous active groupings"
				+ "\n\nThe User can use the \'Mark\' button to select spaces they believe to be empy,"
				+ "\nhowever selecting an active space will constitute an incorrect move", "Piccross Rules", JOptionPane.PLAIN_MESSAGE);


	}

	/**
	 * Method to update timer dynamically
	 * @param i current timer.. time...
	 */
	public void setTime(int i) {
		// output i to text field in timer
		String output = Integer.toString(i);
		timer.setText(output);

	}

	/**
	 * Method to reset the view upon ui component selection. 
	 */
	public void resetView() {

		// iterate over component array for xHints
		for (int i = 0; i < Xhints.length; i++) {

			// clears the hints
			Xhints[i].removeAll();
			Xhints[i].updateUI();

			JLabel text = new JLabel(this.formatHint(model.getXhint(i), 'x'));
			text.setFont(hintFont);

			Xhints[i].add(text);

		}

		// iterate over component array for yHints. Could
		// probably make one loop do all hints but redundancy saves
		// future changes of differing dimension. Also java so what's the point
		// in efficieny
		for (int i = 0; i < Yhints.length; i++) {

			// clears y hints
			Yhints[i].removeAll();
			Yhints[i].updateUI();


			JLabel text = new JLabel(this.formatHint(model.getYhint(i), 'y'));
			text.setFont(hintFont);
			// update the hint bars
			Yhints[i].add(text);


		}

		// Cleans the board
		// Iterate over all the squares not the most algorithmically efficient but works
		for (int i = 0; i < gameSquares.length; i++) {

			for (int j = 0; j < gameSquares[i].length; j++) {

				// check if square is not empty. Reset if so
				if (gameSquares[i][j].getBackground() != gamePieceColor || gameSquares[i][j].getIcon() != null) {

					gameSquares[i][j].setText("");
					gameSquares[i][j].setBackground(gamePieceColor);
					gameSquares[i][j].setIcon(null);
				}
			}
		}

		// check for if resetting from game over where buttons are disabled
		rules.setEnabled(true);
		mark.setEnabled(true);

		// set mark to disabled. Front end only, ensure model is also updated
		mark.setSelected(false);

		// reset the score
		score.setText("0");

		// instantly set and reload the timer
		timer.setText("0");
		timer.updateUI();

	}

	/**
	 * method to disable ui components when game over is reached
	 */
	public void gameOver() {

		// update the final score variable
		score.setText(Integer.toString(model.getFinalScore()));

		// disables buttons. Gets reset in reset method
		rules.setEnabled(false);
		mark.setEnabled(false);

		// condition for perfect game
		if(model.isPerfect()) {

			// create component for output formatting
			JPanel box = new JPanel();
			box.setLayout(new BorderLayout());

			// create child objects for window componenet
			JLabel pic = new JLabel(new ImageIcon("gamepicwinner.png"));
			JLabel text = new JLabel("\n\nFinal Score: " + Integer.toString(model.getFinalScore()));
			JLabel selection = new JLabel("Would you like to upload your score?");

			// format the code output
			text.setHorizontalAlignment(SwingConstants.CENTER);
			text.setVerticalAlignment(SwingConstants.CENTER);
			selection.setHorizontalAlignment(SwingConstants.CENTER);
			selection.setVerticalAlignment(SwingConstants.CENTER);
			text.setFont(textFont);

			// add components to the window
			box.add(pic, BorderLayout.NORTH);
			box.add(text, BorderLayout.CENTER);
			box.add(selection, BorderLayout.SOUTH);

			// create dialogure interrupt, store choice to int variable
			int choice = JOptionPane.showConfirmDialog(null, box, "Winner!", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

			// selection to upload
			if (choice == JOptionPane.YES_OPTION) {

				// check the connection button status
				if (!connect.isEnabled()) {


					String board = model.getBoard();					
					// send data to client class
					client.uploadGameBoard(board);

				}

				// conditional for no connection existing.
				else {

					JFrame noConnection = new JFrame();

					//fill pop - up window with default message for now
					JOptionPane.showMessageDialog(noConnection, "No connection exits", "No connection exists", JOptionPane.PLAIN_MESSAGE);	

				}

			}

		} 

		else {

			// create component for output formatting
			JPanel box = new JPanel();
			box.setLayout(new BorderLayout());

			// create child objects for window componenet
			JLabel pic = new JLabel(new ImageIcon("gamepicend.png"));
			JLabel text = new JLabel("Final Score: " + Integer.toString(model.getFinalScore()));
			JLabel selection = new JLabel("Would you like to upload your score?");

			// format the code output
			text.setHorizontalAlignment(SwingConstants.CENTER);
			text.setVerticalAlignment(SwingConstants.CENTER);
			selection.setHorizontalAlignment(SwingConstants.CENTER);
			selection.setVerticalAlignment(SwingConstants.CENTER);
			text.setFont(textFont);

			// add components to the window
			box.add(pic, BorderLayout.NORTH);
			box.add(text, BorderLayout.CENTER);
			box.add(selection, BorderLayout.SOUTH);

			// create dialogure interrupt, store choice to int variable
			int choice = JOptionPane.showConfirmDialog(null, box, "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (choice == JOptionPane.YES_OPTION) {

				// check the connection button status
				if (!connect.isEnabled()) {


					String board = model.getBoard();					
					// send data to client class
					client.uploadGameBoard(board);

				}

				// conditional for no connection existing.
				else {

					JFrame noConnection = new JFrame();

					//fill pop - up window with default message for now
					JOptionPane.showMessageDialog(noConnection, "No connection exits", "No connection exists", JOptionPane.PLAIN_MESSAGE);	

				}

			}

		}

	}

	/** 
	 * Method to display menu information option to user
	 */
	public void about() {
		JOptionPane.showMessageDialog(null, "Piccross\n\n"
				+ "Aiden Mackean: 040707105"
				+ "\nGreg Rowat: 041001565\n\n"
				+ "Winter Term 2022", "Information", JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Main worker for creating and filling the functionality of the menu bar
	 * @return Menu object filled and interactive. Called on frame object in main. 
	 */
	public JMenuBar addMenu() {

		bar = new JMenuBar();

		newMenu.setActionCommand("New");
		// needs accelerator =ctrl_n
		newMenu.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, ActionEvent.CTRL_MASK));


		exit.setActionCommand("Exit");
		solution.setActionCommand("Solution");
		about.setActionCommand("About");

		fillEdges.setActionCommand("Edges");
		fullGrid.setActionCommand("Full");
		fillCenter.setActionCommand("Center");

		noWin.setActionCommand("noWin");
		emptyWin.setActionCommand("Win");

		connect.setActionCommand("Connect");
		disconnect.setActionCommand("Disconnect");

		network.add(connect);
		network.add(disconnect);

		// flag condition will need to be checked for disconnect / connect state
		disconnect.setEnabled(false);

		empty.add(noWin);
		empty.add(emptyWin);

		debug.add(fillEdges);
		debug.add(fullGrid);
		debug.add(fillCenter);
		debug.add(empty);

		gameMenu.add(newMenu);

		if(debugMode) {
			//gameMenu.add(debug);
		}

		gameMenu.addSeparator();
		gameMenu.add(exit);

		helpMenu.add(solution);
		helpMenu.add(about);

		bar.add(gameMenu);
		bar.add(network);
		bar.add(helpMenu);

		return bar;

	}


	/**
	 * Method to display solution from menu selection
	 */
	public void getSolution() {

		for (int i = 0; i < gameSquares.length; i++) {
			for (int j = 0; j < gameSquares[i].length; j++) {

				// just checks the binary val, sets pic if = 1
				if(model.getValue(i,j)==1) {
					gameSquares[i][j].setIcon(new ImageIcon("omark.png"));
				}

			}
		}

	}

	/**
	 * method to format the correctly formatted string for hint row / column
	 * @param s - returned string from model, hint to be formatted
	 * @param i - char representing selection determination of row format or column format
	 * @return formatted string
	 */

	public String formatHint(String s, char i) {

		// x represents formatting for rows, y is columns
		// takes the returned string from model containing the hints, gets the length
		// of said string and formats the output for the board items
		if (i == 'x' ) {

			switch(s.length()) {

			case 1:

				s = "<html><br/><br/><br/>" + s.charAt(0) 
				+ "</html>";

				break;
			case 2:

				s = "<html><br/>" + s.charAt(0)
				+ "<br/><br/><br/><br/>" + s.charAt(1) 
				+ "<br/></html>";
				break;


			case 3:
				s = "<html><br/>" + s.charAt(0)
				+ "<br/><br/>" + s.charAt(1) 
				+ "<br/><br/>" + s.charAt(2) 
				+ "<br/></html>";
				break;


			}//end switch

		} else {

			// formatted output depending on the length of the result
			switch(s.length()) {

			case 1:

				s = "<html><br/><br/><br/>" + s.charAt(0) 
				+ "</html>";

				break;
			case 2:

				s = "<html><br/><br/><br/>" + s.charAt(0) + 
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + s.charAt(1)
				+ "</html>";
				break;

				// this one is redundant, it will always be 1 / 1 / 1 but if we ever expand our board this would be the logic
			case 3:
				s = "<html><br/><br/><br/>" + s.charAt(0) + 
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + s.charAt(1) +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + s.charAt(2)
				+ "</html>";
				break;
			}//end switch	
		}
		return s;	
	}

	/**
	 *  Helper method to disable / enable network menu items
	 *  depending on the current state
	 */
	public void setConnection() {

		// turn connect item off
		if (connect.isEnabled() == true) {

			connect.setEnabled(false);
			disconnect.setEnabled(true);

		}

		// turn connect item on
		else {

			connect.setEnabled(true);
			disconnect.setEnabled(false);

		}		
	}

	/**
	 * helper method to instantiate provided network dialogue pop up
	 * @return parent frame to be interrupted
	 */
	public JFrame sendFrameReference() {

		return frameReference;

	}

	/**
	 * method to pass the frame reference into the view for use in NetworkModal
	 * @param frame - frame object reference
	 */
	public void passFrame(JFrame frame) {

		frameReference = frame;

	}

	/**
	 * method to get the client reference
	 * @param networkClient - reference to the client class object
	 */
	public void getClientReference(PiccrossClient networkClient) {

		client = networkClient;

	}

	/**
	 * method to get the final time when game is completed
	 * @return finalTime - users time at completion of game
	 */
	public String getTime() 
	{

		String finalTime = timer.getText();

		return finalTime;
	}


}//end Piccross view class