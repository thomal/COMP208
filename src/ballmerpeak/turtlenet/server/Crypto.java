//All methods ought to be static
package ballmerpeak.turtlenet.server;

import ballmerpeak.turtlenet.server.FIO;
import ballmerpeak.turtlenet.shared.Message;
import java.io.*;
import java.security.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;
import java.util.StringTokenizer;
import java.security.SecureRandom;

public class Crypto {
    public static SecureRandom srand = new SecureRandom(
                                               Long.toString(
                                                   System.currentTimeMillis())
                                               .getBytes());

    /** Check whether the user has a keypair.
     * \return true is the user has a keypair, false otherwise.
     */
    public static Boolean keysExist() {
        File publicKey  = new File(Database.path + "/public.key");
        File privateKey = new File(Database.path + "/private.key");
        return publicKey.exists() && privateKey.exists();
    }
    
    /** Generate an RSA keypair.
     * Stores the keys in Database.path + public/private .key
     * They are automatically encrypted by TNClient.
     */
    public static void keyGen() {
        try {
            Logger.write("INFO", "Crypto","Generating keys");
            
            //generate the key
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(1024, srand);
            KeyPair keys = gen.generateKeyPair();
            
            //create the DB directory if needed
            if (!Database.DBDirExists())
                Database.createDBDir();
            
            //and save the keys into it
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
            Logger.write("ERROR", "Crypto", "Could not generate keypair");
        }
    }
    
    /** Encrypt local data store.
     * encrypts all files in the db folder with AES-128. Renames files to <filename>.aes
     * \param password The password used to derive the AES key.
     * \return true of success, false otherwise.
     */
    public static boolean encryptDB(String password) {
        Logger.write("VERBOSE", "Crypto", "encryptDB(" + password + ")");
        try {
            String salt = Long.toString(System.currentTimeMillis());
            password += salt;
            FIO.writeFileBytes(salt.getBytes("UTF-8"), Database.path + "/salt");
            FIO.writeFileBytes(encryptBytes(FIO.readFileBytes(Database.path + "/turtlenet.db"), password+"db"), Database.path + "/turtlenet.db.aes");
            FIO.writeFileBytes(encryptBytes(FIO.readFileBytes(Database.path + "/public.key"), password+"pu"), Database.path + "/public.key.aes");
            FIO.writeFileBytes(encryptBytes(FIO.readFileBytes(Database.path + "/private.key"), password+"pr"), Database.path + "/private.key.aes");
            FIO.writeFileBytes(encryptBytes(FIO.readFileBytes(Database.path + "/lastread"), password+"lr"), Database.path + "/lastread.aes");
            new File(Database.path + "/turtlenet.db").delete();
            new File(Database.path + "/public.key").delete();
            new File(Database.path + "/private.key").delete();
            new File(Database.path + "/lastread").delete();
        } catch (Exception e) {
            Logger.write("FATAL", "Crypto", "Unable to encrypt files: " + e);
            return false;
        }
        return true;
    }
    
    /** Decrypt local data store.
     * Decrypts all files in the db folder with AES-128. Removes the trailing .aes from the filename
     * \param password The password used to derive the AES key.
     * \return true of success, false otherwise.
     */
    public static boolean decryptDB(String password) {
        Logger.write("VERBOSE", "Crypto", "decryptDB(" + password + ")");
        try {
            password += new String(FIO.readFileBytes(Database.path + "/salt"));
            FIO.writeFileBytes(decryptBytes(FIO.readFileBytes(Database.path + "/turtlenet.db.aes"), password+"db"), Database.path + "/turtlenet.db");
            FIO.writeFileBytes(decryptBytes(FIO.readFileBytes(Database.path + "/public.key.aes"), password+"pu"), Database.path + "/public.key");
            FIO.writeFileBytes(decryptBytes(FIO.readFileBytes(Database.path + "/private.key.aes"), password+"pr"), Database.path + "/private.key");
            FIO.writeFileBytes(decryptBytes(FIO.readFileBytes(Database.path + "/lastread.aes"), password+"lr"), Database.path + "/lastread");
            new File(Database.path + "/turtlenet.db.aes").delete();
            new File(Database.path + "/public.key.aes").delete();
            new File(Database.path + "/private.key.aes").delete();
            new File(Database.path + "/lastread.aes").delete();
            new File(Database.path + "/salt").delete();
        } catch (Exception e) {
            Logger.write("FATAL", "Crypto", "Unable to decrypt files: " + e);
            return false;
        }
        return false;
    }
    
    /** Get a keypair for testing purposes.
     * \param bar baz.
     * \return An RSA KeyPair
     */
    public static KeyPair getTestKey() {
        Logger.write("INFO", "Crypto","Generating test keypair");
        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(1024, srand);
            return gen.generateKeyPair();
        } catch (Exception e) {
            Logger.write("ERROR", "Crypto", "Couldn't generate test keypair: " + e);
            return null;
        }
    }
    
    /** Get the current users public key.
     * \return The current users PublicKey.
     */
    public static PublicKey getPublicKey() {
        try {
            ObjectInputStream file = new ObjectInputStream(
                                     new FileInputStream(
                                     new File("./db/public.key")));
            return (PublicKey) file.readObject();
        } catch (Exception e) {
            Logger.write("WARNING", "Crypto", "Could not read public key");
        }
        return null;
    }
    
    /** Get the current users private key.
     * \return The current users PrivateKey
     */
    public static PrivateKey getPrivateKey() {
        try {
            ObjectInputStream file = new ObjectInputStream(
                                     new FileInputStream(
                                     new File("./db/private.key")));
            return (PrivateKey) file.readObject();
        } catch (Exception e) {
            Logger.write("WARNING", "Crypto", "Could not read private key");
        }
        return null;
    }
    
    /** Generate the appropriate signature for a message.
     * Returns the appropriate signature for the given message as a string.
     * Signature is created using the current users private key.
     * \param msg The message to sign.
     * \return A signature of msg by the current users private key.
     */
    public static String sign (Message msg) {
        Logger.write("INFO", "Crypto","sign()");
        return sign(msg, Crypto.getPrivateKey());
    }
    
    /** Generate the appropriate signature for a message.
     * Returns the appropriate signature for the given message as a string.
     * Signature is created using the given key.
     * \param msg The message to sign.
     * \param k The key to sign with.
     * \return A signature of msg by k.
     */
    public static String sign (Message msg, PrivateKey k) {
        Logger.write("INFO", "Crypto","sign()");
        try {
            Signature signer = Signature.getInstance("SHA1withRSA");
            signer.initSign(k);
            signer.update((Long.toString(msg.timestamp) + msg.content).getBytes("UTF-8"));
            byte[] sig = signer.sign();
            return Crypto.Base64Encode(sig);
        } catch (Exception e) {
            Logger.write("ERROR", "Crypto", "Could not sign message");
        }
        return "";
    }
    
    /** Hash a string.
     * Uses SHA-256.
     * \param data The text to hash.
     * \return The SHA-256 hash of the data.
     */
    public static String hash (String data) {
        try {
            MessageDigest hasher = MessageDigest.getInstance("SHA-256");        
            return DatatypeConverter.printHexBinary(hasher.digest(data.getBytes("UTF-8")));
        } catch (Exception e) {
            Logger.write("FATAL", "DB","SHA-256 not supported by your JRE");
        }
        return "not_a_hash";
    }
    
    /** Verify a signature.
     * \param msg The message that's been signed.
     * \param author The key suspected to be the author of the signature.
     * \return true is author signed msg, false otherwise.
     */
    public static boolean verifySig (Message msg, PublicKey author) {
        Logger.write("INFO", "Crypto","verifySig()");
        try {
            Signature sigChecker = Signature.getInstance("SHA1withRSA");
            sigChecker.initVerify(author);
            sigChecker.update((Long.toString(msg.getTimestamp())+msg.getContent()).getBytes("UTF-8"));
            boolean valid = sigChecker.verify(Crypto.Base64Decode(msg.getSig()));
            if (valid) {
                Logger.write("INFO", "Crypto","verifySig() - TRUE");
            } else {
                Logger.write("INFO", "Crypto","verifySig() - FALSE");
            }
            return valid;
        } catch (Exception e) {
            Logger.write("ERROR", "Crypto", "Could not verify signature");
        }
        return false;
    }
    
    /** Encrypt a message.
     * Uses an RSA header with the main ciphertext being AES encrypted. There
     * are significant speed advantages to this given most modern CPUs have
     * AES as part of the instruction set.
     *
     * Message is encrypted using the current users private key.
     *
     * Time differentials can, and have, been used to corrolate otherwise
     *  anonymous messages; therefore server time is used. This is not to
     *  protect against malicious server operators, but operators ordered after
     *  the fact to provide the data they've collected.
     *
     * The NetworkConnection is used to get the servers time.
     * \param msg The message to encrypt.
     * \param recipient The public key that should be used to encrypt the message.
     * \param connection A network connection to the server you intend to send
     *    this message to. Used to get a timestamp.
     * \return The encrypted message as a string.
     */
    public static String encrypt(Message msg, PublicKey recipient, NetworkConnection connection) {
        try {
            Logger.write("INFO", "Crypto","encrypt()");
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
            return Crypto.Base64Encode(iv) + "\\" + Crypto.Base64Encode(encryptedAESKey) + "\\" +
                   Crypto.Base64Encode(aesCipherText);
        } catch (Exception e) {
            Logger.write("WARNING", "Crypto", "Unable to encrypt message: " + e);
        }
        return "";
    }
    
    /** Decrypt a message.
     * Uses an RSA header with the main ciphertext being AES encrypted. There
     * are significant speed advantages to this given most modern CPUs have
     * AES as part of the instruction set.
     *
     * Message is decrypted using the current users private key.
     *
     * \param msg The message to decryot.
     * \return The decrypted message.
     */
    public static Message decrypt(String msg) {
        Logger.write("INFO", "Crypto","decrypt()");
        try {
            //claim messages are the only plaintext in the system, still need decoding
            if (msg.substring(0,2).equals("c ")) {
                String decoding = new String(Crypto.Base64Decode(msg.substring(2)));
                return Message.parse(decoding);
            }
        
            String[] tokens = new String[3];
            StringTokenizer tokenizer = new StringTokenizer(msg, "\\", false);
            tokens[0] = tokenizer.nextToken();
            tokens[1] = tokenizer.nextToken();
            tokens[2] = tokenizer.nextToken();
        
            byte[] iv            = Crypto.Base64Decode(tokens[0]);
            byte[] cipheredKey   = Crypto.Base64Decode(tokens[1]);
            byte[] cipherText    = Crypto.Base64Decode(tokens[2]);
            
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
            //Logger.write("WARNING", "Crypto", "Unable to decrypt message: " + e);
        }
        return new Message("NULL", "", 0, "");
    }
    
    /** Encode a PublicKey as a String.
     * \param key The PublicKey to encode.
     * \return A string representation of key.
     */
    public static String encodeKey (PublicKey key) {
        if (key != null) {
            return Base64Encode(key.getEncoded());
        } else {
            Logger.write("ERROR", "Crypto","encodeKey passed null key");
            return "--INVALID KEYSTRING--";
        }
    }
    
    /** Decode a string representation of a PublicKey.
     * \param codedKey The encoded public key to decode.
     * \return The PublicKey encoded in codedKey.
     */
    public static PublicKey decodeKey (String codedKey) {
        if (codedKey != null) {
            try {
                return KeyFactory.getInstance("RSA").generatePublic(
                                    new X509EncodedKeySpec(Base64Decode(codedKey)));
            } catch (Exception e) {
                Logger.write("ERROR", "Crypto", "decodeKey(" + codedKey + ") passed invalid keystring");
                return null;
            }
        }
        Logger.write("WARNING", "Crypto", "decodeKey(...) returning null - passed invalid keystring");
        return null;
    }
    
    /** Encode a byte[] as a string.
     * Uses base64 encoding.
     * \param data The byte array to encode.
     * \return A string representation of data.
     */
    public static String Base64Encode (byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }
    
    /** Decode a string representation of a byte[]
     * Uses base64 encoding.
     * \param data The string to decode.
     * \return The byte[] data represents.
     */
    public static byte[] Base64Decode (String data) {
        return DatatypeConverter.parseBase64Binary(data);
    }
    
    /** Get a random number.
     * \param min The minimum value to return, inclusive.
     * \param max The maximum value to return, exclusive.
     * \return a random integer between min and max (inclusive.)
     */
    public static int rand (int min, int max) {
        int range = max - min;
        return (int)(Math.random() * (range + 1)) + min;
    }
    
    /** AES Encrypt data.
     * AES encrypts a byte[] using AES and a key derived from key.
     * \param data The data to encrypt.
     * \param key The string from which the key is derived.
     * \return data encrypted using AES.
     */
    public static byte[] encryptBytes (byte[] data, String key) {
        try {
            SecretKeySpec spec = new SecretKeySpec(getAESKey(key), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, spec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            Logger.write("FATAL", "Crypto", "Could not encrypt bytes: " + e);
            return null;
        }
    }
    
    /** Decrypt AES data.
     * Decrypts AES encrypted byte[]s using a key derived from key.
     * \param data The data to decrypt.
     * \param key The string from which the key is derived.
     * \return decrypted data.
     */
    public static byte[] decryptBytes (byte[] data, String key) {
        try {
            SecretKeySpec spec = new SecretKeySpec(getAESKey(key), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, spec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            Logger.write("FATAL", "Crypto", "Could not decrypt bytes: " + e);
            return null;
        }
    }
    
    /** Derives a byte[] suitable for constructing an AES key from a string.
     * \param password The string from which to derive a byte[].
     * \return A byte[] suitable for constructing an AES key.
     */
    private static byte[] getAESKey(String password) {
        try {
            byte[] pwBytes = password.getBytes("UTF-8");
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            SecureRandom srandAES = SecureRandom.getInstance("SHA1PRNG");
            srandAES.setSeed(pwBytes);
            gen.init(128, srandAES);
            SecretKey key = gen.generateKey();
            return key.getEncoded();
        } catch (Exception e) {
            Logger.write("FATAL", "Crypto", "Could not get AES key: " + e);
            return null;
        }
    }
}
