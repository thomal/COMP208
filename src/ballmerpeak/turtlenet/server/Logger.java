package ballmerpeak.turtlenet.server;

import java.io.*;

class Logger {
    static boolean started = false;
    static String path;
    static PrintWriter log;
    
    public static void init (String logfile) {
        started = true;
        path = logfile;
        
        try {
            log = new PrintWriter(new BufferedWriter(new FileWriter(path)));
        } catch (Exception e) {
            throw new RuntimeException("ERROR: Unable to open log: " + e);
        }
    }
    
    public static void close () {
        if(started)
            log.close();
    }
    
    public static void write (String s) {
        if (started) {
            log.println(s);
            log.flush(); //In case of a crash we don't want to be digging up the wrong code
        }
    }
}
