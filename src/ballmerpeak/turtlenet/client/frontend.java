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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.Style.FontWeight;

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
	FlexTable friendsDetailsPanel = new FlexTable();

	// Reusable Panels
	FlowPanel inputPanel = new FlowPanel();
	FlowPanel outputPanel = new FlowPanel();
	HorizontalPanel navigationPanel = new HorizontalPanel();

	// Create panels that display controls for views
	FlowPanel commentsControlPanel = new FlowPanel();
	FlowPanel myWallControlPanel = new FlowPanel();
	FlowPanel friendsWallControlPanel = new FlowPanel();
	FlowPanel groupsControlPanel = new FlowPanel();
	FlowPanel messagesControlPanel = new FlowPanel();

	public void onModuleLoad() {
		// Call methods to set up panels
		loginPanelSetup();
		navigationPanelSetup();
		myWallControlPanelSetup();
		friendsWallControlPanelSetup();
		settingsPanelSetup();
		friendsListPanelSetup();
		messageListPanelSetup();
		myDetailsPanelSetup();
		inputPanelSetup();
		outputPanelSetup();
		groupsControlPanelSetup();
		myDetailsPanelSetup();
		messagesControlPanelSetup();
		commentsControlPanelSetup();

		// Call method to load the initial login page
		// loadLogin();

		// Call temporary constuction method
		loadPanelDev();
	}

	// #########################################################################
	// #########################################################################
	// ####################Setup panels needed to create views##################
	// #########################################################################
	// #########################################################################

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

		// Add style name for CSS
		loginPanel.addStyleName("gwt-login");

		// Add click handler for button
		loginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String usernameToServer = usernameInput.getText();

				if (!FieldVerifier.isValidName(usernameToServer)) {
					usernameLabel
							.setText("Please enter at least four characters:");
					return;
				} else
					loadMyWall();
			}
		});
	}

	private void navigationPanelSetup() {
		// Create navigation links
		Anchor linkMyWall = new Anchor("My Wall");
		Anchor linkMessages = new Anchor("Messages");
		Anchor linkFriends = new Anchor("Friends");
		Anchor linkSettings = new Anchor("Settings");
		Anchor linkLogout = new Anchor("Logout");

		// Add links to navigation panel
		navigationPanel.add(linkMyWall);
		navigationPanel.add(linkMessages);
		navigationPanel.add(linkFriends);
		navigationPanel.add(linkSettings);
		navigationPanel.add(linkLogout);

		// Add style name for CSS
		navigationPanel.addStyleName("gwt-navigation");

		// Add click handlers for anchors
		linkMyWall.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
					loadMyWall();
			}
		});
		
		linkMessages.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
					loadMessageList();
			}
		});
		
		linkFriends.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
					loadFriendsList();
			}
		});
		
		linkSettings.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
					loadSettings();
			}
		});
		
		linkLogout.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				/*
				 * Here we need to call a method which logs the user out
				 */

				// Take the user back to the login screen
				loadLogin();
			}
		});
	}

	private void friendsListPanelSetup() {
		// Column title for anchors linking to messages
		Label friendsNameLabel = new Label("Friend's Name");
		friendsNameLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		friendsNameLabel.getElement().getStyle().setProperty("paddingLeft" , "100px");
		friendsListPanel.setWidget(0, 0, friendsNameLabel);
		
		// Column title for labels outputing the date a message was recieved
		Label friendsKeyLabel = new Label("Friend's Public Key");
		friendsKeyLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		friendsListPanel.setWidget(0, 1, friendsKeyLabel);
	
		/*
		 * The number 10 in the following for loop should be replaced with the
		 * return from a method that queries the database to find out how many
		 * friends(public keys) the user has
		 */
		for (int i = 1; i <= 10; i++) {

			/*
			 * 'Integer.toString(i)' should be replaced by the public key of a
			 * friend. To find this call a method that returns the list of all
			 * of the users friends(hopefully as an array) and then use Variable
			 * i from the loop we are currently in to select a friends key from
			 * the array.
			 */
			final String friendKey = Integer.toString(i);

			/*
			 * Here "Friend's Name" should be replaced with a call to a method
			 * that takes a public key(give it friendKey) and returns a friend's
			 * name
			 */
			Anchor linkFriendsWall = new Anchor("Friend's Name");
			friendsListPanel.setWidget(i, 0, linkFriendsWall);
			
			// Display each friend's key next to their name 
			friendsListPanel.setWidget(i, 1, new Label(friendKey));

			// Add click handlers for anchors
			linkFriendsWall.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					loadFriendsWall(friendKey);
				}
			});
			
		}
		// Add style name for CSS
		friendsListPanel.addStyleName("gwt-friends-list");
	}

	private void messageListPanelSetup() {
		// Column title for anchors linking to messages
		Label messageRecievedFromLabel = new Label("Message recieved from");
		messageRecievedFromLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		messageRecievedFromLabel.getElement().getStyle().setProperty("paddingLeft" , "100px");
		messageListPanel.setWidget(0, 0, messageRecievedFromLabel);
		
		// Column title for labels outputing the date a message was recieved
		Label messageRecievedOnLabel = new Label("Message recieved on");
		messageRecievedOnLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		messageListPanel.setWidget(0, 1, messageRecievedOnLabel);
		
		/*
		 * The number 10 in the following for loop should be replaced with the
		 * return from a method that queries the database to find out how many
		 * messages have been sent to the user(every message ever if possible)
		 */
		for (int i = 1; i <= 10; i++) {
		
			/*
			 * 'Integer.toString(i)' should be replaced by the ID of a message. 
			 * To find this call a method that returns the list of all
			 * of the messages a user has recieved(hopefully as an array) and 
			 * then use Variable i from the loop we are currently in to select 
			 * a message ID from the array
			 */
			final String messageID = Integer.toString(i);
			
			/*
			 * 'Integer.toString(i)' should be replaced by the ID of the friend
			 * who sent the message to the current user.
			 * To find this call a method that returns the ID of the user who
			 * sent a message when given the ID of that message. Give it messageID.
			 */
			final String userID = Integer.toString(i);
			
			/*
			 * "Friend's Name" should be replaced with a call to a method that 
			 * returns a users name when given the ID of a user. Give it userID.
			 */			 
			Anchor linkMessageContents = new Anchor("Friend's Name");
			messageListPanel.setWidget(i, 0, linkMessageContents);
			
			/*
			 * '01/01/1970' should be replaced with a call to a method that
			 * returns the date a message was recieved when given the ID of a message.
			 * Give it messageID.
			 */
			Label displayMessageDate = new Label("01/01/1970 @ 00:00");
			messageListPanel.setWidget(i, 1, displayMessageDate);
			
			// Add click handlers for anchors
			linkMessageContents.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					loadMessageContents(messageID);
				}
			});
		}
		// Add style name for CSS
		messageListPanel.addStyleName("gwt-message-list");
	}

	private void myDetailsPanelSetup() {
		// Create widgets

		// Add widgets to panel

		// Add style name for CSS
		myDetailsPanel.addStyleName("gwt-my-details");
	}
	
	private void friendsDetailsPanelSetup() {
		// Create widgets

		// Add widgets to panel

		// Add style name for CSS
		friendsDetailsPanel.addStyleName("gwt-friends-details");

		// Add click handlers for anchors

	}

	private void inputPanelSetup() {
		// Create widgets

		// Add widgets to panel

		// Add style name for CSS
		inputPanel.addStyleName("gwt-input");
	}

	private void outputPanelSetup() {
		// Create widgets

		// Add widgets to panel

		// Add style name for CSS
		outputPanel.addStyleName("gwt-output");
	}

	private void myWallControlPanelSetup() {
		// Create widgets
		Anchor linkMyDetails = new Anchor("My details");

		// Add widgets to panel
		myWallControlPanel.add(linkMyDetails);

		// Add style name for CSS
		myWallControlPanel.addStyleName("gwt-my-wall-control");

		// Add click handlers for anchors

	}
	
	private void friendsWallControlPanelSetup() {
		// Create widgets

		// Add widgets to panel

		// Add style name for CSS
		friendsWallControlPanel.addStyleName("gwt-friends-wall-control");

		// Add click handlers for anchors
	}

	private void groupsControlPanelSetup() {
		// Create widgets

		// Add widgets to panel

		// Add style name for CSS
		groupsControlPanel.addStyleName("gwt-groups-control");

		// Add click handlers for anchors

	}


	private void messagesControlPanelSetup() {
		// Create widgets

		// Add widgets to panel

		// Add style name for CSS
		messagesControlPanel.addStyleName("gwt-messages-control");

		// Add click handlers for anchors

	}

	private void commentsControlPanelSetup() {
		// Create widgets

		// Add widgets to panel

		// Add style name for CSS
		commentsControlPanel.addStyleName("gwt-comments-control");

		// Add click handlers for anchors

	}

	private void settingsPanelSetup() {
		// Create widgets

		// Add widgets to panel

		// Add style name for CSS
		settingsPanel.addStyleName("gwt-settings-panel");

		// Add click handlers for anchors

	}

	// #########################################################################
	// #########################################################################
	// ############################Load different view##########################
	// #########################################################################
	// #########################################################################

	private void loadPanelDev() {
		// Add all panels to page
		RootPanel.get().add(loginPanel);
		RootPanel.get().add(settingsPanel);
		RootPanel.get().add(friendsListPanel);
		RootPanel.get().add(messageListPanel);
		RootPanel.get().add(myDetailsPanel);
		RootPanel.get().add(inputPanel);
		RootPanel.get().add(outputPanel);
		RootPanel.get().add(navigationPanel);
		RootPanel.get().add(commentsControlPanel);
		RootPanel.get().add(myWallControlPanel);
		RootPanel.get().add(friendsWallControlPanel);
		RootPanel.get().add(groupsControlPanel);
		RootPanel.get().add(myDetailsPanel);
		RootPanel.get().add(messagesControlPanel);
	}

	private void loadLogin() {
		// Clear page
		RootPanel.get().clear();
		// Add login panel to page
		RootPanel.get().add(loginPanel);
	}

	private void loadMyWall() {
		// Clear page
		RootPanel.get().clear();
		// Add navigation to page
		RootPanel.get().add(navigationPanel);
		RootPanel.get().add(outputPanel);
		RootPanel.get().add(myWallControlPanel);

		// Some kind of way of accepting other peoples posts on your wall
	}

	private void loadFriendsWall(String friendKey) {

	}

	private void loadMyDetails() {

	}

	private void loadFriendsDetails() {

	}

	private void loadComments() {

	}

	private void loadMessageList() {

	}

	private void loadMessageContents(String messageID) {

	}

	private void loadFriendsList() {

	}

	private void loadAddKey() {

	}

	private void loadGroups() {

	}

	private void loadEditGroups() {

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
