/* Louis:
I've added one method and a new interface. The first is an example of how to
call Database functions and returns all posts by the user "john_doe", the new
new interface constructs Messages with a correct timestamp and hash. This second
one will be useful, the first one is meant to serve as a demonstration. Be aware
that if you want to use a class it has to be purged of all non-GWT-compatible
code. Because of this it's better to just add more methods to the interface than
call them directly. Below is how you would actually call the demo database
method:

turtlenet.demoDBCall(new AsyncCallback<String>() {
    public void onFailure(Throwable caught) {
        //code to execute on failure
    }

    public void onSuccess(Message[] results) {
        //code to execute on success
        //update the interface with new data or something here
    }
});

Because these are asynchronous they don't block, i.e. the next line of code
immediatly executes before the call is finished. For this reason I would
reccomend having the onSuccess method in the callback do the real work of
modifying the interface, and have the click event only trigger the asyncronous
function. You can add to the DB by calling c.db.addX methods, just like
demoDBCall.

You also can't use the PublicKey class. The TurtlenetImpl class can use anything
it likes, so you should return Crypto.encodeKey(key) instead of trying to return
the keys the DB gives you. Similarly for functions adding to the DB, you should
pass them a string (you don't have any other choice actually) and pass
Crypto.decodeKey(key) to the DB function.

Crypto.decodeKey :: String -> PublicKey
Crypto.encodeKey :: PublicKey -> String

You can't make an interface for the Database class directly, so you have to be
like the example above and have loads of one-line functions that just call the
corresponding DB function. This sucks, but I can't see a way around it because
the DB cannot be purged of GWT-incompatible code.

Oh, and conversaions/posts/comments are identified by their signature. So when
you like something then you pass the signature from whatever you're liking to
the DB.
*/

package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.Message;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.dom.client.Style.FontWeight;

public class frontend implements EntryPoint {

	// Create remote service proxy to talk to the server-side Turtlenet service
	private final TurtlenetAsync turtlenet = GWT.create(Turtlenet.class);
	//private final TurtlenetAsync msgfactory = GWT.create(MessageFactory.class);

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
	        /*Connect to turtlenet server*/
	        turtlenet.startTN(new AsyncCallback<String>() {
	                              public void onFailure(Throwable caught) {
	                                  //pretend nothing happened
	                              }
	                              public void onSuccess(String result) {
	                                  //bask in success
	                              }
	                          });
	        /* Add handler for window closing */
	        Window.addCloseHandler(new CloseHandler<Window>() {
                    public void onClose(CloseEvent<Window> event) {
                        turtlenet.stopTN(new AsyncCallback<String>() {
	                    public void onFailure(Throwable caught) {
	                        //pretend nothing happened
	                    }
	                    public void onSuccess(String result) {
	                        //bask in success
	                    }
	                });
                    }
                });


        //Call methods to set up panels
		loginPanelSetup();
		navigationPanelSetup();
		myWallControlPanelSetup();
		friendsWallControlPanelSetup();
		settingsPanelSetup();
		messageListPanelSetup();
		myDetailsPanelSetup();
		inputPanelSetup();
		outputPanelSetup();
		myDetailsPanelSetup();
		messagesControlPanelSetup();
		commentsControlPanelSetup();
		/*
		 * "publicKey" here should be replaced with the key of the friend's 
		 * details we want to look up
		 */
		friendsDetailsPanelSetup("publicKey");

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
				if (usernameInput.getText().length()<4) {
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
					loadFriendsList("All");
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

	private void friendsListPanelSetup(String currentGroupID) {
		// Column title for anchors linking to messages		
		Label friendsNameLabel = new Label("Friend's Name");
		friendsNameLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		friendsNameLabel.getElement().getStyle().setProperty("paddingLeft" , "100px");
		friendsListPanel.setWidget(1, 0, friendsNameLabel);
		
		// Column title for labels outputing the date a message was recieved
		Label friendsKeyLabel = new Label("Friend's Public Key");
		friendsKeyLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		friendsListPanel.setWidget(1, 1, friendsKeyLabel);
	
		/*
		 * The number 10 in the following for loop should be replaced with the
		 * return from a method that takes a group ID and returns the number of
		 * friends(public keys) the user has in the current group. 
		 * Give it currentGroupID. If currentGroupID is "All" then it should 
		 * return the number of all of the current users friends
		 */
		for (int i = 1; i <= 10; i++) {

			/*
			 * 'Integer.toString(i)' should be replaced by the public key of a
			 * friend. To find this call a method that takes a groups ID and 
			 * returns the list of all of the users friend's keys in that group,
			 * hopefully as an array. Give it currentGroupID. 
			 * If currentGroupID = "All" then it should return the list of all
			 * of the current users friend's keys. 
			 * Use Variable i from the loop we are currently in to select an 
			 * individual friend's key from the array.
			 */
			final String friendKey = Integer.toString(i);

			/*
			 * Here "Friend's Name" should be replaced with a call to a method
			 * that takes a public key(give it friendKey) and returns a friend's
			 * name
			 */
			Anchor linkFriendsWall = new Anchor("Friend's Name");
			friendsListPanel.setWidget((i + 1), 0, linkFriendsWall);
			
			// Display each friend's key next to their name 
			friendsListPanel.setWidget((i + 1), 1, new Label(friendKey));

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
		// Create widgets relating to username
		Label usernameLabel = new Label("Username:");
		myDetailsPanel.setWidget(0, 0, usernameLabel);
		
		final TextBox editUsername = new TextBox();
		/*
		 * "han" should be replaced with a call to a method that returns the 
		 * username of the current user.
		 * https://www.youtube.com/watch?v=xat70fI7Tag
		 */
		editUsername.setText("han");
		myDetailsPanel.setWidget(0, 1, editUsername);
		
		Button saveUsername = new Button("Save Username");
		myDetailsPanel.setWidget(0, 2, saveUsername);
		
		saveUsername.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				/*
				 * Here should lie a call to a method that takes a String and
				 * adds it to the database replacing the current user's username.
				 * Give it editUsername.getText()
				 */
			}
		});
		
		// Create widgets relating to name
		Label nameLabel = new Label("Name:");
		myDetailsPanel.setWidget(1, 0, nameLabel);
		
		final TextBox editName = new TextBox();
		/*
		 * "John Hancock" should be replaced with a call to a method that returns 
		 * the name of the current user.
		 */
		editName.setText("John Hancock");
		myDetailsPanel.setWidget(1, 1, editName);
		
		Button saveName = new Button("Save Name");
		myDetailsPanel.setWidget(1, 2, saveName);
		
		saveName.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				/*
				 * Here should lie a call to a method that takes a String and
				 * adds it to the database replacing the current user's name.
				 * Give it editName.getText()
				 */
			}
		});		
		
		
		// Create widgets relating to birthday
		Label birthdayLabel = new Label("Birthday:");
		myDetailsPanel.setWidget(2, 0, birthdayLabel);
		
		final TextBox editBirthday = new TextBox();
		/*
		 * "01011970" should be replaced with a call to a method that returns 
		 * the birthday of the current user.
		 */
		editBirthday.setText("01/01/1970");
		myDetailsPanel.setWidget(2, 1, editBirthday);
		
		Button saveBirthday = new Button("Save Birthday");
		myDetailsPanel.setWidget(2, 2, saveBirthday);
		
		saveBirthday.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				/*
				 * Here should lie a call to a method that takes a String and
				 * adds it to the database replacing the current user's birthday.
				 * Give it editBirthday.getText(). If the database requires a
				 * number then you'll have to convert it first as gwt text boxes
				 * give you strings. 
				 * You can use Integer.parseInt(editBirthday.getText())
				 */
			}
		});	


		// Create widgets relating to gender
		// Gender shouldn't be chosen from a list. The user should be able to 
		// write whatever they want. This way people who are trans gender are
		// satisfied(Simply having an 'Other' option is usually not enough
		Label genderLabel = new Label("Gender:");
		myDetailsPanel.setWidget(3, 0, genderLabel);
		
		final TextBox editGender = new TextBox();
		/*
		 * "Male" should be replaced with a call to a method that returns 
		 * the gender of the current user.
		 */
		editGender.setText("Male");
		myDetailsPanel.setWidget(3, 1, editGender);
		
		Button saveGender = new Button("Save Gender");
		myDetailsPanel.setWidget(3, 2, saveGender);
		
		saveGender.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				/*
				 * Here should lie a call to a method that takes a String and
				 * adds it to the database replacing the current user's gender.
				 * Give it editGender.getText()
				 */
			}
		});	
		
		// Create widgets relating to email
		Label emailLabel = new Label("Email:");
		myDetailsPanel.setWidget(4, 0, emailLabel);
		
		final TextBox editEmail = new TextBox();
		/*
		 * "john@hancock.com" should be replaced with a call to a method that returns 
		 * the email address of the current user.
		 */
		editEmail.setText("john@hancock.com");
		myDetailsPanel.setWidget(4, 1, editEmail);		
		
		Button saveEmail = new Button("Save Email");
		myDetailsPanel.setWidget(4, 2, saveEmail);
		
		saveEmail.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				/*
				 * Here should lie a call to a method that takes a String and
				 * adds it to the database replacing the current user's 
				 * email address. Give it editEmail.getText()
				 */
			}
		});
		
		// Add style name for CSS
		myDetailsPanel.addStyleName("gwt-my-details");
	}
	
	private void friendsDetailsPanelSetup(String friendsDetailsKey) {
		// Create widgets
		Label friendsDetailsUsernameTitle = new Label("Username:");
		friendsDetailsPanel.setWidget(0, 0, friendsDetailsUsernameTitle);

		/*
		 * "tsmith1" should be replaced with a call to a method that returns the
		 * username of a friend when given a friend's key. Give it friendsDetailsKey.
		 */
		Label friendsDetailsUsernameLabel = new Label("tsmith1");
		friendsDetailsPanel.setWidget(0, 1, friendsDetailsUsernameLabel);


		Label friendsDetailsNameTitle = new Label("Name:");
		friendsDetailsPanel.setWidget(1, 0, friendsDetailsNameTitle);

		/*
		 * "Tom Smith" should be replaced with a call to a method that returns the
		 * name of a friend when given a friend's key. Give it friendsDetailsKey.
		 */
		Label friendsDetailsNameLabel = new Label("Tom Smith");
		friendsDetailsPanel.setWidget(1, 1, friendsDetailsNameLabel);


		Label friendsDetailsBirthdayTitle = new Label("Birthday:");
		friendsDetailsPanel.setWidget(2, 0, friendsDetailsBirthdayTitle);

		/*
		 * "01/01/1970" should be replaced with a call to a method that returns the
		 * birthday of a friend when given a friend's key. Give it friendsDetailsKey.
		 */
		Label friendsDetailsBirthdayLabel = new Label("01/01/1970");
		friendsDetailsPanel.setWidget(2, 1, friendsDetailsBirthdayLabel);
		

		Label friendsDetailsGenderTitle = new Label("Gender:");
		friendsDetailsPanel.setWidget(3, 0, friendsDetailsGenderTitle);

		/*
		 * "Male" should be replaced with a call to a method that returns the
		 * gender of a friend when given a friend's key. Give it friendsDetailsKey.
		 */
		Label friendsDetailsGenderLabel = new Label("Male");
		friendsDetailsPanel.setWidget(3, 1, friendsDetailsGenderLabel);


		Label friendsDetailsEmailTitle = new Label("Email:");
		friendsDetailsPanel.setWidget(4, 0, friendsDetailsEmailTitle);

		/*
		 * "john@smith.com" should be replaced with a call to a method that returns the
		 * email of a friend when given a friend's key. Give it friendsDetailsKey.
		 */
		Label friendsDetailsEmailLabel = new Label("john@smith.com");
		friendsDetailsPanel.setWidget(4, 1, friendsDetailsEmailLabel);


		Label friendsDetailsKeyTitle = new Label("Public Key:");
		friendsDetailsPanel.setWidget(5, 0, friendsDetailsKeyTitle);

		Label friendsDetailsKeyLabel = new Label(friendsDetailsKey);
		friendsDetailsPanel.setWidget(5, 1, friendsDetailsKeyLabel);

		// Add style name for CSS
		friendsDetailsPanel.addStyleName("gwt-friends-details");
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

	private void groupsControlPanelSetup(String currentGroupID) {
		// if current group is All display 'Add new friend'
		// else display 'Add friend to this group'
		
		// there should be links to each of the users groups when a link is clicked
		// loadFriendsList(groupID);  << Group ID come froms anchor click handler
	
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
		RootPanel.get().add(messageListPanel);
		RootPanel.get().add(myDetailsPanel);
		RootPanel.get().add(inputPanel);
		RootPanel.get().add(outputPanel);
		RootPanel.get().add(navigationPanel);
		RootPanel.get().add(commentsControlPanel);
		RootPanel.get().add(myWallControlPanel);
		RootPanel.get().add(friendsWallControlPanel);
		RootPanel.get().add(myDetailsPanel);
		RootPanel.get().add(messagesControlPanel);
		loadFriendsList("All");
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
		
		//Gonna need some kind of loop here
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

	private void loadFriendsList(String currentGroupID) {
		/*
		RootPanel.get().clear();
		
		// Add navigation to page
		RootPanel.get().add(navigationPanel);
		
		*/
		
		// Prepare panels to add to page
		friendsListPanelSetup(currentGroupID);
		groupsControlPanelSetup(currentGroupID);
		
		// Add panels to page
		RootPanel.get().add(friendsListPanel);
		RootPanel.get().add(groupsControlPanel);
	}

	private void loadAddKey() {

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
