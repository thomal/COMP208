package ballmerpeak.turtlenet.testing;

import ballmerpeak.turtlenet.shared.Message;

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
        System.out.println("\ntestMessageConstructors:");
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
        
        //setup
        
        //tests
        
        return failures == ifailures;
    }
}
