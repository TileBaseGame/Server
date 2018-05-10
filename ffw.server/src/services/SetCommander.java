package services;

import java.io.IOException;
import java.util.List;

import common.*;
import common.msg.*;
import main.Constants;
import main.GameClient;
import models.t_commander;

class SetCommanderRsp extends GameResponse
{
	public SetCommanderRsp(long commanderId)
	{
		buf = new MsgEncoder(Constants.RSP_SET_OPPONENT_COMMANDER);
		buf.addInt32(0);
		buf.addLong(commanderId);
	}
	
	public SetCommanderRsp(int sn, long commanderId, int resultCode, String opponent)
	{
		buf = new MsgEncoder(Constants.RSP_SET_COMMANDER);
		buf.addInt32(sn);
		buf.addLong(commanderId);
		buf.addInt32(resultCode);
		buf.addString(opponent);
	}
}

/**
 * The RequestLogin class authenticates the user information to log in. Other
 * tasks as part of the login process lies here as well.
 */
public class SetCommander extends GameRequest 
{
	private int sn;
	private long commanderId;
	
	@Override
	public void parse() throws IOException 
	{
		sn = nextInteger();
		commanderId = this.nextLong();
	}
	
	@Override
	public void doBusiness() throws Exception 
	{
		List<t_commander> list = MySQL.select(t_commander.class, "select * from t_commander where id = %d", commanderId);
		if(list.size() == 0)
		{
			Log.printf("SetCommander: invalid commander(id=%d).", commanderId);
			responses.add(new SetCommanderRsp(sn, commanderId, -1, "invalid commander"));
			return;
		}
		
		if(client.battle == null || !client.battle.check())
		{
			Log.printf("SetCommander: illegal operation, no matched battle.");
			responses.add(new SetCommanderRsp(sn, commanderId, -2, "illegal operation, no matched battle."));
			return;
		}
		
		GameClient oppo = client.battle.getOpponent(client.playerId);
		if(oppo != null) // tell opponent which commander i select 
		{
			Log.printf("SetCommander: notify '%s' that '%s' has selected commander(id=%d).", oppo.user, client.user, commanderId);
			oppo.putResponse(new SetCommanderRsp(commanderId));
		}
		
		client.battle.setCommander(client.playerId, list.get(0));
		Log.printf("SetCommander: '%s' selects commander(id=%d).", client.user, commanderId);
		responses.add(new SetCommanderRsp(sn, commanderId, 0, ""));
	}
}
