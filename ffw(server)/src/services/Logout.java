package services;

// Java Imports
import java.io.IOException;

import common.*;
import common.msg.*;
import main.Constants;

class LogoutRsp extends GameResponse
{
	public LogoutRsp(int sn, int resultCode, String errMsg)
	{
		buf = new MsgEncoder(Constants.RSP_LOGOUT);
		buf.addInt32(sn);
		buf.addInt32(resultCode);
		buf.addString(errMsg);
	}
}

/**
 * The RequestLogin class authenticates the user information to log in. Other
 * tasks as part of the login process lies here as well.
 */
public class Logout extends GameRequest 
{   
	private int sn;
	
	@Override
	public void parse() throws IOException 
	{
		sn = nextInteger();
	}
	
	@Override
	public void doBusiness() throws Exception 
	{
		Log.printf("Logout: %s has logged off.", client.user);
		responses.add(new LogoutRsp(sn, 0, ""));
		isDone = true;
	}
}
