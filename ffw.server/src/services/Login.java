package services;

// Java Imports
import java.io.IOException;
import java.util.List;

import common.*;
import common.msg.*;
import main.Constants;
import models.t_user;

class LoginRsp extends GameResponse
{
	public LoginRsp(int sn, int resultCode, String errMsg)
	{
		buf = new MsgEncoder(Constants.RSP_LOGIN);
		buf.addInt32(sn);
		buf.addInt32(resultCode);
		buf.addString(errMsg);
	}
}

/**
 * The RequestLogin class authenticates the user information to log in. Other
 * tasks as part of the login process lies here as well.
 */
public class Login extends GameRequest 
{
	private int sn;
    private String name;
    private String passwd;
    
	@Override
	public void parse() throws IOException 
	{
		sn = nextInteger();
		name = nextString().trim();
		passwd = nextString().trim();
	}
	
	@Override
	public void doBusiness() throws Exception 
	{
		List<t_user> list = MySQL.select(t_user.class, "select * from t_user where name='%s'", name);
		if(list.size() == 0)
		{
			Log.printf("Login(user=%s, passwd=%s): no such a user.", name, passwd);
			responses.add(new LoginRsp(sn, -1, "no such a user"));
			isDone = true;
		}
		else
		{
			t_user u = list.get(0);
			if(!u.passwd.equals(passwd))
			{
				Log.printf("Login(user=%s, passwd=%s): wrong password.", name, passwd);
				responses.add(new LoginRsp(sn, -2, "wrong password"));
				isDone = true;
			}
			else
			{
				Log.printf("Login(user=%s, passwd=%s): auth pass.", name, passwd);
				responses.add(new LoginRsp(sn, 0, ""));
				
				// other logic
				client.user = name;
			}
		}
	}
}
