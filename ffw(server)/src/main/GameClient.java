package main;

// Java Imports
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import common.*;
import common.msg.*;
import services.QuickPlay;

/**
 * The GameClient class is an extension of the Thread class that represents an
 * individual client. Not only does this class holds the connection between the
 * client and server, it is also in charge of managing the connection to
 * actively receive incoming requests and send outgoing responses. This thread
 * lasts as long as the connection is alive.
 */
public class GameClient implements Runnable 
{
    // Client Variables
    private String sessionId;
    private Socket clientSocket;
    
    private InputStream inputStream; // For use with incoming requests
    private OutputStream outputStream; // For use with outgoing responses
    private DataInputStream dataInputStream; // Stores incoming requests for use
    
    public boolean isDone = false;
    private Queue<GameResponse> updates; // Temporarily store responses for client
    
    public String user;
    
    public int playerId = 0;
    public Battle battle = null;

    /**
     * Initialize the GameClient using the client socket and creating both input
     * and output streams.
     * 
     * @param sessionId holds the unique identifier of this session
     * @param clientSocket holds reference of the socket being used
     * @throws IOException 
     */
    public GameClient(String sessionId, Socket clientSocket) throws IOException 
    {
        this.sessionId = sessionId;
        this.clientSocket = clientSocket;

        updates = new LinkedList<GameResponse>();

        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();
        dataInputStream = new DataInputStream(inputStream);
    }
    
    public void startBattle(Battle bat, int playerId)
    {
    	this.battle = bat;
    	this.playerId = playerId;
    	
    	putResponse(QuickPlay.doResponse(battle.opponent(playerId).user));
    }

    /**
     * Holds the main loop that processes incoming requests by first identifying
     * its type, then interpret the following data in each determined req
     * class. Queued up responses created from each req class will be sent
     * after the req is finished processing.
     * 
     * The loop exits whenever the isPlaying flag is set to false. One of these
     * occurrences is triggered by a timeout. A timeout occurs whenever no
     * activity is picked up from the client such as being disconnected.
     */
    @Override
    public void run() 
    {
    	short cmd = -1;
        long lastActivity = System.currentTimeMillis();
        while (!isDone) 
        {
            try 
            {
                // Extract the size of the package from the data stream
                short reqlen = MsgDecoder.readShort(dataInputStream);
                if (reqlen > 0)
                {
                	//System.out.println("get a request: length="+reqlen);
                    lastActivity = System.currentTimeMillis();
                    
                    // Separate the remaining package from the data stream
                    byte[] buffer = new byte[reqlen];
                    inputStream.read(buffer, 0, reqlen);
                    DataInputStream dataInput = new DataInputStream(new ByteArrayInputStream(buffer));
                    
                    // Extract the request code number
                    cmd = MsgDecoder.readShort(dataInput);
                    GameRequest req = ServiceTable.get(cmd);
                    if (req != null) 
                    {
                    	req.bind(cmd, this, dataInput);
                        req.parse(); // Parse the req
                        req.doBusiness(); // Interpret the data
                        try 
                        {
                            // Retrieve any responses created by the req object
                            for (GameResponse rsp : req.getResponses()) 
                            {
                                // Transform the response into bytes and pass it into the output stream
                            	//System.out.println("send rsp...");
                                send(rsp);
                            }
                            isDone = req.done();
                        } 
                        catch (IOException ex) 
                        {
                        	ex.printStackTrace();
                            Log.printf_e("Client %s connection lost: %s", sessionId, ex.getMessage());
                            isDone = true;
                        }
                    }
                } 
                else
                {
                	// check async responses, if any
                	try 
                    {
                		Queue<GameResponse> rsps = null;
                		synchronized(this)
                		{
                			if(updates.size() > 0)
                			{
                				rsps = updates;
                				updates = new LinkedList<GameResponse>();
                			}
                		}
                		
                		if(rsps != null)
                		{
	                		// Retrieve any responses created by the req object
	                        for (GameResponse rsp : rsps) 
	                        {
	                            // Transform the response into bytes and pass it into the output stream
	                            send(rsp);
	                        }
                		}
                    } 
                    catch (IOException ex) 
                    {
                        Log.printf_e("Client %s connection lost: %s", sessionId, ex.getMessage());
                        isDone = true;
                    }
                	
                    // If there was no activity for the last moments, exit loop
                    if ((System.currentTimeMillis() - lastActivity) / 1000 >= Constants.TIMEOUT_SECONDS) 
                    {
                        isDone = true;
                    }
                }
            } 
            catch (Exception ex) 
            {
                Log.printf_e("Request [%d] Error: %s", cmd, ex.getMessage());
                ex.printStackTrace();
            }
        }

        // Remove this GameClient from the server
        GameServer.getInstance().destroySession(sessionId);
        Log.printf("Session %s is ended.", sessionId);
    }

    private void send(GameResponse response) throws IOException 
    {
        outputStream.write(response.constructResponseInBytes());
    }

    public boolean putResponse(GameResponse rsp) 
    {
    	synchronized(this)
    	{
    		return updates.add(rsp);
    	}
    }

    public String getIP() 
    {
        return clientSocket.getInetAddress().getHostAddress();
    }
    
    public String getSessionId() 
    {
        return sessionId;
    }
}
