package services;

import java.io.IOException;

import common.*;
import common.msg.*;
import main.Constants;
import main.GameClient;

class GameActionRsp extends GameResponse
{
	public GameActionRsp(long who, int x, int y, long[] units, int[] damages)
	{
		buf = new MsgEncoder(Constants.RSP_OPPONENT_GAME_ACTION);
		buf.addInt32(0);
		
		buf.addLong(who);
		buf.addInt32(x);
		buf.addInt32(y);
		
		buf.addLong(units[0]);
		buf.addInt32(damages[0]);
		
		buf.addLong(units[1]);
		buf.addInt32(damages[1]);
		
		buf.addLong(units[2]);
		buf.addInt32(damages[2]);
		
		buf.addLong(units[3]);
		buf.addInt32(damages[3]);
		
		buf.addLong(units[4]);
		buf.addInt32(damages[4]);
	}
	
	public GameActionRsp(int sn, int resultCode, String opponent)
	{
		buf = new MsgEncoder(Constants.RSP_GAME_ACTION);
		buf.addInt32(sn);

		buf.addInt32(resultCode);
		buf.addString(opponent);
	}
}

/**
 * The RequestLogin class authenticates the user information to log in. Other
 * tasks as part of the login process lies here as well.
 */
public class GameAction extends GameRequest 
{
	private int sn;
	private long who;
	private int x, y;
	private long[] units = {0, 0, 0, 0, 0};
	private int[] damages = {0, 0, 0, 0, 0};
	
	@Override
	public void parse() throws IOException 
	{
		sn = nextInteger();
		
		who = nextLong();
		x = nextInteger();
		y = nextInteger();
		
		units[0] = nextLong();
		damages[0] = nextInteger();
		
		units[1] = nextLong();
		damages[1] = nextInteger();
		
		units[2] = nextLong();
		damages[2] = nextInteger();
		
		units[3] = nextLong();
		damages[3] = nextInteger();
		
		units[4] = nextLong();
		damages[4] = nextInteger();
	}
	
	@Override
	public void doBusiness() throws Exception 
	{
		if(client.battle == null || !client.battle.check())
		{
			Log.printf("GameAction: illegal operation, no matched battle.");
			responses.add(new GameActionRsp(sn, -2, "illegal operation, no matched battle."));
			return;
		}
		
		GameClient oppo = client.battle.getOpponent(client.playerId);
		if(oppo != null) // tell opponent which units i select 
		{
			Log.printf("GameAction: notify '%s' that '%s' has move unit(%d) to (%d, %d), damages(%d:%d, %d:%d, %d:%d, %d:%d, %d:%d).", 
					oppo.user, client.user, who, x, y,
					units[0], damages[0],
					units[1], damages[1],
					units[2], damages[2],
					units[3], damages[3],
					units[4], damages[4]);
			oppo.putResponse(new GameActionRsp(who, x, y, units, damages));
		}
		
		Log.printf("GameAction: '%s' moves unit(%d) to (%d, %d), damages(%d:%d, %d:%d, %d:%d, %d:%d, %d:%d).", 
				client.user, who, x, y,
				units[0], damages[0],
				units[1], damages[1],
				units[2], damages[2],
				units[3], damages[3],
				units[4], damages[4]);

		responses.add(new GameActionRsp(sn, 0, ""));
	}
}

