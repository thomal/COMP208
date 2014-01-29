import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;

class Hasher {
    public static String hash (String data) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");        
            return DatatypeConverter.printHexBinary(hasher.digest(data.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("SHA-256 isn't supported.");
        }
        return "not_a_hash";
    }
}
