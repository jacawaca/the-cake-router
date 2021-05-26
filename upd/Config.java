package cakeRouter;

/**
 * @author Jacek Strzałkowski jacek.strzalkowski.stud@pw.edu.pl
 *
 */

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Config {
    public static final int PORT = 9000;
    public static final int BUFFER_SIZE = 1024;
    public static final InetAddress MULTICAST_ADDRESS;
    public static final int MULTICAST_PORT = 9000;
    static {
        try{
            MULTICAST_ADDRESS = InetAddress.getByName("239.255.42.99");
        }catch (UnknownHostException e){
            throw new RuntimeException(e);
        }
    }
}