package ballmerpeak.turtlenet.testing;

import ballmerpeak.turtlenet.shared.Message;
import ballmerpeak.turtlenet.server.MessageFactoryImpl;
import ballmerpeak.turtlenet.server.Crypto;
import java.security.*;

class Test {
    static int passes    = 0;
    static int failures  = 0;
    static int anomalies = 0;
    
    public static void main (String[] argv) {
        System.out.println("=====Testing===============================================================");
        testMessageContructors();
        testMessageParsing();
        testMessageAccessors();
        testMessageFactory();
        testCrypto();
        System.out.println("===========================================================================");
        System.out.println("Pass: " + passes + " Failures: " + failures + " Anomalies: " + anomalies);
        System.out.println("===========================================================================");
    }
    
    private static void test (String i, boolean v) {
        if (v) {
            System.out.println("\t" + i + getTabs(i) + "PASS");
            passes++;
        } else {
            System.out.println("\t" + i + getTabs(i) + "FAIL");
            failures++;
        }
    }
    
    private static void test (String i, String answer, String expected) {
        if (answer.equals(expected)) {
            System.out.println("\t" + i + getTabs(i) + "PASS");
            passes++;
        } else {
            System.out.println("\t" + i + getTabs(i) + "FAIL");
            System.out.println("\t\tGot: " + answer);
            System.out.println("\t\tExpected: " + expected);
            failures++;
        }
    }
    
    private static void test (String i, long answer, long expected) {
        if (answer == expected) {
            System.out.println("\t" + i + getTabs(i) + "PASS");
            passes++;
        } else {
            System.out.println("\t" + i + getTabs(i) + "FAIL");
            System.out.println("\t\tGot: " + answer);
            System.out.println("\t\tExpected: " + expected);
            failures++;
        }
    }
    
    private static String getTabs (String s) {
        int tabs = 0;
        String ts = "";
        
        if (s.length() < 8)
            tabs = 4;
        else if (s.length() < 16)
            tabs = 3;
        else if (s.length() < 24)
            tabs = 2;
        else if (s.length() < 32)
            tabs = 1;
        else
            tabs = 0;
        
        while (tabs-- > 0)
            ts += "\t";
        return ts;
    }
    
    private static boolean testMessageContructors() {
        System.out.println("testMessageConstructors:");
        int ifailures = failures;
        
        Message m = new Message("POST", "hello, world!\\_O_/", 4224, "<sig>");
        
        test ("1 getCmd"      , m.getCmd(),      "POST");
        test ("2 getSig"      , m.getSig(),      "<sig>");
        test ("3 getContent"  , m.getContent(),  "hello, world!\\_O_/");
        test ("4 getTimestamp", m.getTimestamp(), 4224);
        
        
        return failures == ifailures;
    }
    
    private static boolean testMessageParsing() {
        System.out.println("\ntestMessageParsing:");
        int ifailures = failures;
        
        Message m = Message.parse("abcd\\efgh\\ijkl\\mnop\\4224");
        
        test ("1 getCmd"      , m.getCmd(),       "abcd");
        test ("2 getSig"      , m.getSig(),       "efgh");
        test ("3 getContent"  , m.getContent(),   "ijkl\\mnop");
        test ("4 getTimestamp", m.getTimestamp(),  4224);
        
        
        return failures == ifailures;
    }
    
    private static boolean testMessageAccessors() {
        System.out.println("\ntestMessageAccessors:");
        int ifailures = failures;
        
        KeyPair k1 = Crypto.getTestKey();
        KeyPair k2 = Crypto.getTestKey();
        String  k1e = Crypto.encodeKey(k1.getPublic());
        String  k2e = Crypto.encodeKey(k2.getPublic());
        Message claimm  = new Message("CLAIM",  "zero_cool",                  4224, "<sig>");
        Message revokem = new Message("REVOKE", "2442",                       4224, "<sig>");
        Message pdatam  = new Message("PDATA",  "name:John Doe;dob:1972;",    4224, "<sig>");
        Message chatm   = new Message("CHAT",   k1e + ":" + k2e,              4224, "<sig>");
        Message pchatm  = new Message("PCHAT",  "<convsig>:Hi bob.",          4224, "<sig>");
        Message postm   = new Message("POST",   "Hello, World! \\_O_/",       4224, "<sig>");
        Message fpostm  = new Message("FPOST",  "I'm posting on your wall",   4224, "<sig>");
        Message cmntm   = new Message("CMNT",   "<post or comment sig>:nice", 4224, "<sig>");
        Message likem   = new Message("LIKE",   "<post or comment sig>",      4224, "<sig>");
        Message eventm  = new Message("EVNT",   "0:60000:bobs birthday",      4224, "<sig>");
        
        test("1  CLAIMgetName", claimm.CLAIMgetName(), "zero_cool");
        
        test("2  REVOKEgetTime", revokem.REVOKEgetTime(), 2442);
        
        String[][] pdvs = pdatam.PDATAgetValues();
        test("3  PDATAgetValues", pdvs[0][0], "name");
        test("4  PDATAgetValues", pdvs[0][1], "John Doe");
        test("5  PDATAgetValues", pdvs[1][0], "dob");
        test("6  PDATAgetValues", pdvs[1][1], "1972");
        
        test("7  CHATgetKeys", Crypto.decodeKey(chatm.CHATgetKeys()[0]).equals(k1.getPublic()));
        test("8  CHATgetKeys", Crypto.decodeKey(chatm.CHATgetKeys()[1]).equals(k2.getPublic()));
        
        test("9  PCHATgetText",  pchatm.PCHATgetText(),           "Hi bob.");
        test("10 PCHATgetConversationID", pchatm.PCHATgetConversationID(), "<convsig>");
        
        test("11 POSTgetText", postm.POSTgetText(), "Hello, World! \\_O_/");
        
        //No FPOSTgetX() methods because FPOSTs are POSTs in disguise.
        
        test("12 CMNTgetText",   cmntm.CMNTgetText(),   "nice");
        test("13 CMNTgetItemID", cmntm.CMNTgetItemID(), "<post or comment sig>");
        
        test("14 LIKEgetItemID", likem.LIKEgetItemID(), "<post or comment sig>");
        
        test("15 EVNTgetName",  eventm.EVNTgetName(), "bobs birthday");
        test("16 EVNTgetStart", eventm.EVNTgetStart(), 0);
        test("17 EVNTgetEnd",   eventm.EVNTgetEnd(),   60000);
        
        
        return failures == ifailures;
    }
    
    private static boolean testMessageFactory() {
        System.out.println("\ntestMessageFactory:");
        int ifailures = failures;
        
        MessageFactoryImpl f = new MessageFactoryImpl();
        KeyPair k1 = Crypto.getTestKey();
        KeyPair k2 = Crypto.getTestKey();
        PublicKey keys[] = {k1.getPublic(), k2.getPublic()};
        String  k1e = Crypto.encodeKey(k1.getPublic());
        String  k2e = Crypto.encodeKey(k2.getPublic());
        Message claimm  = f.newCLAIM("zero_cool");
        Message revokem = f.newREVOKE(2442);
        Message pdatam  = f.newPDATA("name", "John Doe");
        Message chatm   = f.newCHAT(keys);
        Message pchatm  = f.newPCHAT("<convsig>", "Hi bob.");
        Message postm   = f.newPOST("Hello, World! \\_O_/");
        Message fpostm  = f.newFPOST("I'm posting on your wall");
        Message cmntm   = f.newCMNT("<post or comment sig>", "nice");
        Message likem   = f.newLIKE("<post or comment sig>");
        Message eventm  = f.newEVNT(0, 60000, "bobs birthday");
        
        test("1  newCLAIM", claimm.CLAIMgetName(), "zero_cool");
        
        test("2  newREVOKE", revokem.REVOKEgetTime(), 2442);
        
        String[][] pdvs = pdatam.PDATAgetValues();
        test("3  newPDATA", pdvs[0][0], "name");
        test("4  newPDATA", pdvs[0][1], "John Doe");
        
        test("5  newCHAT", Crypto.decodeKey(chatm.CHATgetKeys()[0]).equals(k1.getPublic()));
        test("6  newCHAT", Crypto.decodeKey(chatm.CHATgetKeys()[1]).equals(k2.getPublic()));
        
        test("7  newPCHAT",  pchatm.PCHATgetText(),           "Hi bob.");
        test("8  newPCHAT", pchatm.PCHATgetConversationID(), "<convsig>");
        
        test("9  newPOST", postm.POSTgetText(), "Hello, World! \\_O_/");
        
        //No FPOSTgetX() methods because FPOSTs are POSTs in disguise.
        
        test("10 newCMNT",   cmntm.CMNTgetText(),   "nice");
        test("11 newCMNT", cmntm.CMNTgetItemID(), "<post or comment sig>");
        
        test("12 newLIKE", likem.LIKEgetItemID(), "<post or comment sig>");
        
        test("13 newEVNT",  eventm.EVNTgetName(), "bobs birthday");
        test("14 newEVNT", eventm.EVNTgetStart(), 0);
        test("15 newEVNT",   eventm.EVNTgetEnd(),   60000);
 
        System.out.println("\tWARNING: Doesn't test signature or timestamp generation");
        anomalies++;
        
        
        return failures == ifailures;
    }
    
    private static boolean testCrypto() {
        System.out.println("\ntestCrypto:");
        int ifailures = failures;
        
        KeyPair k1 = Crypto.getTestKey();
        
        test("1 key = decode(encode(key))", k1.getPublic().equals(Crypto.decodeKey(Crypto.encodeKey(k1.getPublic()))));
        
        System.out.println("\tWARNING: Not nearly enough crypto tests");
        anomalies++;              
        
        
        return failures == ifailures;
    }
}