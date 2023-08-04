
import java.awt.EventQueue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * JAP - CS Academic Level 4
 * Client class for network connection protocol
 * Course: CST8221-302 - Java Application Programming
 * @author Professor: Daniel Cormier
 * @author Aiden Mackean: 040707105
 * @author Greg Rowat: 041001565
 */
public class PiccrossClient {

	/** port number as passed by networkModal */
	private int port;
	/** client name as passed by networkModal */
	private String name;
	/** IPV4 or URL address as passed by networkModal */
	private String address;
	/** view reference for dynamic updating */
	private PiccrossView view;
	/** Reference to model for game board transfer */
	private PiccrossModel model;
	/** static variable to control single instantiation */
	private static PiccrossClient clientSingleton = null;
	/** socket object */
	private Socket clientS;
	/** connection status */
	private boolean connected;
	/** output stream object to communicate with server */
	private PrintWriter out;
	/** timer reference */
	private ControllableTimer timer;


	/**
	 * Constructor for a network client
	 * @param uView - reference to main view of MVC
	 * @param uModel - reference to game model of MVC
	 * @param f - connection status will be false
	 * @param uTimer - reference to the game timer object
	 */
	private PiccrossClient(PiccrossView uView, PiccrossModel uModel, boolean f, ControllableTimer uTimer)
	{
		view = uView;
		model = uModel;
		connected = f;
		timer = uTimer;

	}

	/**
	 * Singleton instantiation of client for network connection. Configs args as constructor fields in call
	 * @param uView - piccross game view board
	 * @param timer - reference to the timer object
	 * @param uModel - name of host model
	 * @param f - boolean control for connection flag
	 * @return reference to client singleton object
	 */
	public static PiccrossClient instantiate(PiccrossView uView, PiccrossModel uModel, boolean f, ControllableTimer timer)
	{

		if(clientSingleton==null) clientSingleton = new PiccrossClient(uView, uModel, f, timer);

		return clientSingleton;

	}

	/**
	 * method to connect the client socket to server, establish IO;
	 * @param client - client reference to pass to the inner class
	 */
	public void connectClient(PiccrossClient client)
	{

		try
		{

			//Try establishing connection to server socket
			clientS = new Socket();
			clientS.connect(new InetSocketAddress(InetAddress.getByName(address),port), 10000);
			
			//only if the socket has connected to the server establish input/output streams and set connection flag
			if(clientS.isConnected()) {

				//set connection flag
				connected = true;

				setOutputStream();

				sendMessage("@" + name);

				//launch a thread to handle all incoming server traffic
				ClientSocketThread cst = new ClientSocketThread(clientS, view, model, client, timer);

				//Runnable r1 = cst1;
				Runnable r1 = cst;

				//Thread t1 = new Thread(r1);
				Thread t1 = new Thread(r1);

				//t1.start();
				t1.start();

			}//end input/output establishment

		}//end try
		catch(UnknownHostException e)
		{
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					
					view.outputText("Unknown host: Unable to connect to " + address);
					
				}
					
			});
			
		}
		catch(SocketTimeoutException e) {
			
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					
					view.outputText("Connection timeout attempting to connect '" + address + "' on port: " + port + " \nplease check port or restart server");
					
				}
					
			});
			
		}
		catch(SocketException e)
		{
			
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					
					view.outputText("Failed to connect no socket listening at port " + port);
					
				}
					
			});
			
		}
		catch (IOException e)
		{
			
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					
					view.outputText("Failed to connect socket or establish Output stream at port " + port);
					e.printStackTrace();
					
				}
					
			});

		}

	}

	/**
	 * method to establish an output stream to the server
	 */
	public void setOutputStream()
	{
		// I wanted to establish the connection in the connection method, but it wasn't being recognized globally
		// so doing it like this keeps the design flow but allows the client to speak to server
		try
		{
			//establish a global outputStream from client socket outputStream
			OutputStream outStream = clientS.getOutputStream();
			out = new PrintWriter(outStream, true);
		}

		catch(IOException e)
		{
			
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					
					view.outputText("Error unable to establish output stream to " + clientS.getPort());
					e.printStackTrace();
					
				}
					
			});
			
		}

	}

	/**
	 * method to send a string message to the server
	 * @param s - message to be sent to server
	 */
	public void sendMessage(String s) {

		try {

			String clientMessage = s;

			out.println(clientMessage);

		} catch (Exception e) {

			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					
					
					view.outputText("Error unable to transmit message on " + clientS.getPort());
					e.printStackTrace();
					
				}
					
			});

		}

	}

	/**
	 * method for checking the connection status of the client to server
	 * @return connected - boolean value of true or false representing status of client connection
	 */
	public synchronized boolean getConnectionStatus()
	{
		return connected;
	}

	/**
	 * set the name of the client through the network dialogue
	 * @param name - user name from the network dialogue
	 */
	public synchronized void setName(String name)
	{
		this.name = name;
	}

	/**
	 * set the address of the client through the network dialogue
	 * @param address - Address from the network dialogue
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}

	/**
	 * set the port of the client through the network dialogue
	 * @param port - port number from network dialogue
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * method to get the current game board data and append the users final scores to
	 * send to server
	 * @param board - the encoded game board logic from model
	 */
	public void uploadGameBoard(String board) {

		String clientMessage = board;

		//append the user score data to the game board data for transmission
		clientMessage += "-" + name +
				"-" + Integer.toString(model.getFinalScore()) +
				"-" + view.getTime();

		//transmit game/score data to server
		out.println(clientMessage);

	}

	/**
	 * method to reset client connection flag
	 */
	public void ConnectionOFF()
	{
		connected = false;
	}

	/**
	 * method to manually disconnect client socket triggering the runnable shutdown procedure
	 */
	public void Disconect()
	{
		try {
			//close the client/server connection, this will trigger shutdown in runnable
			clientS.close();
		} catch (IOException e) {
			
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					
					
					view.outputText("Error closing socket on port " + clientS.getPort());
					e.printStackTrace();
					
				}
					
			});

		}
	}


}//end Piccross client

/**
 * Runnable class to manage all server to client network traffic
 * @author gregr, aidenm
 */
class ClientSocketThread implements Runnable
{

	/**the client socket reference that has been connected to the server*/
	private Socket clientS;
	/** one more view reference */
	private PiccrossView view;
	/** Reference to model for game board updates */
	private PiccrossModel model;
	/** input stream reader */
	private Scanner in;
	/** Reference to client for control */
	private PiccrossClient client;
	/** reference to the timer */
	private ControllableTimer t;

	/**
	 * constructor for a threaded client socket
	 * @param s - Socket connected to server
	 * @param view - reference to the main view for updating chat output
	 * @param model - reference to model to update game logic from server
	 * @param client - reference to client to manage connection status
	 * @param timer - reference to the timer object
	 */
	public ClientSocketThread(Socket s, PiccrossView view, PiccrossModel model, PiccrossClient client, ControllableTimer timer)
	{
		clientS = s;
		this.view = view;
		this.model = model;
		this.client = client;
		t = timer;
	}


	@Override
	public void run()
	{
		try
		{
			//try to establish an incoming stream on clientSocket to listen for server
			InputStream inStream = clientS.getInputStream();
			in = new Scanner(inStream);
			try
			{

				//wait for server incoming data
				while(in.hasNextLine())
				{

					String incomingMSG = in.nextLine();

					// conditional for incoming gameboard
					if(incomingMSG.startsWith("$$$GAME-")) {
						
						String[] parts = incomingMSG.split("~");
						model.updateFromServer(parts[0]);

						// add the ui updates to the event queue
						EventQueue.invokeLater(new Runnable() {

							@Override
							public void run() {
								
								view.outputText("Game loaded from server");
								view.outputText(parts[1]);
								// reset the game board with current logic pulled
								view.resetView();
								//reset and restart timer
								t.setStatus(1);
								t.setStatus(3);

							}

						});//end eventqueue call

					}//end game update message

					//encoded messages from chat broadcast to be appended to chat output window
					else if(incomingMSG.startsWith("$$$CAST~"))
					{
						String[] parts = incomingMSG.split("~");
						String chatMSG = "";


						//iteration loop in case user uses ~ in regular chat message. Reassemble message
						for(int i=1; i<parts.length; i++)
						{
							 chatMSG += parts[i];
						}

						final String test = chatMSG;
						// add ui updates to the eventqueue
						EventQueue.invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								// output incoming text to the ui
								view.outputChat(test);
							}
						}); //End EventQueue

					}

					//If user has changed their name on the server, feedback message to set the name in the client
					else if(incomingMSG.startsWith("$$$NAME~"))
					{
						String[] parts = incomingMSG.split("~");
						client.setName(parts[1]);

					}//end name change message

					else {

						// add ui updates to the eventqueue
						EventQueue.invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								// output incoming text to the ui
								view.outputText(incomingMSG);
							}
						}); //End EventQueue

					}//end standard chat message

				}//end blocking action to listen for server on thread

			}//end try

			finally
			{
				//close the network thread
				clientS.close();
				//set client connection to false
				client.ConnectionOFF();
				//reset the network menu
				view.setConnection();
				
				// add ui updates to the eventqueue
				EventQueue.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						// output incoming text to the ui
						view.outputText("Disconected : Network Client closed");
					}
				}); //End EventQueue

			}
		}

		catch (IOException e)
		{
			
			EventQueue.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					// output incoming text to the ui
					view.outputText("Error establishing inStream connection to port " + clientS.getPort());
					e.printStackTrace();
				}
			}); //End EventQueue

		}

	}//end run()

}//end class