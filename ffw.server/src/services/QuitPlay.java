package services;

import java.io.IOException;
import common.*;
import common.msg.*;
import main.Constants;
import main.GameClient;

class QuitPlayRsp extends GameResponse
{
	public QuitPlayRsp(short cmd, int sn, int resultCode, String opponent)
	{
		buf = new MsgEncoder(cmd);
		buf.addInt32(sn);
		buf.addInt32(resultCode);
		buf.addString(opponent);
	}
}

/**
 * The RequestLogin class authenticates the user information to log in. Other
 * tasks as part of the login process lies here as well.
 */
public class QuitPlay extends GameRequest 
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
			GameClient oppo = client.battle.getOpponent(client.playerId);
			if(oppo != null) // tell opponent which units i select 
			{
				Log.printf("QuitPlay: notify '%s' that '%s' has quit battle.", oppo.user, client.user);
				oppo.putResponse(new QuitPlayRsp(Constants.RSP_OPPONENT_QUITPLAY, 0, 0, ""));
			}
			
			client.battle.quit(client.playerId);
			client.battle = null;
		}
		
		Log.printf("QuitPlay: quit battle.");
		responses.add(new QuitPlayRsp(Constants.RSP_QUITPLAY, sn, 0, ""));
	}
	
	public static GameResponse quitNotification()
	{
		return new QuitPlayRsp(Constants.RSP_OPPONENT_QUITPLAY, 0, 0, "");
	}
}
