package main;

/**
 * The Constants class stores important variables as constants for later use.
 */
public class Constants 
{
	public final static short REQ_REGISTER = 1000;
	public final static short RSP_REGISTER = 9000;
	
    public final static short REQ_LOGIN = 1001;
    public final static short RSP_LOGIN = 9001;
    
    public final static short REQ_LOGOUT = 1002;
    public final static short RSP_LOGOUT = 9002;
    
    public final static short REQ_QUICKPLAY = 1003;
    public final static short RSP_QUICKPLAY = 9003;
    
    public final static short REQ_SET_COMMANDER = 1004;
    public final static short RSP_SET_COMMANDER = 9004;
    public final static short RSP_SET_OPPONENT_COMMANDER = 9104;
    
    public final static short REQ_SET_UNITS = 1005;
    public final static short RSP_SET_UNITS = 9005;
    public final static short RSP_SET_OPPONENT_UNITS = 9105;
   
    public final static short REQ_SET_UNITS_ABILITIES = 1006;
    public final static short RSP_SET_UNITS_ABILITIES = 9006;
    public final static short RSP_SET_OPPONENT_UNITS_ABILITIES = 9106;
    
    public final static short REQ_GAME_ACTION = 1007;
    public final static short RSP_GAME_ACTION = 9007;
    public final static short RSP_OPPONENT_GAME_ACTION = 9107;
    
    public final static short REQ_QUITPLAY = 1008;
    public final static short RSP_QUITPLAY = 9008;
    public final static short RSP_OPPONENT_QUITPLAY = 9108;

    // other parameters
    public static final int TIMEOUT_SECONDS = 90;
}
