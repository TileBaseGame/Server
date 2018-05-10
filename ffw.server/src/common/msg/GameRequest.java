package common.msg;

// Java Imports
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import main.GameClient;

/**
 * The GameRequest class is an abstract class used as a basis for storing
 * request information.
 */
public abstract class GameRequest 
{
	public int cmd;
	protected boolean isDone = false;
    protected GameClient client;
    protected DataInputStream dataInput;
    protected List<GameResponse> responses;

    public GameRequest() 
    {
        responses = new ArrayList<GameResponse>();
    }
    
    public void bind(int cmd, GameClient client, DataInputStream dataInput)
    {
    	this.cmd = cmd;
    	this.client = client;
    	this.dataInput = dataInput;
    }

    /**
     * Parse the request from the input stream.
     * 
     * @throws IOException 
     */
    public abstract void parse() throws IOException;
    
    public boolean done()
    {
    	return isDone;
    }

    /**
     * Interpret the information from the request.
     * 
     * @throws Exception 
     */
    public abstract void doBusiness() throws Exception;

    /**
     * Get the responses generated from the request.
     * 
     * @return the responses
     */
    public List<GameResponse> getResponses() 
    {
        return responses;
    }
    
    public boolean nextBoolean() throws IOException
    {
    	return MsgDecoder.readBoolean(dataInput);
    }
    
    public byte nextByte() throws IOException
    {
    	return MsgDecoder.readByte(dataInput);
    }
    
    public short nextShort() throws IOException
    {
    	return MsgDecoder.readShort(dataInput);
    }
    
    public int nextInteger() throws IOException
    {
    	return MsgDecoder.readInt(dataInput);
    }
    
    public long nextLong() throws IOException
    {
    	return MsgDecoder.readLong(dataInput);
    }
    
    public float nextFloat() throws IOException
    {
    	return MsgDecoder.readFloat(dataInput);
    }
    
    public String nextString() throws IOException
    {
    	return MsgDecoder.readString(dataInput);
    }

    @Override
    public String toString() 
    {
        String str = "";
        str += "-----" + "\n";
        str += getClass().getName() + "\n";
        str += "\n";

        for (Field field : getClass().getDeclaredFields()) {
            try {
                str += field.getName() + " - " + field.get(this) + "\n";
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        str += "-----";
        return str;
    }
}