package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;

public class frontend implements EntryPoint{

    /**
     * Create a remote service proxy to talk to the server-side Turtlenet service.
     */
    private final TurtlenetAsync turtlenet = GWT.create(Turtlenet.class);
    
    //Create panels
    FlexTable loginPanel = new FlexTable();
    HorizontalPanel navigationPanel = new HorizontalPanel();
    VerticalPanel sidebarPanel = new VerticalPanel();

    public void onModuleLoad() {
    	loginPanelSetup();   
		navigationPanelSetup();
        sidebarPanelSetup();
        
        //Add style name to panels for CSS
        navigationPanel.addStyleName("gwt-navigation");
        loginPanel.addStyleName("gwt-login");
	}
	
	private void loginPanelSetup() {
		//Create login panel widgets
		final Button loginButton = new Button("Login");
        final TextBox usernameInput = new TextBox();
        final Label usernameLabel = new Label();
		
		//Setup widgets
		usernameInput.setText("");
        usernameLabel.setText("Please enter your username:");
		
		//Add widgets to login panel
        loginPanel.setWidget(1, 1, usernameLabel);
        loginPanel.setWidget(2, 1, usernameInput);
        loginPanel.setWidget(3, 1, loginButton);
        //Add login panel to page
        RootPanel.get().add(loginPanel);
        
        //This happens when the user tries to login
		loginButton.addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			String usernameToServer = usernameInput.getText();
    		
				if (!FieldVerifier.isValidName(usernameToServer)) {
                    usernameLabel.setText("Please enter at least four characters:");
                    return;
                }
                else loadWall();
    		}
		});
	}
	
	private void navigationPanelSetup() {
		//Create navigation links
		Anchor linkWall = new Anchor("Wall");
		Anchor linkMessages = new Anchor("Messages");
		Anchor linkFriends = new Anchor("Friends");
		Anchor linkEvents = new Anchor("Events");

        //Add links to navigation panel
        navigationPanel.add(linkWall);
        navigationPanel.add(linkMessages);
        navigationPanel.add(linkFriends);
        navigationPanel.add(linkEvents);
	}
	
	private void sidebarPanelSetup() {
		//Add some widgets and stuff		
	}
	
	private void loadWall() {
		//Clear page
		RootPanel.get().clear();
		//Add navigation to page
        RootPanel.get().add(navigationPanel);
	}
	
}
