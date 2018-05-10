package services;

import java.io.IOException;
import java.util.List;

import common.*;
import common.msg.*;
import main.Constants;
import main.GameClient;
import models.t_unit;

class SetUnitsRsp extends GameResponse
{
	public SetUnitsRsp(long[] units)
	{
		buf = new MsgEncoder(Constants.RSP_SET_OPPONENT_UNITS);
		buf.addInt32(0);
		buf.addLong(units[0]);
		buf.addLong(units[1]);
		buf.addLong(units[2]);
		buf.addLong(units[3]);
		buf.addLong(units[4]);
	}
	
	public SetUnitsRsp(int sn, int[] check, int resultCode, String opponent)
	{
		buf = new MsgEncoder(Constants.RSP_SET_UNITS);
		buf.addInt32(sn);
		
		buf.addInt32(check[0]);
		buf.addInt32(check[1]);
		buf.addInt32(check[2]);
		buf.addInt32(check[3]);
		buf.addInt32(check[4]);
		
		buf.addInt32(resultCode);
		buf.addString(opponent);
	}
}

/**
 * The RequestLogin class authenticates the user information to log in. Other
 * tasks as part of the login process lies here as well.
 */
public class SetUnits extends GameRequest 
{
	private int sn;
	private long[] units = {0, 0, 0, 0, 0};
	
	@Override
	public void parse() throws IOException 
	{
		sn = nextInteger();
		units[0] = nextLong();
		units[1] = nextLong();
		units[2] = nextLong();
		units[3] = nextLong();
		units[4] = nextLong();
	}
	
	@Override
	public void doBusiness() throws Exception 
	{
		boolean succ = true;
		int[] check = {0, 0, 0, 0, 0};
		for(int i=0; i<units.length; i++)
		{
			List<t_unit> list = MySQL.select(t_unit.class, "select * from t_unit where id = %d", units[i]);
			if(list.size() == 0)
			{
				Log.printf("SetUnits: invalid unit[%d](id=%d).", i, units[i]);
				check[i] = -1;
				succ = false;
			}
		}
		
		if(client.battle == null || !client.battle.check())
		{
			Log.printf("SetUnits: illegal operation, no matched battle.");
			responses.add(new SetUnitsRsp(sn, check, -2, "illegal operation, no matched battle."));
			return;
		}
		
		if(!succ)
		{
			Log.printf("SetUnits: invalid units(%d:%d, %d:%d, %d:%d, %d:%d, %d:%d).", 
					units[0], check[0], 
					units[1], check[1],
					units[2], check[2],
					units[3], check[3],
					units[4], check[4]);
			
			responses.add(new SetUnitsRsp(sn, check, -1, "invalid units"));
			return;
		}
			
		GameClient oppo = client.battle.getOpponent(client.playerId);
		if(oppo != null) // tell opponent which units i select 
		{
			Log.printf("SetUnits: notify '%s' that '%s' has selected units(%d, %d, %d, %d, %d).", 
					oppo.user, client.user, units[0], units[1], units[2], units[3], units[4]);
			oppo.putResponse(new SetUnitsRsp(units));
		}

		Log.printf("SetUnits: '%s' selects units(%d, %d, %d, %d, %d).", client.user, units[0], units[1], units[2], units[3], units[4]);
		responses.add(new SetUnitsRsp(sn, check, 0, ""));
	}
}

