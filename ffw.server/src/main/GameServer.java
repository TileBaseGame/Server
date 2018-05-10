package main;

// Java Imports
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.*;

/**
 * The GameServer class serves as the main module that runs the server.
 * Incoming connection requests are established and redirected to be managed
 * by another class called the GameClient. Several specialized methods are also
 * stored here to perform other specific needs.
 */
public class GameServer 
{
    // Singleton Instance
    private static GameServer gameServer;
    
    // Server Variables
    private boolean isDone; // Server Loop Flag
    private GameServerConf configuration; // Stores gameServer config. variables
    private ServerSocket serverSocket;
    private ExecutorService clientThreadPool;
    
    private List<GameClient> players = new ArrayList<GameClient>();
    
    // Reference Tables
    private Map<String, GameClient> activeThreads = new HashMap<String, GameClient>(); // Session ID -> Client

    /**
     * Create the GameServer by setting up the request types and creating a
     * connection with the database.
     */
    public GameServer() 
    {
        // Load configuration file
        configure();
        
        // Initialize tables for global use
        ServiceTable.init(); // Contains request codes and classes
        
        // Thread Pool for Clients
        clientThreadPool = Executors.newCachedThreadPool();
        
        new Thread()
        {
        	public void run()
        	{
        		while(!isDone)
        		{
        			if(players.size() >= 2)
        			{
        				GameClient c1 = null;
        				GameClient c2 = null;
        				
        				synchronized(GameServer.this)
        				{
	        				c1 = players.get(0);
	        				c2 = players.get(1);
	        				
	        				if(c1.isDone)
	        				{
	        					players.remove(0);
	        					continue;
	        				}
	        				
	        				if(c2.isDone)
	        				{
	        					players.remove(1);
	        					continue;
	        				}
	        				
	        				players.remove(0);
	        				players.remove(0);
        				}
        				
        				Battle bat = new Battle(c1, c2);
        				c1.startBattle(bat, 0, c1.user, c2.user);
        				c2.startBattle(bat, 1, c1.user, c1.user);
        				
        				Log.printf("GameServer: battle matched, player0=[%s], player1=[%s].",  c1.user, c2.user);
        			}
        			else
        			{
        				try
        				{
        					Thread.sleep(200);
        				}
        				catch(Exception e)
        				{
        					
        				}
        			}
        		}
        	}
        }.start();
    }
    
    public void matchBattle(GameClient c)
    {
		synchronized(this)
		{
			players.add(c);
		}
    }

    public static GameServer getInstance() 
    {
        if (gameServer == null)
            gameServer = new GameServer();
        
        return gameServer;
    }

    /**
     * Load values from a configuration file.
     */
    public final void configure()
    {
        configuration = new GameServerConf();

        ConfParser confFileParser = new ConfParser("conf/gameServer.conf");
        configuration.setConfRecords(confFileParser.parse());
    }

    /**
     * Run the game gameServer by waiting for incoming connection requests.
     * Establishes each connection and stores it into a GameClient to manage
     * incoming and outgoing activity.
     */
    private void run() 
    {
        try 
        {
            // Open a connection using the given port to accept incoming connections
            serverSocket = new ServerSocket(configuration.getPortNumber());
            serverSocket.setReuseAddress(true);  // port reuse
            
            Log.printf("Server is started on port: %d", serverSocket.getLocalPort());
            Log.println("Waiting for clients.");
            
            // Loop indefinitely to establish multiple connections
            while (!isDone) 
            {
                try 
                {
                	// Accept the incoming connection from client
                    Socket clientSocket = serverSocket.accept();
                    Log.printf("%s:%d is in.", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                    
                    // Create a runnable instance to represent a client that holds the client socket
                    GameClient client = new GameClient(createUniqueID(), clientSocket);
                    
                    // bind a thread to handle this client
                    startSession(client);
                } 
                catch (IOException e) 
                {
                    System.out.println(e.getMessage());
                }
            }
        } 
        catch (IOException ex) 
        {
            Log.println_e(ex.getMessage());
        }
    }

    private static String createUniqueID() 
    {
        return UUID.randomUUID().toString();
    }
    
    public void startSession(GameClient client) 
    {
        activeThreads.put(client.getSessionId(), client);
        clientThreadPool.submit(client);
    }

    public void destroySession(String session_id) 
    {
        activeThreads.remove(session_id);
    }

    /**
     * Initiates the Game Server by configuring and running it. Restarts
     * whenever it crashes.
     *
     * @param args contains additional launching parameters
     */
    public static void main(String[] args) 
    {
        try 
        {
        	// try to start the game server
            Log.printf("First Fantasy Wars Mk. II is starting...\n");

            gameServer = new GameServer();
            gameServer.run();
        } 
        catch (Exception ex) 
        {
            Log.println_e("Server internel error: " + ex.getMessage());
            
            try 
            {
            	// restart the game server after 30 seconds
                Thread.sleep(30000);
                Log.println_e("Server is now restarting...");
                GameServer.main(args);
            } 
            catch (InterruptedException ex1) 
            {
                Log.println_e(ex1.getMessage());
            }
        }
    }
}
