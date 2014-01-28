//All methods ought to be static
import java.io.*;
import java.security.*;
import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

class Crypto {    
    public static Boolean keysExist() {
        File puk = new File("./db/public.key");
        File prk = new File("./db/private.key");
        return puk.exists() && prk.exists();
    }
    
    public static void keyGen() {
        try {
            //generate the key
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(1024);
            KeyPair key = gen.generateKeyPair();
            
            //and save it
            ObjectOutputStream pukfile = new ObjectOutputStream(
                                        new FileOutputStream(
                                        new File("./db/public.key")));
            pukfile.writeObject(key.getPublic());
            pukfile.close();
            
            ObjectOutputStream prkfile = new ObjectOutputStream(
                                        new FileOutputStream(
                                        new File("./db/private.key")));
            prkfile.writeObject(key.getPrivate());
            prkfile.close();
        } catch (Exception e) {
            System.out.println("ERROR: Could not generate keypair");
        }
    }
    
    public static PublicKey getPublicKey() {
        try {
            ObjectInputStream file = new ObjectInputStream(
                                     new FileInputStream(
                                     new File("./db/public.key")));
            return (PublicKey) file.readObject();
        } catch (Exception e) {
            System.out.println("WARNING: Could not read public key");
        }
        return null;
    }
    
    public static PrivateKey getPrivateKey() {
        try {
            ObjectInputStream file = new ObjectInputStream(
                                     new FileInputStream(
                                     new File("./db/private.key")));
            return (PrivateKey) file.readObject();
        } catch (Exception e) {
            System.out.println("WARNING: Could not read private key");
        }
        return null;
    }
    
    public static String sign (String msg) {
        System.out.println("WARNING: Dummy sign method");
        return msg;
    }
    
    public static String encrypt(String msg, PublicKey recipient) {
        try {
            //sign and encrypt
            msg = Crypto.sign(msg);
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, recipient);
            byte[] cipherText = rsa.doFinal(msg.getBytes());
            return Base64Encode(cipherText);
        } catch (Exception e) {
            System.out.println("WARNING: Unable to encrypt message");
        }
        return "";
    }
    
    public static String decrypt(String msg) {
        try {
            byte[] cipherText = Base64Decode(msg);
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.DECRYPT_MODE, getPrivateKey());
            byte[] plainText = rsa.doFinal(cipherText);
            return new String(plainText);
        } catch (Exception e) {
            System.out.println("WARNING: Unable to decrypt message: " + e);
        }
        return "";
    }
    
    public static String Base64Encode (byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }
    
    public static byte[] Base64Decode (String data) {
        return DatatypeConverter.parseBase64Binary(data);
    }
}
