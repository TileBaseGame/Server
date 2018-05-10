package common;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TcpClient
{
	public String ip = "127.0.0.1";
	public int port = 8080;
	public Socket socket = null;
	
	public TcpClient(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
		
		connect();
	}
	
	public boolean connect()
	{
		close();
		
		try 
		{
			socket = new Socket(ip, port);
			return true;
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public void close()
	{
		if(socket != null)
		{
			if(!socket.isClosed())
			{
				try 
				{
					socket.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			socket = null;
		}
	}
	
	public void send(byte[] buf)
	{
		if(socket != null)
		{
			OutputStream os;
			try 
			{
				os = socket.getOutputStream();
				os.write(buf);
				os.flush();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public void recv(byte[] buf)
	{
		if(socket != null)
		{
			OutputStream os;
			try 
			{
				os = socket.getOutputStream();
				os.write(buf);
				os.flush();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
