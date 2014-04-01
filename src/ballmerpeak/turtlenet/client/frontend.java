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
import com.google.gwt.user.client.ui.FlowPanel;

public class frontend implements EntryPoint {

	// Create remote service proxy to talk to the server-side Turtlenet service
	// private final TurtlenetAsync turtlenet = GWT.create(Turtlenet.class);

	// Create panels that have only one use
	FlexTable loginPanel = new FlexTable();
	HorizontalPanel settingsPanel = new HorizontalPanel();

	// Create panels that display lists of things
	FlexTable friendsListPanel = new FlexTable();
	FlexTable messageListPanel = new FlexTable();
	FlexTable myDetailsPanel = new FlexTable();

	// Reusable Panels
	FlowPanel inputPanel = new FlowPanel();
	FlowPanel outputPanel = new FlowPanel();
	HorizontalPanel navigationPanel = new HorizontalPanel();

	// Create panels that display controls for views
	FlowPanel commentsControlPanel = new FlowPanel();
	FlowPanel postsControlPanel = new FlowPanel();
	FlowPanel groupsControlPanel = new FlowPanel();
	FlowPanel editDetailsControlPanel = new FlowPanel();
	FlowPanel messagesControlPanel = new FlowPanel();

	public void onModuleLoad() {
		// Call methods to set up panels
		loginPanelSetup();
		navigationPanelSetup();
		postsControlPanelSetup();
		settingsPanelSetup();
		friendsListPanelSetup();
		messageListPanelSetup();
		myDetailsPanelSetup();
		inputPanelSetup();
		outputPanelSetup();
		groupsControlPanelSetup();
		editDetailsControlPanelSetup();
		messagesControlPanelSetup();
		commentsControlPanelSetup();

		// Call method to load the initial login page
		// loadLogin();
		loadPanelDev();
	}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
//////////////////////Setup panels needed to create views///////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

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

		// Add style name to loginPanel for CSS
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
					loadMyPosts();
			}
		});
	}

	private void settingsPanelSetup() {
		settingsPanel.addStyleName("gwt-settings-panel");
	}

	private void navigationPanelSetup() {
		// Create navigation links
		Anchor linkMyPosts = new Anchor("Posts");
		Anchor linkProfile = new Anchor("Profile");
		Anchor linkMessages = new Anchor("Messages");
		Anchor linkFriends = new Anchor("Friends");
		Anchor linkSettings = new Anchor("Settings");
		Anchor linkLogout = new Anchor("Logout");

		// Add links to navigation panel
		navigationPanel.add(linkMyPosts);
		navigationPanel.add(linkProfile);
		navigationPanel.add(linkMessages);
		navigationPanel.add(linkFriends);
		navigationPanel.add(linkSettings);
		navigationPanel.add(linkLogout);

		// Add style name to navigationPanel for CSS
		navigationPanel.addStyleName("gwt-navigation");
	}

	private void friendsListPanelSetup() {
		friendsListPanel.addStyleName("gwt-friends-list");
	}

	private void messageListPanelSetup() {
		messageListPanel.addStyleName("gwt-message-list");
	}

	private void myDetailsPanelSetup() {
		myDetailsPanel.addStyleName("gwt-my-details");
	}

	private void inputPanelSetup() {
		inputPanel.addStyleName("gwt-input");
	}

	private void outputPanelSetup() {
		outputPanel.addStyleName("gwt-output");
	}

	private void postsControlPanelSetup() {
		// Add style name to post panel for CSS
		postsControlPanel.addStyleName("gwt-posts-control");
	}

	private void groupsControlPanelSetup() {
		groupsControlPanel.addStyleName("gwt-groups-control");
	}

	private void editDetailsControlPanelSetup() {
		editDetailsControlPanel.addStyleName("gwt-edit-details");
	}

	private void messagesControlPanelSetup() {
		messagesControlPanel.addStyleName("gwt-messages-control");
	}

	private void commentsControlPanelSetup() {
		commentsControlPanel.addStyleName("gwt-comments-control");
	}

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
/////////////////////////////Load different views///////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

	private void loadPanelDev() {
		// load all panels
		RootPanel.get().add(loginPanel);
		RootPanel.get().add(settingsPanel);
		RootPanel.get().add(friendsListPanel);
		RootPanel.get().add(messageListPanel);
		RootPanel.get().add(myDetailsPanel);
		RootPanel.get().add(inputPanel);
		RootPanel.get().add(outputPanel);
		RootPanel.get().add(navigationPanel);
		RootPanel.get().add(commentsControlPanel);
		RootPanel.get().add(postsControlPanel);
		RootPanel.get().add(groupsControlPanel);
		RootPanel.get().add(editDetailsControlPanel);
		RootPanel.get().add(messagesControlPanel);
	}

	private void loadLogin() {
		// Clear page
		RootPanel.get().clear();
		// Add login panel to page
		RootPanel.get().add(loginPanel);
	}

	private void loadMyPosts() {
		// Clear page
		RootPanel.get().clear();
		// Add navigation to page
		RootPanel.get().add(navigationPanel);
		RootPanel.get().add(outputPanel);
		RootPanel.get().add(postsControlPanel);
	}

	private void loadFriendsPosts() {

	}

	private void loadMyDetails() {

	}

	private void loadFriendsDetails() {

	}

	private void loadComments() {

	}

	private void loadMessageList() {

	}

	private void loadMessageContents() {

	}

	private void loadFriendsList() {

	}

	private void loadAddKey() {

	}

	private void loadGroups() {

	}

	private void loadEditGroups() {

	}

	private void loadEditDetails() {

	}

	private void loadSettings() {

	}

	private void loadCreatePost() {

	}

	private void loadCreateComment() {

	}

	private void loadCreateMessages() {

	}

}
