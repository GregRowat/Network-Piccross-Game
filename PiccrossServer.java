import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

/**
 * JAP - CS Academic Level 4
 * Client class for network connection protocol
 * Course: CST8221-302 - Java Application Programming
 * @author Professor: Daniel Cormier
 * @author Aiden Mackean: 040707105
 * @author Greg Rowat: 041001565
 */
public class PiccrossServer {

	/** port number to launch server socket can command line or default */
	private  int port;
	/** server socket reference */
	private ServerSocket serverSocket = null;
	/** thread safe data structure of client threads */
	private Vector<ClientHandler> connections = new Vector<ClientHandler>(0);
	/** high score table */
	private Vector<String> highScoreT = new Vector<String>(0);
	/** String representing the current game in storage.*/
	private String gameBoardCode = "";
	/** connection counter */
	private int conCount = 0;

	/**
	 * Constructor for facilitating a server socket, listens for all client sockets and creates a thread
	 * of client socket connection
	 * @param Port - port number to establish server socket on, can be provided by args or default value 
	 */
	public PiccrossServer(int Port) 
	{
		this.port = Port;
	}

	/**
	 * establish server socket on port number provided by PicrossServer reference
	 */
	public void launchServer()
	{
		try 
		{
			//establish socket on port for server
			serverSocket = new ServerSocket(port);
			System.out.println("\nListening on port: " +port);

			//listen for clients
			while(true) 
			{

				try 
				{
					//create client socket when server receives connection request
					Socket incoming = serverSocket.accept();
					//instantiate a client thread with client socket connection
					ClientHandler cHand = new ClientHandler(incoming,this);
					//add thread to vector of connections
					connections.add(cHand);
					Runnable r = cHand;
					Thread t = new Thread(r);
					t.start();

					System.out.println("Inbound connection #" + conCount);
					//count the number of connections
					conCount++;
				}

				catch(IOException e) 
				{
					System.out.println("\nERROR: failed to accept incoming connection");
				}

			}//end client to server connection loop
		}//end try new connection and thread

		catch(BindException e) {
			System.out.println("\nError: binding exception on port: " + port);
		}
		catch(IOException e) 
		{
			System.out.println("\nERROR: failed to instantiate socket at port: " + port);
			return;
		}
		
		finally{
			try {
				serverSocket.close();
			} catch (IOException e) {
				
				System.out.println("Error shutting down server");
				e.printStackTrace();
			}
			System.out.println("Server disconnected");
		}

	}//end server constructor

	/**
	 * method to decrement the connection counter so the server prints the appropriate
	 * number of incoming connection
	 */
	public synchronized void DecrementConCount() {
		conCount--;
	}

	/**
	 * method to broadcast client messages to all connected clients
	 * @param message - single client message to be broadcast to all server clients
	 * @param type - token code to further encode chat messages
	 */
	public synchronized void broadcast(String message, int type) 
	{

		//iterate all client threads and send a single client broadcast message
		for (ClientHandler cHand:connections) 
		{
			cHand.sendChatMessage(message, type);
		}
	}

	/**
	 * print client messages or necessary information to server console but not to clients
	 * @param message - client message or information to be output on server console only
	 */
	public synchronized void printClientMessage(String message) 
	{

		System.out.println(message);
	}

	/**
	 * method to iterate the list of connections and gather all player names as a string
	 * to be transmitted to the client
	 * @return players - a concatenated string on player names to be transmitted to player
	 */
	public synchronized String getAllPlayers() 
	{

		//variable storage to concatenate player names to
		String players = "";
		//loop control to know when to place commas
		int count = 1;

		//iterate the connected clients
		for(ClientHandler cHand:connections) 
		{

			//if this is the first player name ignore the leading comma
			if(count == 1)
			{
				players += cHand.getName();
			}

			//otherwise provide a delimiter to split names on
			else {
				players += " , " +cHand.getName();
			}

			//increment counter... only matters once but whatever
			count++;

		}

		return players;
	}

	/**
	 * method to save the game data sent by user
	 * @param gameData - encoded string of data to represent the game logic
	 */
	public synchronized void saveGameBoard(String gameData)
	{
		gameBoardCode = gameData;

	}

	/**
	 * method to retrieve current game saved on server
	 * @return gameBoardCode - encoded string of data representing game logic
	 */
	public synchronized String getCurrentGame()
	{

		if(gameBoardCode == "")
		{
			return "No game board currently on server";
		}

		else 
		{
			return gameBoardCode;
		}

	}

	/**
	 * Method to gather the highscore table as a string for transmission
	 * @return highScore - an encoded string of every entry in the score table
	 */
	public synchronized String getHighScore()
	{



		//string to concatenate the highscore table for transmission
		String highScore = "";

		//if table is empty return a string to transmit to client
		if(highScoreT.isEmpty()) {
			highScore = "There is currently no highscore";
		}

		//otherwise collect all scores from table
		else
		{
			for(int i = 0; i < highScoreT.size(); i++)
			{
				highScore += highScoreT.get(i) + "\n";
			}
		}

		return highScore;
	}

	/**
	 * method to append a highscore to the vector
	 * @param userScore - client provided score data at time of game upload
	 */
	public synchronized void addHighscore(String userScore) 
	{

		highScoreT.add(userScore);
	}

	/**
	 * method to reset the highscore table when a new game is uploaded
	 */
	public synchronized void resetHighScore()
	{
		highScoreT.clear();
	}

	/**
	 * method to delete the unique thread from the connection list
	 * @param ch - reference to the individual client thread for comparison in vector
	 */
	public synchronized void deleteThread(ClientHandler ch) {

		//iterate the vector of connections until the disconnected client is found, remove from list
		for(int i =0; i<connections.size();i++) {

			//if object in vector is exact object to be removed
			if( connections.get(i).equals(ch))
			{
				connections.remove(i);
				break;
			}
		}

	}

	/**
	 * Entry point of program, parses command line arguments and instantiates/launches server
	 * @param args - optional port number at command line
	 */
	public static void main(String args[]) 
	{

		/** default port number used if not provided or provided incorrectly by user */
		final int defaultPort = 61001;

		int port = defaultPort;

		//if arguments are passed at command line (port number)
		if(args.length > 0) 
		{

			try 
			{
				//set server port to user provided port number
				port = Integer.parseInt(args[0]);
			}

			//ensure argument input is an integer
			catch(NumberFormatException e) 
			{
				System.out.println("\nERROR: Invalid port number " + args[0] +"");
				//set server to default port number 
				port = defaultPort;
				System.out.printf("Using default port: %d\n", defaultPort);
			}

			//check if port number is proper 16 bit integer range
			if(port <10000 || port > 65536) 
			{
				System.out.printf("\nERROR: Invalid port number: %d", port);
				//set server to default port number 
				port = defaultPort;
				System.out.printf("Using default port: %d\n", defaultPort);
			}
		}//end console arguments

		//instantiate and server object with defined port, and launch
		PiccrossServer server = new PiccrossServer(port);
		server.launchServer();

	}//end server main program
}// end PiccrossServer class

/**
 * Class to instantiate and manage individual client threads as they are connected to the server
 * @author gregr, aidenm
 */
class ClientHandler implements Runnable
{

	/** server reference to communicate from client*/
	PiccrossServer server;
	/** buffer reader for client input stream*/
	private Scanner in;
	/** buffer writer for client input stream*/
	private PrintWriter out;
	/** new client socket connection*/
	private Socket incoming;
	/** client display name */
	private String name;

	/**
	 * constructor for client socket connection
	 * @param i - the incoming client socket connection received by server
	 * @param s - the server being connected to
	 */
	public ClientHandler(Socket i,PiccrossServer s) 
	{
		server = s;
		incoming = i;

	}//end constructor

	/**
	 *  Manages individual client threads input/output and command tasks
	 */
	public void run() 
	{
		try
		{
			try
			{
				//get input and output streams of client socket connection
				InputStream inStream = incoming.getInputStream();
				OutputStream outStream = incoming.getOutputStream();

				in = new Scanner(inStream);
				out = new PrintWriter(outStream, true);

				out.println("Connection to Piccross Server successful \n Welcome to G & A Picross ! \n Use '#HELP' for commands.");

				boolean done = false;
				while(!done && in.hasNextLine())
				{
					String line = in.nextLine();

					//Special condition for assigning the clients user name at connection time
					if (line.startsWith("@")) {
						String[] parts = line.split("@");
						String name = parts[1];

						setName(name);
						server.printClientMessage(name+" has connected.");

					}

					else if (line.startsWith("$$$GAME-")) {
						String[] game = line.split("-");

						//STRING[] GAME
						//0 = game command
						//1 = game data
						//2 = user name
						//3 = user score
						//4 = user time

						//re-concatenate the Game data string to be compared or stored
						String gameData = game[0] + "-" + game[1];

						//concatenate users score data for score table
						String userScore = game[2] + "  " + game[3] + "  " + game[4];

						//see if game board already exists in the server.
						//since we are only storing one game this will be the condition to append the highscore table
						if(server.getCurrentGame().equals(gameData)) 
						{
							//collect the users score for printing to server
							String scoreAddedMsg = game[2] + " has uploaded a new highscore " + game[3] + " , " + game[4];
							//add score to the score table vector
							server.addHighscore(userScore);
							//print notification on server console
							server.printClientMessage(scoreAddedMsg);
						}

						//if its a new board we will save it to the server and restart the high score table
						else {
							String newGameMsg = game[2] + " has uploaded a new game to server, high scores reset";
							server.saveGameBoard(gameData);
							server.resetHighScore();

							server.addHighscore(userScore);

							//notify server and clients of new game being available on server
							server.broadcast(newGameMsg, 0);
							server.printClientMessage(newGameMsg);
						}

					}//end game board special condition

					//selections structure for parsing client commands to server
					else if (line.startsWith("#")) {
						String [] parts = line.split("#");
						String command = parts[1];

						//special case for changing a users name to further parse the string into command and new name
						if(command.startsWith("NAME-"))
						{
							//Separate command from the new name
							String[] changeName = line.split("-");

							//build a string message to broadcast upon change of user name
							String message = getName();
							message += " has changed their name to " + changeName[1];

							//set the new name to the client thread
							setName(changeName[1]);

							//send encoded command to client to change local computer reference
							out.println("$$$NAME~" + changeName[1]);

							//broadcast new name to all clients
							server.broadcast(message, 0);
							server.printClientMessage(message);

						}		

						else if("HELP".equals(command.trim())) 
						{
							helpMessage(out);
						}
						else if("WHO".equals(command.trim())) 
						{
							out.println(server.getAllPlayers());
						}
						else if("GET".equals(command.trim()))
						{
							
							//if there is no game board return a notification message
							if(server.getCurrentGame().equals("No game board currently on server")) {
								out.println(server.getCurrentGame());
							}
							
							//if there is a current game board return the game plus current score data of that game
							else {
								String gameWithScore = server.getCurrentGame() + "~" + server.getHighScore();
								out.println(gameWithScore);
							}
							
						}
						else if("SCORE".equals(command.trim())) 
						{
							out.println(server.getHighScore());
						}
						else if("BYE".equals(command.trim())) 
						{
							done = true;
						}

						//if anything parsed after # does not match a command print error message
						else
						{
							out.println("not a valid command, type #HELP for list of commands");
						}

					}//end command line parse tree


					//if stream is not prefixed by a command or special encode assume chat message and broadcast
					else {	
						
						//add the client threads user name to message to be printed on client side chat windows
						//this allows all messages broadcast to chat to be labeled as the senders client
						String clientMSG = this.getName() +": " + line;
						server.broadcast(clientMSG, 1);
					}

				}//end client input loop

			}//end inner try

			finally
			{
				//notification of client disconnection
				incoming.close();
				server.broadcast(disconnectMessage(), 0);
				server.printClientMessage(disconnectMessage());
				server.DecrementConCount();
				server.deleteThread(this);

			}//end finally

		}//end outer try

		catch(IOException e)
		{
			e.printStackTrace();
		}//end catch
	}//end run

	/**
	 * method to retrieve client threads user name
	 * @return name - user name of client thread
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * method to set the name of the client thread based on the connection message sent by
	 * client at connection time
	 * @param newName - name sent by client to be stored on the server
	 */
	public void setName(String newName) 
	{
		this.name = newName;
	}

	/**
	 * method to print the command list to the client
	 * @param out - the direct output stream of the client thread
	 */
	public void helpMessage(PrintWriter out) 
	{
		out.println("#HELP : this message.\n#BYE: disconnect.\n#WHO: list all connected players.\n#NAME-newName: Rename yourself.\n#GET: gets the current game.\n#SCORE: gets the score table of current server game\n");

	}

	/**
	 * method to pass a disconnect message to the server broadcast and console when the client thread ends or disconnects
	 * @return message - a default disconnect message with the threads user name
	 */
	public String disconnectMessage()
	{
		String message = name + " has Disconnected";
		return message;
	}

	/**
	 * Method to transmit data from server to client connected at this thread
	 * @param message - encoded strings of chat or server messages
	 * @param type - parameter to parse which output the message is intended for
	 */
	public void sendChatMessage(String message, int type) {

		//chat message types
		// 0 = server to chat notifications ( ie. notice of name change)
		// 1 = client to clients chat message
		if(type == 0) 
		{
			String serverMSG = "$$$CAST~" + "Server: " + message;
			out.println(serverMSG);
		}

		else
		{
			//encode all broadcast messages to be parsed to chat output box
			String chatMSG = "$$$CAST~" + message;
			out.println(chatMSG);
		}


	}

}//end client handler class test 3