package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class pool
{
	public static final int MAX_CONN = 10;
	public static pool instance = new pool();
	
	private DBConf cfg = null;
	private ArrayList<Connection> list = new ArrayList<Connection>();
	
	public pool()
	{
        synchronized(this)
		{
        	cfg = new DBConf();
            ConfParser fp = new ConfParser("conf/db.conf");
            cfg.setConfRecords(fp.parse());
            
	        do
	        {
	        	Connection conn = new_conn();
	        	if(conn != null)
	        	{
	        		list.add(conn);
	        	}
	        }while(list.size()<MAX_CONN);
		}
	}

	public void close()
	{
		synchronized(this)
		{
			for(int i=0; i<list.size(); i++)
			{
				Connection conn = list.get(i);
				if(conn != null)
				{
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			list.clear();
		}
	}
	
	public Connection get()
	{
		synchronized(this)
		{
			return list.size()>0 ? list.remove(0) : null;
		}
	}
	
	public void put(Connection conn)
	{
		synchronized(this)
		{
			list.add(conn);
		}
	}
	
	private Connection new_conn()
	{
	    final String driver = "com.mysql.jdbc.Driver";

	    String url = String.format("jdbc:mysql://%s/%s", cfg.getDBURL(), cfg.getDBName());
	    String usr = cfg.getDBUsername();
	    String passwd = cfg.getDBPassword();
	    
	    try 
	    {
	        Class.forName(driver);
	        // System.out.printf("MySQL connection: url=[%s], usr=[%s], passwd=[%s]\n", url, usr, passwd);
	        return DriverManager.getConnection(url, usr, passwd);
	    }
	    catch(Exception e)
	    {
	    	System.out.println("ERROR: failed to new connection to mysql: " + e.getMessage());
	    }
		return null;
	}
}

public class MySQL
{
	@SuppressWarnings("unchecked")
	public static <T> List<T> select(Class<T> cls, String sql, Object...args)
	{
		Connection conn = pool.instance.get();
		
		ArrayList<T> r = new ArrayList<T>();
        Statement stmt = null;
		try 
		{
			String q = String.format(sql, args);
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(q);
			if(rs != null)
			{
				ResultSetMetaData m = rs.getMetaData();
				int n = m.getColumnCount();
				
				Clazz<T> z = new Clazz<T>(cls);
				
				Map<String, String> data = new HashMap<String, String>();
				data.clear();
				while(rs.next())
		        {
					for(int i=1; i<=n; i++)
					{
						String col = m.getColumnName(i);
						data.put(col, rs.getString(i));
					}
					
					T t = z.set(data);
					if(t != null)
					{
						r.add(t);
					}
		        }
			}
		} 
		catch (Exception e) 
		{
			System.out.println("ERROR: failed to query data: " + e.getMessage());
			r.clear();
		}
		finally
		{
			pool.instance.put(conn);
			if(stmt != null)
			{
				try 
				{
					stmt.close();
				}
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
		}
		return r;
	}
	
	@SuppressWarnings("unchecked")
	public static boolean execute(String sql, Object...args)
	{
		Connection conn = pool.instance.get();
		
		PreparedStatement stmt = null;
		try 
		{
			stmt = conn.prepareStatement(String.format(sql, args));
			stmt.executeUpdate();
		} 
		catch (Exception e) 
		{
			System.out.println("ERROR: failed to insert data: " + e.getMessage());
			return false;
		}
		finally
		{
			pool.instance.put(conn);
			if(stmt != null)
			{
				try 
				{
					stmt.close();
				}
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}
		}
		return true;
	}
}
