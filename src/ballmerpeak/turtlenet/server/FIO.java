//File IO

package ballmerpeak.turtlenet.server;
import java.io.*;
import ballmerpeak.turtlenet.server.Logger;

public class FIO {
    public static byte[] readFileBytes (String filename) {
        RandomAccessFile f = null;
        byte[] bytes = null;
        
        try {
            f = new RandomAccessFile(filename, "r");
            Long lsize = f.length();
            int  isize = (int)f.length();
            if (lsize == isize) {
                bytes = new byte[isize];
                f.readFully(bytes);
            } else {
                Logger.write("FATAL", "FIO", filename + " is too large, could not read file.");
            }
            f.close();
        } catch (IOException e) {
            Logger.write("FATAL", "FIO", "Could not read file: " + e);
            return bytes = null;
        }
                
        return bytes;
    }
    
    public static boolean writeFileBytes (byte[] data, String filename) {
        FileOutputStream out;
        try {
            out = new FileOutputStream(new File(filename));
            out.write(data);
            out.close();
            return true;
        } catch (IOException e) {
            Logger.write("FATAL", "FIO", "Could not write file: " + e);
            return false;
        }
    }
}
