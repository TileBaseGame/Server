package services;

import java.io.IOException;
import common.*;
import common.msg.*;
import main.Constants;
import main.GameServer;

class QuickPlayRsp extends GameResponse
{
	public QuickPlayRsp(int sn, int resultCode, String opponent)
	{
		buf = new MsgEncoder(Constants.RSP_QUICKPLAY);
		buf.addInt32(sn);
		buf.addInt32(resultCode);
		buf.addString(opponent);
	}
}

/**
 * The RequestLogin class authenticates the user information to log in. Other
 * tasks as part of the login process lies here as well.
 */
public class QuickPlay extends GameRequest 
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
		Log.printf("QuickPlay: begin to match battle with '%s'.", client.user);
		GameServer.getInstance().matchBattle(client);
	}
	
	public static GameResponse doResponse(String opponent)
	{
		return new QuickPlayRsp(0, 1, opponent);
	}
}
