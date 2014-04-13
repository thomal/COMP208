package ballmerpeak.turtlenet.server;

import java.io.*;

class Logger {
    static boolean started;
    static String path;
    static PrintWriter log;
    
    public static void init (String logfile) {
        path = logfile;
        
        try {
            PrintWriter log = new PrintWriter(new BufferedWriter(new FileWriter(path)));
        } catch (Exception e) {
            System.out.println("ERROR: Unable to open log");
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
