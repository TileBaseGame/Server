package main;

// Java Imports
import java.util.HashMap;
import java.util.Map;

import common.*;
import common.msg.*;

/**
 * The ServiceTable class stores a mapping of unique request code numbers
 * with its corresponding request class.
 */
public class ServiceTable 
{
    private static Map<Short, Class<?>> dict = new HashMap<Short, Class<?>>(); // Request Code -> Class

    /**
     * Initialize the hash map by populating it with request codes and classes.
     */
    public static void init() 
    {
    	add(Constants.REQ_REGISTER, "Register");
        add(Constants.REQ_LOGIN, "Login");
        add(Constants.REQ_LOGOUT, "Logout");
        add(Constants.REQ_QUICKPLAY, "QuickPlay");
        add(Constants.REQ_SET_COMMANDER, "SetCommander");
        add(Constants.REQ_SET_UNITS, "SetUnits");
        add(Constants.REQ_SET_UNITS_ABILITIES, "SetUnitsAbilities");
        add(Constants.REQ_GAME_ACTION, "GameAction");
        add(Constants.REQ_QUITPLAY, "QuitPlay");
    }

    /**
     * Map the request code number with its corresponding request class, derived
     * from its class name using reflection, by inserting the pair into the
     * table.
     *
     * @param code a value that uniquely identifies the request type
     * @param name a string value that holds the name of the request class
     */
    public static void add(short code, String name) 
    {
        try 
        {
            dict.put(code, Class.forName("services." + name));
        } 
        catch(ClassNotFoundException e) 
        {
            Log.println_e(e.getMessage());
        }
    }

    /**
     * Get the instance of the request class by the given request code.
     *
     * @param cmd a value that uniquely identifies the request type
     * @return the instance of the request class
     */
    public static GameRequest get(short cmd) 
    {
        GameRequest req = null;
        try 
        {
            Class<?> cls = dict.get(cmd);
            if (cls != null)
            {
            	// new the instance for the very request
            	req = (GameRequest) cls.newInstance();
            } 
            else 
            {
                Log.printf_e("Request cmd[%d] does not exist.\n", cmd);
            }
        } 
        catch (Exception e) 
        {
            Log.println_e(e.getMessage());
        }
        return req;
    }
}
