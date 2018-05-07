package main;

/**
 * The Constants class stores important variables as constants for later use.
 */
public class Constants {

    // Request(1xxx)
	public final static short REQ_REGISTER = 1000;
    public final static short REQ_LOGIN = 1001;
    public final static short REQ_LOGOUT = 1002;
    public final static short REQ_QUICKPLAY = 1003;
    public final static short REQ_SELECT_COMMANDER = 1004;
    
    // Response(9xxx)
	public final static short RSP_REGISTER = 9000;
    public final static short RSP_LOGIN = 9001;
    public final static short RSP_LOGOUT = 9002;
    public final static short RSP_QUICKPLAY = 9003;
    public final static short RSP_SELECT_COMMANDER = 9004;

    
    // other parameters
    public static final int TIMEOUT_SECONDS = 90;
}
