import java.util.Vector;

class NetworkConnection {
    public NetworkConnection (String serverurl) {
        System.out.println("WARNING: Duumy network connection constructor");
        url = serverurl;
        
        //dummy
        messages.add("MSG 1");
        messages.add("MSG 2");
    }
    
    public void close () {
        System.out.println("WARNING: Dummy network connection close");
    }
    
    public Boolean hasMessage () {
        return messages.size() >= 1;
    }
    
    public String getMessage() {
        String m = messages.get(0);
        messages.removeElementAt(0);
        return m;
    }
    
    private String url;
    private Vector<String> messages;
}
