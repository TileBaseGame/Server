package services;

import java.io.IOException;
import java.util.List;

import common.*;
import common.msg.*;
import main.Constants;
import main.GameServer;
import models.t_commander;

class SelectCommanderRsp extends GameResponse
{
	public SelectCommanderRsp(int sn, int resultCode, String opponent)
	{
		buf = new MsgEncoder(Constants.RSP_SELECT_COMMANDER);
		buf.addInt32(sn);
		buf.addInt32(resultCode);
		buf.addString(opponent);
	}
}

/**
 * The RequestLogin class authenticates the user information to log in. Other
 * tasks as part of the login process lies here as well.
 */
public class SelectCommander extends GameRequest 
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
		List<t_commander> list = MySQL.select(t_commander.class, "select * from t_commander where id = '%d'", commanderId);
		if(list.size() == 0)
		{
			Log.printf("SelectCommander: invalid commander(id=%d).", commanderId);
			responses.add(new SelectCommanderRsp(sn, -1, "invalid commander"));
			return;
		}
		
		if(client.battle == null)
		{
			Log.printf("SelectCommander: illegal operation, no matched battle yet.");
			responses.add(new SelectCommanderRsp(sn, -2, "illegal operation"));
			return;
		}
		
		client.battle.setCommander(client.playerId, list.get(0));
		Log.printf("SelectCommander: commander(id=%d) selected.", commanderId);
		responses.add(new SelectCommanderRsp(sn, 0, ""));
	}
}
