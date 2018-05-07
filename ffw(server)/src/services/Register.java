package services;

//Java Imports
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import common.*;
import common.msg.*;
import main.Constants;
import models.t_user;

class RegisterRsp extends GameResponse
{
	public RegisterRsp(int sn, int resultCode, String errMsg)
	{
		buf = new MsgEncoder(Constants.RSP_REGISTER);
		buf.addInt32(sn);
		buf.addInt32(resultCode);
		buf.addString(errMsg);
	}
}

/**
* The RequestLogin class authenticates the user information to log in. Other
* tasks as part of the login process lies here as well.
*/
public class Register extends GameRequest 
{
	private int sn;
	private String email;
	private String name;
	private String passwd;
	private static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
 
	@Override
	public void parse() throws IOException 
	{
		sn = nextInteger();
		email = nextString().trim();
		name = nextString().trim();
		passwd = nextString().trim();	
	}
	
	@Override
	public void doBusiness() throws Exception 
	{
		if(email.isEmpty() || !Pattern.matches(REGEX_EMAIL, email))
		{
			Log.printf("Register(email=%s, user=%s, passwd=%s): invalid email.", email, name, passwd);
			responses.add(new RegisterRsp(sn, -3, "invalid email"));
			isDone = true;
			return;
		}
		
		if(name.isEmpty())
		{
			Log.printf("Register(email=%s, user=%s, passwd=%s): invalid user name.", email, name, passwd);
			responses.add(new RegisterRsp(sn, -4, "invalid user name"));
			isDone = true;
			return;
		}
		
		if(passwd.isEmpty())
		{
			Log.printf("Register(email=%s, user=%s, passwd=%s): password should not be empty.", email, name, passwd);
			responses.add(new RegisterRsp(sn, -5, "password should not be empty"));
			isDone = true;
			return;
		}
		
		List<t_user> list = MySQL.select(t_user.class, "select * from t_user where email='%s'", email);
		if(list.size() != 0)
		{
			Log.printf("Register(email=%s, user=%s, passwd=%s): email already existed.", email, name, passwd);
			responses.add(new RegisterRsp(sn, -2, "email already existed"));
			isDone = true;
			return;
		}
		
		list = MySQL.select(t_user.class, "select * from t_user where name='%s'", name);
		if(list.size() != 0)
		{
			Log.printf("Register(email=%s, user=%s, passwd=%s): user name already existed.", email, name, passwd);
			responses.add(new RegisterRsp(sn, -1, "user name already existed"));
			isDone = true;
			return;
		}
		
		if(!MySQL.execute("insert into t_user (email, name, passwd) values ('%s', '%s', '%s')", email, name, passwd))
		{
			Log.printf("Register(email=%s, user=%s, passwd=%s): database exception.", email, name, passwd);
			responses.add(new RegisterRsp(sn, -100, "database exception"));
			isDone = true;
			return;
		}
		
		Log.printf("Register(email=%s, user=%s, passwd=%s): auth pass, user '%s' registered.", email, name, passwd, name);
		client.user = name;
		responses.add(new RegisterRsp(sn, 0, ""));
	}
}
