package services;

import java.io.IOException;
import common.*;
import common.msg.*;
import main.Constants;
import main.GameClient;
import main.GameServer;

class QuickPlayRsp extends GameResponse
{
	public QuickPlayRsp(int sn, String who_first, int resultCode, String opponent)
	{
		buf = new MsgEncoder(Constants.RSP_QUICKPLAY);
		buf.addInt32(sn);
		buf.addInt32(resultCode);
		buf.addString(opponent);
		buf.addString(who_first);
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
		if(client.battle != null)
		{
			client.battle.quit(client.playerId);
			client.battle = null;
			
			GameClient oppo = client.battle.getOpponent(client.playerId);
			if(oppo != null) // tell opponent which units i select 
			{
				Log.printf("QuitPlay: notify '%s' that '%s' has quit battle.", oppo.user, client.user);
				oppo.putResponse(new QuitPlayRsp(Constants.RSP_OPPONENT_QUITPLAY, 0, 0, ""));
			}
		}
		
		Log.printf("QuickPlay: begin to match battle with '%s'.", client.user);
		GameServer.getInstance().matchBattle(client);
	}
	
	public static GameResponse doResponse(String who_first, String opponent)
	{
		return new QuickPlayRsp(0, who_first, 1, opponent);
	}
}
