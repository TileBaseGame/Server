package models;

public class t_user 
{
	public long id;
	public String email;
	public String name;
	public String passwd;
	
	public void debug()
	{
		System.out.printf("  id=%d, email=%s, name=%s, passwd=%s\n", id, email, name, passwd);
	}

}
