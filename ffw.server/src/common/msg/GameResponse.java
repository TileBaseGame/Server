package common.msg;

// Java Imports
import java.lang.reflect.Field;

/**
 * The GameResponse class is an abstract class used as a basis for storing
 * response information.
 */
public abstract class GameResponse
{
    protected MsgEncoder buf; // Response information stored as bytes

    /**
     * Convert the response into bytes format.
     * 
     * @return the response as bytes
     */
    public byte[] constructResponseInBytes()
    {
    	return buf==null ? null : buf.getBytes();
    }

    @Override
    public String toString() 
    {
        String str = "";

        str += "-----" + "\n";
        str += getClass().getName() + "\n";
        str += "\n";

        for (Field field : getClass().getDeclaredFields()) {
            try {
                str += field.getName() + " - " + field.get(this) + "\n";
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        str += "-----";
        return str;
    }
}