package services;

import java.io.IOException;
import java.util.List;

import common.*;
import common.msg.*;
import main.Constants;
import main.GameClient;
import models.t_unit_abilities;

class SetUnitsAbilitiesRsp extends GameResponse
{
	public SetUnitsAbilitiesRsp(long[] units, long[] units_abilities)
	{
		buf = new MsgEncoder(Constants.RSP_SET_OPPONENT_UNITS_ABILITIES);
		buf.addInt32(0);
		
		buf.addLong(units[0]);
		buf.addLong(units_abilities[0]);
		
		buf.addLong(units[1]);
		buf.addLong(units_abilities[1]);
		
		buf.addLong(units[2]);
		buf.addLong(units_abilities[2]);
		
		buf.addLong(units[3]);
		buf.addLong(units_abilities[3]);
		
		buf.addLong(units[4]);
		buf.addLong(units_abilities[4]);
	}
	
	public SetUnitsAbilitiesRsp(int sn, int[] check, int resultCode, String opponent)
	{
		buf = new MsgEncoder(Constants.RSP_SET_UNITS_ABILITIES);
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
public class SetUnitsAbilities extends GameRequest 
{
	private int sn;
	private long[] units = {0, 0, 0, 0, 0};
	private long[] units_abilities = {0, 0, 0, 0, 0};
	
	@Override
	public void parse() throws IOException 
	{
		sn = nextInteger();
		units[0] = nextLong();
		units_abilities[0] = nextLong();
		
		units[1] = nextLong();
		units_abilities[1] = nextLong();
		
		units[2] = nextLong();
		units_abilities[2] = nextLong();
		
		units[3] = nextLong();
		units_abilities[3] = nextLong();
		
		units[4] = nextLong();
		units_abilities[4] = nextLong();
	}
	
	@Override
	public void doBusiness() throws Exception 
	{
		boolean succ = true;
		int[] check = {0, 0, 0, 0, 0};
		for(int i=0; i<units_abilities.length; i++)
		{
			List<t_unit_abilities> list = MySQL.select(t_unit_abilities.class, "select * from t_unit_abilities where id = %d and unit_id = %d", 
					units_abilities[i], units[i]);
			
			if(list.size() == 0)
			{
				Log.printf("SetUnitsAbilities: invalid unit[%d](id=%d)'s ability(id=%d).", i, units[i], units_abilities[i]);
				check[i] = -1;
				succ = false;
			}
		}
		
		if(client.battle == null || !client.battle.check())
		{
			Log.printf("SetUnitsAbilities: illegal operation, no matched battle.");
			responses.add(new SetUnitsRsp(sn, check, -2, "illegal operation, no matched battle."));
			return;
		}
		
		if(!succ)
		{
			Log.printf("SetUnitsAbilities: invalid units' abilities(%d:%d:%d, %d:%d:%d, %d:%d:%d, %d:%d:%d, %d:%d:%d).", 
					units[0], units_abilities[0], check[0], 
					units[1], units_abilities[1], check[1],
					units[2], units_abilities[2], check[2],
					units[3], units_abilities[3], check[3],
					units[4], units_abilities[4], check[4]);
			
			responses.add(new SetUnitsAbilitiesRsp(sn, check, -1, "invalid units' abilities"));
			return;
		}
			
		GameClient oppo = client.battle.getOpponent(client.playerId);
		if(oppo != null) // tell opponent which commander i select 
		{
			Log.printf("SetUnitsAbilities: notify '%s' that '%s' has selected units' abilities(%d:%d, %d:%d, %d:%d, %d:%d, %d:%d).", 
					oppo.user, client.user, 
					units[0], units_abilities[0], 
					units[1], units_abilities[1], 
					units[2], units_abilities[2], 
					units[3], units_abilities[3], 
					units[4], units_abilities[4]);
			
			oppo.putResponse(new SetUnitsAbilitiesRsp(units, units_abilities));
		}

		Log.printf("SetUnitsAbilities: '%s' has selected units' abilities(%d:%d, %d:%d, %d:%d, %d:%d, %d:%d).", 
				client.user, 
				units[0], units_abilities[0], 
				units[1], units_abilities[1], 
				units[2], units_abilities[2], 
				units[3], units_abilities[3], 
				units[4], units_abilities[4]);
		responses.add(new SetUnitsAbilitiesRsp(sn, check, 0, ""));
	}
}
