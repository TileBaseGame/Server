package common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Clazz<T>
{
	private Class<T> cls = null;
	private Field[] fields = null;
	private Method[] methods = null;
	
	public Clazz(Class<T> cls)
	{
		this.cls = cls;
		
		fields = cls.getDeclaredFields();
		methods = cls.getDeclaredMethods();
	}
	
	public T set(Map<String, String> data)
	{
		try
		{
			T t = (T)cls.newInstance();
			for(Field i : fields)
			{
				String type = i.getType().getSimpleName();
				String name = i.getName();
				String value = data.get(name);
				if(value == null)
					continue;
				
				i.setAccessible(true);
				switch(type)
				{
				case "short": try{i.set(t, Short.parseShort(value));}catch(Exception ex) {} break;
				case "int": try{i.set(t, Integer.parseInt(value));}catch(Exception ex) {} break;
				case "long": try{i.set(t, Long.parseLong(value));}catch(Exception ex) {} break;
				case "float": try{i.set(t, Float.parseFloat(value));}catch(Exception ex) {} break;
				case "double": try{i.set(t, Double.parseDouble(value));}catch(Exception ex) {} break;
				case "Date": try{i.set(t, date_from_str(value));}catch(Exception ex) {} break;
				case "String": try{i.set(t, value);}catch(Exception ex) {} break;
				}
	        }
			return t;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
    // convert date to string
    public static String date_to_str(Date date)
    {
        if(null != date)
        {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return f.format(date);
        }
        return null;
    }
    
    public static Date date_from_str(String date)
    {
        if(null != date && date.equals(""))
        {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try 
            {
				return f.parse(date);
			} 
            catch (ParseException e) 
            {
				e.printStackTrace();
			}
        }
        return null;
    }
}
