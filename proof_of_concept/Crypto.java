//All methods ought to be static
import java.io.*;
import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;
import java.util.StringTokenizer;

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
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(Crypto.getPrivateKey());
            sig.update(msg.getBytes());
            byte[] bytesig = sig.sign();
            return Base64Encode(bytesig);
        } catch (Exception e) {
            System.out.println("ERROR: Could not sign message");
        }
        return "";
    }
    
    public static boolean verifySig (Message msg, PublicKey author) {
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(author);
            sig.update(msg.getContent().getBytes());
            return sig.verify(Base64Decode(msg.getSig()));
        } catch (Exception e) {
            System.out.println("ERROR: Could not verify signature");
        }
        return false;
    }
    
    public static String encrypt(String cmd, String text, PublicKey recipient) {
        try {
            //sign and encrypt
            Message msg = new Message(cmd, text, Crypto.sign(text));
            
            //encrypt with random AES key
            System.out.println("WARNING: AES not using a random key or iv");
            String password        = "1234567890123456";
            String iv              = "0345750576243763";
            SecretKeySpec aesKey   = new SecretKeySpec(password.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, aesKey, ivspec);
            byte[] aesCipherText = aes.doFinal(msg.toString().getBytes());
            
            //encrypt AES key with RSA
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, recipient);
            byte[] RSAaesKey = rsa.doFinal(password.getBytes());
            
            //"iv\RSA encrypted AES key\ciper text"
            return Base64Encode(iv.getBytes()) + "\\" + Base64Encode(RSAaesKey) + "\\" + Base64Encode(aesCipherText);
        } catch (Exception e) {
            System.out.println("WARNING: Unable to encrypt message: " + e);
        }
        return "";
    }
    
    public static Message decrypt(String msg) {
        try {
            String[] tokens = new String[3];
            StringTokenizer tokenizer = new StringTokenizer(msg, "\\", false);
            tokens[0] = tokenizer.nextToken();
            tokens[1] = tokenizer.nextToken();
            tokens[2] = tokenizer.nextToken();
        
            String iv            = new String(Base64Decode(tokens[0]));
            byte[] cipheredKey   = Base64Decode(tokens[1]);
            byte[] cipherText    = Base64Decode(tokens[2]);
            
            //decrypt AES key
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.DECRYPT_MODE, getPrivateKey());
            byte[] aesBytePassword = rsa.doFinal(cipheredKey);
            
            //decrypt AES Ciphertext
            SecretKeySpec aesKey = new SecretKeySpec(aesBytePassword, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, aesKey, ivspec);
            byte[] messagePlaintext = aes.doFinal(cipherText);

            return Message.parse(new String(messagePlaintext));
        } catch (Exception e) {
            System.out.println("WARNING: Unable to decrypt message: " + e);
        }
        return new Message("NULL", "", "");
    }
    
    public static String Base64Encode (byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }
    
    public static byte[] Base64Decode (String data) {
        return DatatypeConverter.parseBase64Binary(data);
    }
    
    public static String hash (String data) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");        
            return DatatypeConverter.printHexBinary(hasher.digest(data.getBytes()));
        } catch (Exception e) {
            System.out.println("SHA-256 isn't supported.");
        }
        return "not_a_hash";
    }
}
