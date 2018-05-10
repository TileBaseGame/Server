package main;

import java.util.ArrayList;

import models.t_commander;
import models.t_unit;

class Player
{
	public t_commander commander = new t_commander();
	public ArrayList<t_unit> units = new ArrayList<t_unit>();
	
	public void setCommander(t_commander commander)
	{
		this.commander.id = commander.id;
		this.commander.name = commander.name;
	}
	
	public void addUnit(t_unit unit)
	{
		
	}
}

public class Battle
{
	public GameClient[] clients = new GameClient[2];
	public Player[] players = new Player[2];
	
	public Battle(GameClient c1, GameClient c2)
	{
		init(c1, c2);
	}
	
	public void init(GameClient c1, GameClient c2)
	{
		clients[0] = c1;
		clients[1] = c2;
		
		players[0] = new Player();
		players[1] = new Player();
	}
	
	public boolean check()
	{
		synchronized(this)
		{
			return clients[0] != null && clients[1] != null;
		}
	}
	
	public void quit(int playerId)
	{
		synchronized(this)
		{
			clients[playerId] = null;
			players[playerId] = null;
		}
	}
	
	public GameClient getOpponent(int playerId)
	{
		synchronized(this)
		{
			if(playerId == 0) return clients[1];
			if(playerId == 1) return clients[0];
		}
		return null;
	}
	
	public void setCommander(int playerId, t_commander commander)
	{
		players[playerId].setCommander(commander);
	}
	
	public void setUnit(int playerId, t_commander commander)
	{
		players[playerId].setCommander(commander);
	}
}
