import java.util.Random;

/**
 * JAP - CS Academic Level 4
 * Backend logic for the piccross model
 * Course: CST8221-302 - Java Application Programming
 * @author Professor: Daniel Cormier
 * @author Aiden Mackean: 040707105
 * @author Greg Rowat: 041001565
 */
public class PiccrossModel {

	/** boolean for mark check status */
	private boolean mark = false;

	/** if we were to add difficulty and board scaling, this would be in a switch statement check */
	private final int BOARD_SIZE = 5;

	/** squares are correct / false using binary logic stored in this array. 
	 * checked against user input
	 */
	private int[][] solution = new int[BOARD_SIZE][BOARD_SIZE];

	/** storage container for xhint array based on inversion of solution array */
	private int[][] xHints = new int[BOARD_SIZE][BOARD_SIZE];

	/** boolean check for game over status */
	private boolean gameOver = false;

	/** tracks the total number of correct game squares picked by player */
	private int correctChoiceTiles = 0;

	/** max total of squares to reach game over. Control for correctChoiceTiles */
	private int totalChoiceTiles = 0;

	/** control boolean for perfect game */
	private boolean perfect = true;

	/** count of incorrect choices user may make */
	private int incorrectChoices = 0;

	/** check method for game over status 
	 * @return - boolean control for game over
	 * */
	public boolean isGameOver() {
		return gameOver;
	}

	/**
	 * Method to return the binary value of 2d array choice
	 * @param i game row
	 * @param j gome column
	 * @return binary value representing active or inactive
	 */
	public int getValue(int i, int j) {

		return solution[i][j];

	}



	/**
	 * helper method called from main. More to organize code than anything
	 */
	public void run() {

		generateSolution();
		generateXhints();

	}

	/**
	 * Method to reset game board with specific debug instance applied
	 * Could probably just use the reset method and overload it but 
	 * this is kinda easier to logically keep track of for me. 
	 * @param s string action command representing debug instance. 
	 */
	public void debugModel(String s) {

		// reset correct tile count
		correctChoiceTiles = 0;
		// reset total correct tiles in board
		totalChoiceTiles = 0;
		// set mark equal to false
		mark = false;
		// reset game over boolean control
		gameOver = false;
		// reset the solution array with debug specification
		generateDebugSolution(s);
		// reset the hint array
		generateXhints();
		// reset the perfect control boolean
		perfect = true;

	}

	/**
	 * Method to instantiate solution array with specific debug case. 
	 * Called from debugModel()
	 * @param s specific debug scenario to test against
	 */
	public void generateDebugSolution(String s) {

		// this would be a great switch but calling methods as boolean doesn't seem to 
		// be working

		// condition for testing full grid solution
		if(s.equals("Full")) {

			for(int i = 0; i < solution.length; i++) {
				for (int x = 0; x < solution[i].length; x++) {

					// set every tile to 1 (active)
					solution[i][x]=1;
					totalChoiceTiles++;

				}
			} // end of loops
		} // end of full if 

		// selection to fill all the edges
		else if (s.equals("Edges")) {

			for(int i = 0; i < solution.length; i++) {
				for (int x = 0; x < solution[i].length; x++) {

					if(i == 0 || i == solution.length-1 || x == 0 || x == solution.length-1) {
						solution[i][x]=1;
						totalChoiceTiles++;
					} 

					else {
						solution[i][x]=0;
					}
				}
			} // end of loops
		} // end of it

		// selection to fill the center tile
		else if (s.equals("Center")) {

			for(int i = 0; i < solution.length; i++) {
				for (int x = 0; x < solution[i].length; x++) {

					// currently hardcoded. Would potentially develop an algorithm
					// if sizing was dynamic (could also be a switch)
					if(x == 2 && i == 2) {
						solution[i][x]=1;
						totalChoiceTiles++;
					} 

					else {
						solution[i][x]=0;
					}
				}
			} // end of loops

		}

		// generate unwinnable, empty game. 
		// useful edge case test for trying to break things
		// NOTE: Not possible to be empty in the game
		else if (s.equals("noWin")) {

			for(int i = 0; i < solution.length; i++) {
				for (int x = 0; x < solution[i].length; x++) {

					solution[i][x]=0;

				}
			} // end of loops

		}

		else if (s.equals("Win")) {

			for(int i = 0; i < solution.length; i++) {
				for (int x = 0; x < solution[i].length; x++) {

					solution[i][x]=0;

				}
			} // end of loops

			gameOver = true;
		}

		else {
			// this should never trigger but might act as boundary catch
		}

	}

	/**
	 * populate the solution[][] array. Called in run method and reset method 
	 */
	public void generateSolution() {

		for(int i = 0; i < solution.length; i++) {
			for (int x = 0; x < solution[i].length; x++) {

				// create random object
				Random rand = new Random();
				// sets exclusive upper limit of random val
				int number = rand.nextInt(2);
				solution[i][x] = number;

				// set max quant for game over condition
				if (solution[i][x]==1) {
					totalChoiceTiles++;
				}

			}
		}

		// this should check against 0 game pieces being active.
		// Makes so even though the chance is extraordinarily small, there will always 
		// be one tile at least generated in an active non debug game
		if (totalChoiceTiles == 0) {
			generateSolution();
		}

	}

	/**
	 * populates the xhint array. yHints doesn't need this, directly manipulates solution array
	 * called in run method and reset method
	 */
	public void generateXhints() {

		// populate hint array
		for (int i = 0; i < xHints.length; i++) {
			for(int x = 0; x < solution.length; x++) {
				xHints[i][x] = solution[x][i];
			}
		}
	}

	/**
	 * called in the view, gets the hints for the specified game column passed in as i 
	 * returns the hints as a formatted string for use in the view
	 * @param i specific column of ui targetted
	 * @return hint as formatted string to insert to ui
	 */
	public String getXhint(int i) {

		// main output variable
		String output = "";

		// temp storage for count iterations of sequential squares
		int count = 0;

		// iteration loop
		for (int x = 0; x < xHints[i].length; x++) {

			// if the square is a solution tile, increment count
			if (xHints[i][x]==1) {
				count++;

				// boundary check for end position of loop
				if (x == xHints[i].length-1) {
					output += Integer.toString(count);
				}
			}

			// output and reset count if non active tile exists
			else {
				// ensure count exists
				if(count != 0) {
					output += Integer.toString(count);
					// reset count variable 
					count = 0;
				}
			} // end of loop selection
		} // end of for loop

		// boundary check for emtpy string
		if (output == "") {
			output = "0";
		}

		return output;

	}

	/**
	 * called in the view, gets the hints for the specified game row passed in as i 
	 * returns the hints as a formatted string for use in the view
	 * @param i specific row of ui targetted
	 * @return hint as formatted string to insert to ui
	 */
	public String getYhint(int i) {

		// main output variable
		String output = "";

		// temp storage for count iterations of sequential squares
		int count = 0;

		// iteration loop
		for (int x = 0; x < solution[i].length; x++) {

			// if the square is a solution tile, increment count
			if (solution[i][x]==1) {
				count++;

				// boundary check for end position of loop
				if (x == solution[i].length-1) {
					output += Integer.toString(count);
				}
			}

			// output and reset count if non active tile exists
			else {
				// ensure count exists
				if(count != 0) {
					output += Integer.toString(count);
					// reset count variable 
					count = 0;
				}
			} // end of loop selection
		} // end of for loop

		// boundary check for emtpy string
		if (output == "") {
			output = "0";
		}

		return output;
	}

	/**
	 * method to check is user input is correct. Main worker driving game loop logic i and j are the grid location passed in
	 * @param i row location targetted
	 * @param j column location targetted
	 * @return if selection is correct or incorrect
	 */
	public boolean checkIfRight(int i, int j) {

		// check the binary value of specified index (after zero indexing it) against mark status
		if (solution[i-1][j-1]==1 && mark == false) {

			// increment correct count
			correctChoiceTiles++;

			// if the terminal condition of squares selected is met, set game over 
			if (correctChoiceTiles == totalChoiceTiles) gameOver = true;
			return true;
		}
		// check selection is right if 0 and mark set
		else if (mark == true && solution[i-1][j-1]==0) {

			return true;
		}

		// condition to increment terminal condition where user selects incorrect mark
		else if (mark == true && solution[i-1][j-1]==1) {

			// increment correct count
			correctChoiceTiles++;

			// update game over conditional
			perfect = false;

			// update incorrect tiles
			incorrectChoices++;

			// if the terminal condition of squares selected is met, set game over 
			if (correctChoiceTiles == totalChoiceTiles) gameOver = true;
			return false;

		}

		// update incorrect tiles
		incorrectChoices++;
		// update game over condition
		perfect = false; 
		return false;

	}

	/**
	 * method to dynamically update mark boolean based on controller / ui reaction 
	 */
	public void updateMark() {

		// toggles mark boolean 
		if (mark == false) mark = true;
		else mark = false;

	}

	/**
	 * Getter to check if game was perfect run
	 * @return perfect boolean checking if any mistakes were made
	 */
	public boolean isPerfect() {
		return perfect;
	}

	/**
	 * method to check if mark is set or not
	 * @return status of mark boolean
	 */
	public boolean getMark() {
		return mark;
	}

	/**
	 * reset the backend model of the game
	 */
	public void newModel() {

		// reset correct tile count
		correctChoiceTiles = 0;
		// reset total correct tiles in board
		totalChoiceTiles = 0;
		// reset the incorrect count
		incorrectChoices = 0;
		// set mark equal to false
		mark = false;
		// reset game over boolean control
		gameOver = false;
		// reset the solution array
		generateSolution();
		// reset the hint array
		generateXhints();
		// reset the perfect control boolean
		perfect = true;

	}

	/**
	 * resets the model back to original instantiated values
	 */
	public void resetModel() {


		// reset correct tile count
		correctChoiceTiles = 0;
		// reset the incorrect count
		incorrectChoices = 0;
		// set mark equal to false
		mark = false;
		// reset game over boolean control
		gameOver = false;
		// reset the perfect control boolean
		perfect = true;

	}

	/**
	 * Returns a standardized game score based on the number of mistakes user has made
	 * @return final user score
	 */
	public int getFinalScore() {
		return BOARD_SIZE*BOARD_SIZE - incorrectChoices;
	}

	/**
	 * method to encode the current game board for network transmission
	 * @return result - the encoded game board logic
	 */
	public String getBoard() {

		String result = "$$$GAME-";

		// loop over the arrays and add the strings
		for(int i = 0; i < solution.length; i++) {
			for(int x = 0; x < solution[i].length; x++) {

				if (solution[i][x] == 1) {
					result += "1";
				}
				else result += "0";
			}//end inner loop

			result += ",";		
		}//end outter loop

		return result;
	}

	/**
	 * Method to update the local game board with the logic retrieved from server
	 * @param incomingMSG - message received from server containing encoded gameboard
	 */
	public void updateFromServer(String incomingMSG) {

		// reset correct tile count
		correctChoiceTiles = 0;
		// reset the incorrect count
		incorrectChoices = 0;
		// reset total correct tiles in board
		totalChoiceTiles = 0;
		// set mark equal to false
		mark = false;
		// reset game over boolean control
		gameOver = false;

		// loops to set the solution array
		String row1 = incomingMSG.substring(8,13);
		String row2 = incomingMSG.substring(14,19);
		String row3 = incomingMSG.substring(20,25);
		String row4 = incomingMSG.substring(26,31);
		String row5 = incomingMSG.substring(32,37);

		System.out.println("");

		// main loop to set the solution array for importing the gameboard

		for (int i = 0; i < solution.length; i++) {

			for (int x = 0; x < solution.length; x++) {

				switch(i) {
				case 0:

					if(row1.charAt(x) == '1') {
						solution[i][x] = 1;
						totalChoiceTiles++;
					} else solution[i][x] = 0;

					break;
				case 1:

					if(row2.charAt(x) == '1') {
						solution[i][x] = 1;
						totalChoiceTiles++;
					} else solution[i][x] = 0;

					break;
				case 2:

					if(row3.charAt(x) == '1') {
						solution[i][x] = 1;
						totalChoiceTiles++;
					} else solution[i][x] = 0;

					break;

				case 3:

					if(row4.charAt(x) == '1') {
						solution[i][x] = 1;
						totalChoiceTiles++;
					} else solution[i][x] = 0;
					break;

				case 4:

					if(row5.charAt(x) == '1') {
						solution[i][x] = 1;
						totalChoiceTiles++;
					} else solution[i][x] = 0;

					break;
				}//end switch			
			}//end inner loop	
		}//end outer loop

		generateXhints();

		// reset the perfect control boolean
		perfect = true;

	}


}//end model class

