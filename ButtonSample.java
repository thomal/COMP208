package n2;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ButtonSample extends JFrame implements ActionListener {
	int x = 0;
    public ButtonSample() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 300);
        setLocation(300, 300);

        JButton button1 = new JButton("button1");
        button1.addActionListener(this);
        add(button1);

        setVisible(true);
    }

    public static void main(String[] args) {
        new ButtonSample();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();

        if (command.equals("button1")) {
        	
        	x++;
           
           if (x%2 == 0){
        	   myMethod1();
        	   System.out.println(x);
        	
           }
           else if(x%2 != 0 ){
        	   myMethod2();
        	   System.out.println(x);
           }
           if(x == 2){
        	   x = 0;
           }
           
        }
    }

    public void myMethod1() {
    	System.out.printf("myMethod1" );
    }
    public void myMethod2() {
    	System.out.printf("myMethod2" );
    }
}