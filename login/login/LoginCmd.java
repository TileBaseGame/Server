package login;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;

public class LoginCmd {
	
	 	static String sql = null;  
	    static JDBCUtils db1 = null;  
	    static ResultSet ret = null;  
	  
	    public static boolean login(String username,String password) {
			 sql = "select * from Players WHERE user_name = "+username+" AND password = "+password;//SQL语句  
		     db1 = new JDBCUtils(sql);//创建DBHelper对象  
		     boolean isExist = false;
		     try {  
		         ret = db1.pst.executeQuery();//执行语句，得到结果集  
		         /*while (ret.next()) {  
		             String play_id = ret.getString(1);  
		             String ufname = ret.getString(2);  
		             String ulname = ret.getString(3);  
		             String udate = ret.getString(4);  
		         }//显示数据          */
		         isExist = ret.first();
		         ret.close();  
		         db1.close();//关闭连接  
		     } catch (SQLException e) {  
		         e.printStackTrace();  
		     }  
		     return isExist;
	    }
	    
	    
	    public static boolean regsiter(String username,String password,String email) {
			 sql = "INSERT INTO player (username,password,email) VALUES ('"+username+"','"+password+"','"+email+"')";
		     db1 = new JDBCUtils(sql);//创建DBHelper对象  
		     boolean isExist = false;
		     try {  
		         ret = db1.pst.executeQuery();//执行语句，得到结果集  
		         /*while (ret.next()) {  
		             String play_id = ret.getString(1);  
		             String ufname = ret.getString(2);  
		             String ulname = ret.getString(3);  
		             String udate = ret.getString(4);  
		         }//显示数据          */
		         ret.close();  
		         db1.close();//关闭连接  
		         isExist = true;
		     } catch (SQLException e) {  
		         e.printStackTrace();  
		     }  
		     return isExist;
	    }
}
