package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.FieldVerifier;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlowPanel;

public class frontend implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Turtlenet
	 * service.
	 */
	//private final TurtlenetAsync turtlenet = GWT.create(Turtlenet.class);

	// Create panels
	FlexTable loginPanel = new FlexTable();
	HorizontalPanel navigationPanel = new HorizontalPanel();
	FlowPanel postPanel = new FlowPanel();

	public void onModuleLoad() {
		//Call methods to set up panels
		loginPanelSetup();
		navigationPanelSetup();
		postPanelSetup();
		//Call method to load the initial login page
		loadLogin();
	}

	private void loginPanelSetup() {
		// Create login panel widgets
		final Button loginButton = new Button("Login");
		final TextBox usernameInput = new TextBox();
		final Label usernameLabel = new Label();

		// Setup widgets
		usernameInput.setText("");
		usernameLabel.setText("Please enter your username:");

		// Add widgets to login panel
		loginPanel.setWidget(1, 1, usernameLabel);
		loginPanel.setWidget(2, 1, usernameInput);
		loginPanel.setWidget(3, 1, loginButton);
		
		//Add style name to loginPanel for CSS
		loginPanel.addStyleName("gwt-login");
		
		// This happens when the user tries to login
		loginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String usernameToServer = usernameInput.getText();

				if (!FieldVerifier.isValidName(usernameToServer)) {
					usernameLabel
							.setText("Please enter at least four characters:");
					return;
				} else
					loadWall();
			}
		});
	}

	private void navigationPanelSetup() {
		// Create navigation links
		Anchor linkWall = new Anchor("Wall");
		Anchor linkProfile = new Anchor("Profile");
		Anchor linkMessages = new Anchor("Messages");
		Anchor linkFriends = new Anchor("Friends");
		Anchor linkSettings = new Anchor("Settings");
		Anchor linkLogout = new Anchor("Logout");

		// Add links to navigation panel
		navigationPanel.add(linkWall);
		navigationPanel.add(linkProfile);
		navigationPanel.add(linkMessages);
		navigationPanel.add(linkFriends);
		navigationPanel.add(linkSettings);
		navigationPanel.add(linkLogout);
		
		//Add style name to navigationPanel for CSS
		navigationPanel.addStyleName("gwt-navigation");
	}
	
	private void postPanelSetup() {
		//Add username
		//Add post contents
		//Add view 'comments' link
		
		//Add style name to post panel for CSS
		postPanel.addStyleName("gwt-post");
	}
	
	private void commentsPanelSetup() {
	
	}
	
	private void loadLogin() {
		// Clear page
		RootPanel.get().clear();
		// Add login panel to page
		RootPanel.get().add(loginPanel);
	}

	private void loadWall() {
		// Clear page
		RootPanel.get().clear();
		// Add navigation to page
		RootPanel.get().add(navigationPanel);
		RootPanel.get().add(postPanel);
	}
	
	private void loadComments () {
	
	}
	
	private void loadMessageList () {
	
	}
	
	private void loadMessageContents () {
	
	}
	
	private void loadFriends () {
	
	}
	
	private void loadGorups () {
	
	}
	
	private void loadEditGroups () {
	
	}
	
	private void loadSettings () {
	
	}
	
	private void loadAddKey () {
	
	}

}
