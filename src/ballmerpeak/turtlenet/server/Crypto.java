//All methods ought to be static
package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.shared.Message;
import java.io.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;
import java.util.StringTokenizer;
import java.security.SecureRandom;

class Crypto {
    public static SecureRandom srand = new SecureRandom(
                                               Long.toString(
                                                   System.currentTimeMillis())
                                               .getBytes());

    public static Boolean keysExist() {
        File publicKey  = new File("./db/public.key");
        File privateKey = new File("./db/private.key");
        return publicKey.exists() && privateKey.exists();
    }
    
    public static void keyGen() {
        try {
            //generate the key
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(1024, srand);
            KeyPair keys = gen.generateKeyPair();
            
            //and save it
            ObjectOutputStream publicKeyFile = new ObjectOutputStream(
                                                   new FileOutputStream(
                                                       new File("./db/public.key")));
            publicKeyFile.writeObject(keys.getPublic());
            publicKeyFile.close();
            
            ObjectOutputStream privateKeyFile = new ObjectOutputStream(
                                                    new FileOutputStream(
                                                        new File("./db/private.key")));
            privateKeyFile.writeObject(keys.getPrivate());
            privateKeyFile.close();
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
            Signature signer = Signature.getInstance("SHA1withRSA");
            signer.initSign(Crypto.getPrivateKey());
            signer.update(msg.getBytes("UTF-8"));
            byte[] sig = signer.sign();
            return Base64Encode(sig);
        } catch (Exception e) {
            System.out.println("ERROR: Could not sign message");
        }
        return "";
    }
    
    public static boolean verifySig (Message msg, PublicKey author) {
        try {
            Signature sigChecker = Signature.getInstance("SHA1withRSA");
            sigChecker.initVerify(author);
            sigChecker.update(msg.getContent().getBytes("UTF-8"));
            return sigChecker.verify(Base64Decode(msg.getSig()));
        } catch (Exception e) {
            System.out.println("ERROR: Could not verify signature");
        }
        return false;
    }
    
    //Time differentials can, and have, been used to corrolate otherwise
    //  anonymous messages; therefore server time is used. This is not to
    //  protect against malicious server operators, but operators ordered after
    //  the fact to provide the data they've collected.
    //The NetworkConnection is used to get the servers time.
    public static String encrypt(String cmd, String text, PublicKey recipient,
                                 NetworkConnection connection) {
        try {
            //sign and encrypt
            //Latency could be used to locate the client if they interact with
            //  multiple malicious servers, or one server in multiple locations
            Message msg = new Message(cmd, text,
                     connection.getTime()+Crypto.rand(0,50), Crypto.sign(text));
            
            //encrypt with random AES key
            byte[]     iv = new byte[16];
            byte[] aeskey = new byte[16];
            srand.nextBytes(iv); //fills the array with random data
            srand.nextBytes(aeskey);
            
            SecretKeySpec aesKeySpec = new SecretKeySpec(aeskey, "AES");
            IvParameterSpec IVSpec   = new IvParameterSpec(iv);
            
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, aesKeySpec, IVSpec);
            byte[] aesCipherText = aes.doFinal(msg.toString().getBytes("UTF-8"));
            
            //encrypt AES key with RSA
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.ENCRYPT_MODE, recipient);
            byte[] encryptedAESKey = rsa.doFinal(aeskey);
            
            //"iv\RSA encrypted AES key\ciper text"
            return Base64Encode(iv) + "\\" + Base64Encode(encryptedAESKey) + "\\" + Base64Encode(aesCipherText);
        } catch (Exception e) {
            System.out.println("WARNING: Unable to encrypt message: " + e);
        }
        return "";
    }
    
    public static Message decrypt(String msg) {
        try {
            //claim messages are the only plaintext in the system, still need decoding
            if (msg.substring(0,2).equals("c ")) {
                String decoding = new String(Base64Decode(msg.substring(2)));
                return Message.parse(decoding);
            }
        
            String[] tokens = new String[3];
            StringTokenizer tokenizer = new StringTokenizer(msg, "\\", false);
            tokens[0] = tokenizer.nextToken();
            tokens[1] = tokenizer.nextToken();
            tokens[2] = tokenizer.nextToken();
        
            byte[] iv            = Base64Decode(tokens[0]);
            byte[] cipheredKey   = Base64Decode(tokens[1]);
            byte[] cipherText    = Base64Decode(tokens[2]);
            
            //decrypt AES key
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.DECRYPT_MODE, getPrivateKey());
            byte[] aesKey = rsa.doFinal(cipheredKey);
            
            //decrypt AES Ciphertext
            SecretKeySpec aesKeySpec = new SecretKeySpec(aesKey, "AES");
            IvParameterSpec IVSpec = new IvParameterSpec(iv);
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, aesKeySpec, IVSpec);
            byte[] messagePlaintext = aes.doFinal(cipherText);

            return Message.parse(new String(messagePlaintext));
        } catch (Exception e) {
            //This is to be expected for messages not addressed to you
            //System.out.println("WARNING: Unable to decrypt message: " + e);
        }
        return new Message("NULL", "", 0, "");
    }
    
    public static String Base64Encode (byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }
    
    public static byte[] Base64Decode (String data) {
        return DatatypeConverter.parseBase64Binary(data);
    }
    
    public static String encodeKey (PublicKey key) {
        return Base64Encode(key.getEncoded());
    }
    
    public static PublicKey decodeKey (String codedKey) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(
                                new X509EncodedKeySpec(Base64Decode(codedKey)));
        } catch (Exception e) {
            System.out.println("ERROR: Could not decode key");
        }
        return null;
    }
    
    public static String hash (String data) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");        
            return DatatypeConverter.printHexBinary(hasher.digest(data.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("SHA-256 isn't supported.");
        }
        return "not_a_hash";
    }
    
    public static int rand (int min, int max) {
        int range = max - min;
        return (int)(Math.random() * (range + 1)) + min;
    }
}
