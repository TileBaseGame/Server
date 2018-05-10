package tests;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import common.*;
import common.msg.*;
import models.t_user;

public class UnitTest 
{
	public String ip = "127.0.0.1";
	public int port = 0;
	
	public UnitTest()
	{
		GameServerConf cfg = new GameServerConf();
        ConfParser conf = new ConfParser("conf/gameServer.conf");
        cfg.setConfRecords(conf.parse());
		port = cfg.getPortNumber();
	}
	
	public static void sleep(int ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch(Exception e)
		{
			
		}
	}
	
	public TcpClient sndRequest(TcpClient tc, short cmd, Object...args)
	{
		if(tc == null) tc = new TcpClient(ip, port); 
		
		MsgEncoder p = new MsgEncoder(cmd);
		for(int i=0; i<args.length; i++)
		{
			if(args[i] instanceof Boolean)
			{
				p.addBoolean((Boolean)args[i]);
			}
			else if(args[i] instanceof Byte)
			{
				p.addByte((Byte)args[i]);
			}
			else if(args[i] instanceof Short)
			{
				p.addShort16((Short)args[i]);
			}
			else if(args[i] instanceof Integer)
			{
				p.addInt32((Integer)args[i]);
			}
			else if(args[i] instanceof Long)
			{
				p.addLong((Long)args[i]);
			}
			else if(args[i] instanceof Float)
			{
				p.addFloat((Float)args[i]);
			}
			else if(args[i] instanceof Double)
			{
				System.out.println("double: " + args[i]);
			}
			else if(args[i] instanceof String)
			{
				p.addString((String)args[i]);
			}
			else
			{
				return null;
			}
		}
		
		tc.send(p.getBytes());
		return tc;
	}
	
	public static boolean test_mysql_query()
	{
		String sql = "select * from t_user";
		List<t_user> data = MySQL.select(t_user.class, sql);
		System.out.println("Query all users: " + sql);
		for(int i=0; i<data.size(); i++)
		{
			data.get(i).debug();
		}
		return true;
	}
	
	public static boolean test_mysql_query(String name)
	{
		String sql = "select * from t_user where name = '" + name + "'";
		List<t_user> data = MySQL.select(t_user.class, sql);
		for(int i=0; i<data.size(); i++)
		{
			data.get(i).debug();
		}
		return true;
	}
	
	public static boolean test_mysql_insert()
	{
		System.out.println("Insert one new record to t_user: ");
		return MySQL.execute("insert into t_user (name, passwd, email) values ('%s', '%s', '%s')", "god", "123", "god@good.com");
	}
	
	public static boolean test_mysql_update()
	{
		System.out.println("Update password of user 'god' to 1234: ");
		return MySQL.execute("update t_user set passwd='%s' where name='%s'", "1234", "god");
	}
	
	public static boolean test_mysql_delete(String name)
	{
		System.out.printf("Delete user '%s': \n", name);
		return MySQL.execute("delete from t_user where name='%s'", name);
	}
	
	public static void test_mysql()
	{
		test_mysql_query();
		test_mysql_insert();
		test_mysql_query();
		test_mysql_update();
		test_mysql_query();
		test_mysql_delete("god");
		test_mysql_query();
	}
	
	public static boolean test_register(String email, String user, String passwd)
	{
		UnitTest ut = new UnitTest();
		
		ut.sndRequest(null, (short)1000, email, user, passwd).close();
		return true;
	}
	
	public static boolean test_login(String user, String passwd)
	{
		UnitTest ut = new UnitTest();
		
		ut.sndRequest(null, (short)1001, user, passwd).close();
		return true;
	}
	
	public static boolean test_logout(String user, String passwd) throws InterruptedException
	{
		UnitTest ut = new UnitTest();
		
		// first login
		TcpClient tc = ut.sndRequest(null, (short)1001, user, passwd);
		
		// sleep 2 seconds, logout
		sleep(2000);
		ut.sndRequest(tc, (short)1002).close();
		return true;
	}
	
	public static boolean test_quickplay(String user1, String passwd1, String user2, String passwd2) throws InterruptedException
	{
		UnitTest ut = new UnitTest();
		
		// first login
		TcpClient tc1 = ut.sndRequest(null, (short)1001, user1, passwd1);
		TcpClient tc2 = ut.sndRequest(null, (short)1001, user2, passwd2);
		
		// sleep 2 seconds, quickplay
		sleep(2000);
		ut.sndRequest(tc1, (short)1003);
		ut.sndRequest(tc2, (short)1003);
		return true;
	}
	
	public static boolean test_selectcommander(String user1, String passwd1, String user2, String passwd2, long commanderId1) throws InterruptedException
	{
		UnitTest ut = new UnitTest();
		
		// first login
		TcpClient tc1 = ut.sndRequest(null, (short)1001, user1, passwd1);
		TcpClient tc2 = ut.sndRequest(null, (short)1001, user2, passwd2);
		
		// sleep 1 seconds, quickplay
		sleep(1000);
		ut.sndRequest(tc1, (short)1003);
		ut.sndRequest(tc2, (short)1003);
		
		// sleep 1 seconds, select commander
		sleep(1000);
		ut.sndRequest(tc1, (short)1004, commanderId1);
		
		sleep(5000);
		tc1.close();
		tc2.close();
		return true;
	}
	
	static public void main(String[] args) throws IOException, SQLException, InterruptedException
	{
		int pause = 1000;
		
		boolean enable_test_mysql = false;
		boolean enable_test_register = false;
		boolean enable_test_login = false;
		boolean enable_test_logout = false;
		boolean enable_test_quickplay = false;
		boolean enable_test_selectcommander = true;
		
		/*------------------ test mysql -----------------*/
		// initialize mysql database(delete user joe and god)
		if(enable_test_mysql)
		{
			test_mysql_query();
			test_mysql_insert();
			test_mysql_update();
			test_mysql_query();
			test_mysql_delete("joe");
			test_mysql_delete("god");
			test_mysql_query();
			sleep(pause);
		}
			
		//*------------------ test register -----------------*/
		if(enable_test_register)
		{
			// invalid email
			test_register("", "lee", "123456");
			test_register("this is email", "lee", "123456");
			
			// invalid user name
			test_register("joe@gmail.com", "", "123456");
			
			// email already existed
			test_register("lee@hotmail.com", "joe", "123456");
			
			// user name already existed
			test_register("joe@hotmail.com", "lee", "123456");
			
			// password should not be empty
			test_register("joe@hotmail.com", "joe", "");
			
			// register ok
			test_register("god@hotmail.com", "god", "1234");
			sleep(pause);
		}

		/*------------------ test login -----------------*/
		if(enable_test_login)
		{
			// no suh a user
			test_login("joe", "123456");
			
			// wrong password
			test_login("lee", "123");
	
			// auth pass
			test_login("lee", "123456");
			sleep(pause);
		}
		
		/*------------------ test logout -----------------*/
		if(enable_test_logout)
		{
			test_logout("lily", "123456");
			sleep(pause);
		}
		
		/*------------------ test quickplay -----------------*/
		if(enable_test_quickplay)
		{
			test_quickplay("lee", "123456", "lily", "123456");
			sleep(pause);
		}
		
		/*------------------ test select commander -----------------*/
		if(enable_test_selectcommander)
		{
			test_selectcommander("lee", "123456", "lily", "123456", 1L);
			sleep(pause);
			//test_selectcommander("lee", "123456", "lily", "123456", 3L);
			//sleep(pause);
		}
	}
}
