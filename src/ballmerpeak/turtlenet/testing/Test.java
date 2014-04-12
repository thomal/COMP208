package ballmerpeak.turtlenet.testing;

import ballmerpeak.turtlenet.shared.Message;
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
        System.out.println("===========================================================================");
        System.out.println("Pass: " + passes + " Failures: " + failures + " Anomalies: " + anomalies);
        System.out.println("===========================================================================");
    }
    
    private static void test (int i, boolean v) {
        if (v) {
            System.out.println("\t" + i + " PASS");
            passes++;
        } else {
            System.out.println("\t" + i + " FAIL");
            failures++;
        }
    }
    
    private static boolean testMessageContructors() {
        System.out.println("testMessageConstructors:");
        int ifailures = failures;
        
        Message m = new Message("POST", "hello, world!\\_O_/", 4224, "<sig>");
        
        test (1, m.getCmd().equals("POST"));
        test (2, m.getSig().equals("<sig>"));
        test (3, m.getContent().equals("hello, world!\\_O_/"));
        test (4, m.getTimestamp() == 4224);
        
        
        return failures == ifailures;
    }
    
    private static boolean testMessageParsing() {
        System.out.println("\ntestMessageParsing:");
        int ifailures = failures;
        
        Message m = Message.parse("abcd\\efgh\\ijkl\\mnop\\4224");
        
        test (1, m.getCmd().equals("abcd"));
        test (2, m.getSig().equals("efgh"));
        test (3, m.getContent().equals("ijkl\\mnop"));
        test (4, m.getTimestamp() == 4224);
        
        
        return failures == ifailures;
    }
    
    private static boolean testMessageAccessors() {
        System.out.println("\ntestMessageAccessors:");
        int ifailures = failures;
        
        KeyPair k1 = Crypto.getTestKey();
        KeyPair k2 = Crypto.getTestKey();
        String    k1e = Crypto.encodeKey(k1.getPublic());
        String    k2e = Crypto.encodeKey(k2.getPublic());
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
        
        test(1, claimm.CLAIMgetName().equals("zero_cool"));
        
        test(2, revokem.REVOKEgetTime() == 2442);
        System.out.println("\tWARNING: Not testing Message::REVOKEgetKey()");
        anomalies++;
        
        String[][] pdvs = pdatam.PDATAgetValues();
        test(3, pdvs[0][0].equals("name"));
        test(4, pdvs[0][0].equals("John Doe"));
        test(5, pdvs[0][0].equals("dob"));
        test(6, pdvs[0][0].equals("1972"));
        
        test(7, chatm.CHATgetKeys()[0].equals(k1));
        test(8, chatm.CHATgetKeys()[1].equals(k2));
        
        test(9, pchatm.PCHATgetText().equals("Hi bob."));
        test(10, pchatm.PCHATgetConversationID().equals("<convsig>"));
        
        test(11, postm.POSTgetText().equals("Hello, World! \\_O_/"));
        
        //No FPOSTgetX() methods because FPOSTs are POSTs in disguise.
        
        test(12, cmntm.CMNTgetText().equals("nice"));
        test(13, cmntm.CMNTgetItemID().equals("<post or comment sig>"));
        
        test(14, likem.LIKEgetItemID().equals("<post or comment sig>"));
        
        test(15, eventm.EVNTgetName().equals("bobs birthday"));
        test(16, eventm.EVNTgetStart() == 0);
        test(17, eventm.EVNTgetEnd() == 60000);
        
        
        
        return failures == ifailures;
    }
}
