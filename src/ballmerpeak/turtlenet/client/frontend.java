//TO-DO
//Ctrl-F TODO
//Add an option to choose which groups may see your profile data, right now only
//  you can see your own profile info

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

Oh, and conversations/posts/comments are identified by their signature. So when
you like something then you pass the signature from whatever you're liking to
the DB.
*/

package ballmerpeak.turtlenet.client;

import ballmerpeak.turtlenet.shared.Message;
import ballmerpeak.turtlenet.shared.Conversation;

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

    /*
     * As horrible as it is all panels need to be created here so that they can
     * be accessed by by setup methods(which setup the panel in a particular way
     * for the current use) and load methods(which call several setup methods to
     * place a bunch of panels on screen to create a view).
     */
    FlexTable loginPanel = new FlexTable();
    FlexTable friendsListPanel = new FlexTable();
    FlexTable conversationListPanel = new FlexTable();
    FlexTable myDetailsPermissionsPanel = new FlexTable();
    FlexTable conversationPanel = new FlexTable();
    FlexTable newConversationPanel = new FlexTable();
    HorizontalPanel navigationPanel = new HorizontalPanel();
    
    
    //NOT DONE
    HorizontalPanel settingsPanel = new HorizontalPanel();
    

    public void onModuleLoad() {
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


        // Call methods to set up panels
        // THIS IS TEMPORARY > Setup methods should be called by a view constructor
        // This just makes creating the panels easy for now. I think using setup in
        // the names was a confusing mistake. Setup doesnt mean it should only happen
        // once at the start. Each panel should be 'set up' appropriately each time we 
        // want to use it
        loginPanelSetup();
        navigationPanelSetup();
        settingsPanelSetup();
        conversationListPanelSetup();
        friendsListPanelSetup("All");

        myDetailsPermissionsPanelSetup();
        newConversationPanelSetup();

        // Call method to load the initial login page
        loadLogin();
        
        /* For now we can compromise and leave both of these enabled. That way
         * the still works(or lets you enter anything as it does now) and I can
         * see all of the panels without us contantly battling over which one
         * is enabled
         */
        
        // Louis temp
        //loadPanelDev();
    }

    // #########################################################################
    // #########################################################################
    // ####################Setup panels needed to create views##################
    // #########################################################################
    // #########################################################################

    private void loginPanelSetup() {
        // Create login panel widgets
        final Button loginButton = new Button("Login");
        final TextBox passwordInput = new TextBox();
        final Label passwordLabel = new Label();

        // Setup widgets
        passwordInput.setText("");
        passwordLabel.setText("Please enter your password:");

        // Add widgets to login panel
        loginPanel.setWidget(1, 1, passwordLabel);
        loginPanel.setWidget(2, 1, passwordInput);
        loginPanel.setWidget(3, 1, loginButton);

        // Add style name for CSS
        loginPanel.addStyleName("gwt-login");

        // Add click handler for button
        loginButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                turtlenet.startTN(passwordInput.getText(), new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        //TODO error
                    }
                    public void onSuccess(String result) {
                        if (result.equals("success")) {
                            loadWall("me");
                        } else if (result.equals("failure")) {
                            passwordLabel.setText("Please enter your password (again): ");
                        } else {
                            //TODO error, this ought NEVER happen
                            passwordLabel.setText("INVALID RESPONSE FROM TNClient");
                        }
                    }
                });
            }
        });
    }

    private void navigationPanelSetup() {
        // Create navigation links
        Anchor linkMyWall = new Anchor("My Wall");
        Anchor linkConversations = new Anchor("Messages");
        Anchor linkFriends = new Anchor("Friends");
        Anchor linkSettings = new Anchor("Settings");
        Anchor linkLogout = new Anchor("Logout");

        // Add links to navigation panel
        navigationPanel.add(linkMyWall);
        navigationPanel.add(linkConversations);
        navigationPanel.add(linkFriends);
        navigationPanel.add(linkSettings);
        navigationPanel.add(linkLogout);

        // Add style name for CSS
        navigationPanel.addStyleName("gwt-navigation");

        // Add click handlers for anchors
        linkMyWall.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                    loadWall("me");
            }
        });
        
        linkConversations.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                    loadConversationList();
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
                turtlenet.stopTN(new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        //TODO Error
                    }
                    public void onSuccess(String result) {
                        loadLogin();
                    }
                });
            }
        });
    }

    private TextBox friendsListPanel_myKeyTextBox;
    private void friendsListPanelSetup(final String currentGroupID) {   
        friendsListPanel.clear();
        
        // Column title for anchors linking to messages        
        Label friendsNameLabel = new Label("Friend's Name");
        friendsNameLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        friendsNameLabel.getElement().getStyle().setProperty("paddingLeft" , "100px");
        friendsListPanel.setWidget(1, 0, friendsNameLabel);
        
        // Column title for labels outputing the date a message was recieved
        Label friendsKeyLabel = new Label("Friend's Public Key");
        friendsKeyLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        friendsListPanel.setWidget(1, 1, friendsKeyLabel);
    
        turtlenet.getCategoryMembers(currentGroupID, new AsyncCallback<String[][]>() {
            String[][] result;
            int i;
            public void onFailure(Throwable caught) {
                //TODO Error
            }
            public void onSuccess(String[][] _result) {
                result = _result;
                for (i = 0; i < result.length; i++) {
                    //list names/keys
                    Anchor linkFriendsWall = new Anchor(result[i][0]);
                    friendsListPanel.setWidget((i + 2), 0, linkFriendsWall);
                    final String resultString = result[i][1];
                    //TODO LOUISTODO Make substring longer
                    friendsListPanel.setWidget((i + 2), 1, new Label(resultString.substring(0, 11) + "..."));
                    //link names to walls
                    linkFriendsWall.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            loadWall(result[i][1]);
                        }
                    });
                }
            }
        });
        
        int row = friendsListPanel.getRowCount() + 2;
        
        if(!currentGroupID.equals("All")) {
            Label currentGroupLabel = new Label(currentGroupID);
            friendsListPanel.setWidget((row - 1), 3, currentGroupLabel);        
        }
        
        final ListBox currentGroups = new ListBox();
        currentGroups.setVisibleItemCount(1);
        currentGroups.setWidth("150px");
        currentGroups.addItem("Choose a category");
        if(!currentGroupID.equals("All")) {
            currentGroups.addItem("All");
        }
        friendsListPanel.setWidget(3, 3, currentGroups);
        
        turtlenet.getCategories(new AsyncCallback<String[][]>() {
            String[][] result;
            int i;
            public void onFailure(Throwable caught) {
                //TODO Error
            }
            public void onSuccess(String[][] _result) {
                result = _result;
                for (i = 0; i < result.length; i++) {
                    currentGroups.addItem(result[i][0]);
                }
                currentGroups.addChangeHandler(new ChangeHandler() {
                    public void onChange(ChangeEvent event) {
                        friendsListPanelSetup(currentGroups.getItemText(currentGroups.getSelectedIndex()));
                    }
                });
            }
        });
        
        Button newGroup = new Button("Add new category");
        friendsListPanel.setWidget(2, 3, newGroup);
        newGroup.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                newGroup();
            }
        });
        
        
        turtlenet.getMyKey(new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO Error
            }
            public void onSuccess(String result) {
                friendsListPanel_myKeyTextBox = new TextBox();
                friendsListPanel_myKeyTextBox.setWidth("500px");
                friendsListPanel_myKeyTextBox.setText(result);
            }
        });
        
        Label myKeyLabel = new Label("My key: ");
        myKeyLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        myKeyLabel.getElement().getStyle().setProperty("paddingLeft" , "100px");
        friendsListPanel.setWidget((row - 1), 0, myKeyLabel);
        friendsListPanel.setWidget((row - 1), 1, friendsListPanel_myKeyTextBox);
        
        if(currentGroupID.equals("All")) {
            Button addFriend = new Button("Add new friend");
            friendsListPanel.setWidget(1, 3, addFriend);
            addFriend.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    addFriend();
                }
            });
        } else {
            Button addToGroup = new Button("Add people to category");
            friendsListPanel.setWidget(1, 3, addToGroup);
            addToGroup.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    addToGroup(currentGroupID);
                }
            });
        }        
        
        // Add style name for CSS
        friendsListPanel.addStyleName("gwt-friends-list");
    }

    private void conversationListPanelSetup() {
        turtlenet.getConversations(new AsyncCallback<Conversation[]>() {
            Conversation[] result;
            int i;
            public void onFailure(Throwable caught) {
                //TODO Error
            }
            public void onSuccess(Conversation[] _result) {
                result = _result;
                for (i = 0; i < result.length; i++) {
                    final String conversationID = result[i].signature;
                    Anchor linkConversation = new Anchor(result[i].firstMessage);
                    conversationListPanel.setWidget(i, 0, linkConversation);
                    
                    // Add click handlers for anchors
                    linkConversation.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            loadConversation(conversationID);
                        }
                    });
                    
                    Label conversationParticipants = new Label(result[i].concatNames());
                    conversationListPanel.setWidget(i, 1, conversationParticipants);
                }
            }
        });
        
        Button newConversation = new Button("New conversation");
        newConversation.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                loadNewConversation();
            }
        });
        
        conversationListPanel.setWidget((conversationListPanel.getRowCount() + 1), 1, newConversation);
        
        // Add style name for CSS
        conversationListPanel.addStyleName("gwt-conversation-list");
    }

    private void myDetails() {
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        FlexTable myDetailsPanel = new FlexTable();
        RootPanel.get().add(myDetailsPanel);        
        
        // Create widgets relating to username
        Label usernameLabel = new Label("Username:");
        myDetailsPanel.setWidget(0, 0, usernameLabel);
        
        final TextBox editUsername = new TextBox();
        turtlenet.getMyUsername(new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                editUsername.setText(result);
            }
        });
        
        myDetailsPanel.setWidget(0, 1, editUsername);
        
        Button saveUsername = new Button("Save Username");
        myDetailsPanel.setWidget(0, 2, saveUsername);
        
        final Label editUsernameLabel = new Label();
        myDetailsPanel.setWidget(0, 3, editUsernameLabel);
        
        saveUsername.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                turtlenet.claimUsername(editUsername.getText(), new AsyncCallback<String>() {
                     public void onFailure(Throwable caught) {
                         //TODO error
                     }
                     public void onSuccess(String result) {
                         if (result.equals("success")) {
                             editUsernameLabel.setText("Username saved");
                         } else if (result.equals("failure")) {
                             editUsernameLabel.setText("Username already taken");
                         }
                     }
                 });
            }
        });
        
        // TODO LOUIS > ADD A LABEL TO DISPLAY ERRORS
        
        // Create widgets relating to name
        Label nameLabel = new Label("Name:");
        myDetailsPanel.setWidget(1, 0, nameLabel);
        
        final TextBox editName = new TextBox();
        turtlenet.getMyPDATA("name", new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                editName.setText(result);
            }
        });
        myDetailsPanel.setWidget(1, 1, editName);
        
        Button saveName = new Button("Save Name");
        myDetailsPanel.setWidget(1, 2, saveName);
        
        final Label editNameLabel = new Label();
        myDetailsPanel.setWidget(1, 3, editNameLabel);
        
        saveName.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                turtlenet.updatePDATA("name", editName.getText(), new AsyncCallback<String>() {
                     public void onFailure(Throwable caught) {
                         //TODO error
                     }
                     public void onSuccess(String result) {
                         if (result.equals("success")) {
                             editNameLabel.setText("Name saved");
                         } else if (result.equals("failure")) {
                             editNameLabel.setText("Failed to save name");
                         }
                     }
                 });
            }
        });   
        
        // Create widgets relating to birthday
        Label birthdayLabel = new Label("Birthday:");
        myDetailsPanel.setWidget(2, 0, birthdayLabel);
        
        final TextBox editBirthday = new TextBox();
        turtlenet.getMyPDATA("birthday", new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                editBirthday.setText(result);
            }
        });
        myDetailsPanel.setWidget(2, 1, editBirthday);
        
        Button saveBirthday = new Button("Save Birthday");
        myDetailsPanel.setWidget(2, 2, saveBirthday);
        
        final Label editBirthdayLabel = new Label();
        myDetailsPanel.setWidget(2, 3, editBirthdayLabel);
        
        saveBirthday.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                turtlenet.updatePDATA("birthday", editBirthday.getText(), new AsyncCallback<String>() {
                     public void onFailure(Throwable caught) {
                         //TODO error
                     }
                     public void onSuccess(String result) {
                         if (result.equals("success")) {
                             editBirthdayLabel.setText("Birthday saved");
                         } else if (result.equals("failure")) {
                             editBirthdayLabel.setText("Failed to save birthday");
                         }
                     }
                 });
            }
        });

        // Create widgets relating to gender
        // Gender shouldn't be chosen from a list. The user should be able to 
        // write whatever they want. This way people who are trans gender are
        // satisfied(Simply having an 'Other' option is usually not enough
        Label genderLabel = new Label("Gender:");
        myDetailsPanel.setWidget(3, 0, genderLabel);
        
        final TextBox editGender = new TextBox();
        turtlenet.getMyPDATA("sex", new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                editGender.setText(result);
            }
        });
        myDetailsPanel.setWidget(3, 1, editGender);
        
        Button saveGender = new Button("Save Gender");
        myDetailsPanel.setWidget(3, 2, saveGender);
        
        final Label editGenderLabel = new Label();
        myDetailsPanel.setWidget(3, 3, editGenderLabel);
        
        saveGender.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                turtlenet.updatePDATA("sex", editGender.getText(), new AsyncCallback<String>() {
                     public void onFailure(Throwable caught) {
                         //TODO error
                     }
                     public void onSuccess(String result) {
                         if (result.equals("success")) {
                             editGenderLabel.setText("Gender saved");
                         } else if (result.equals("failure")) {
                             editGenderLabel.setText("Failed to save gender");
                         }
                     }
                 });
            }
        });
        
        // Create widgets relating to email
        final Label emailLabel = new Label("Email:");
        myDetailsPanel.setWidget(4, 0, emailLabel);
        
        final TextBox editEmail = new TextBox();
        turtlenet.getMyPDATA("email", new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                editEmail.setText(result);
            }
        });
        myDetailsPanel.setWidget(4, 1, editEmail);        
        
        Button saveEmail = new Button("Save Email");
        myDetailsPanel.setWidget(4, 2, saveEmail);
        
        final Label editEmailLabel = new Label();
        myDetailsPanel.setWidget(4, 3, editEmailLabel);
        
        saveEmail.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                turtlenet.updatePDATA("email", editEmail.getText(), new AsyncCallback<String>() {
                     public void onFailure(Throwable caught) {
                         //TODO error
                     }
                     public void onSuccess(String result) {
                         if (result.equals("success")) {
                             editEmailLabel.setText("Email saved");
                         } else if (result.equals("failure")) {
                            editEmailLabel.setText("Failed to save email");
                         }
                     }
                 });
            }
        });
        
        // TODO LOUISTODO Add link to myDetailsPanelPermissions(maybe)
        
        // Add style name for CSS
        myDetailsPanel.addStyleName("gwt-my-details");
    }
    
    private void myDetailsPermissionsPanelSetup() {
        
        Label myDetailsPermissionsLabel = new Label("Select which groups can view your details");
        myDetailsPermissionsLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        myDetailsPermissionsPanel.setWidget(0, 0, myDetailsPermissionsLabel); 
        
        turtlenet.getCategories(new AsyncCallback<String[][]>() {
            String[][] result;
            int i;
            public void onFailure(Throwable caught) {
                //TODO Error
            }
            public void onSuccess(String[][] _result) {
                result = _result;
                for (i = 0; i < result.length; i++) {
                    final CheckBox groupCheckBox = new CheckBox(result[i][0]);
                    groupCheckBox.setValue(result[i][1].equals("true"));
                    myDetailsPermissionsPanel.setWidget((i + 1), 0, groupCheckBox);
                    
                    groupCheckBox.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            turtlenet.updatePDATApermission(groupCheckBox.getText(), groupCheckBox.getValue(), new AsyncCallback<String>() {
                                public void onFailure(Throwable caught) {
                                    //TODO error
                                }
                                public void onSuccess(String result) {
                                    //success
                                }
                            });
                        }
                    });
                }
            }
        });
        
        // TODO LOUISTODO Add link to myDetailsPanel (maybe)
        myDetailsPermissionsPanel.addStyleName("gwt-my-details-permissions");
    }
    
    private void friendsDetails(String friendsDetailsKey) {
        // Setup basic page
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        // Create main panel
        FlexTable friendsDetailsPanel = new FlexTable();
        RootPanel.get().add(friendsDetailsPanel);    
    
        // Create widgets
        Label friendsDetailsUsernameTitle = new Label("Username:");
        friendsDetailsPanel.setWidget(0, 0, friendsDetailsUsernameTitle);
        
        Label friendsDetailsNameTitle = new Label("Name:");
        friendsDetailsPanel.setWidget(1, 0, friendsDetailsNameTitle);
        
        Label friendsDetailsBirthdayTitle = new Label("Birthday:");
        friendsDetailsPanel.setWidget(2, 0, friendsDetailsBirthdayTitle);
        
        Label friendsDetailsGenderTitle = new Label("Gender:");
        friendsDetailsPanel.setWidget(3, 0, friendsDetailsGenderTitle);
        
        Label friendsDetailsEmailTitle = new Label("Email:");
        friendsDetailsPanel.setWidget(4, 0, friendsDetailsEmailTitle);
        
        Label friendsDetailsKeyTitle = new Label("Public Key:");
        friendsDetailsPanel.setWidget(5, 0, friendsDetailsKeyTitle);

        turtlenet.getUsername(friendsDetailsKey, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                Label friendsDetailsUsernameLabel = new Label(result);
                friendsDetailsPanel.setWidget(0, 1, friendsDetailsUsernameLabel);
            }
        });

        turtlenet.getPDATA("name", friendsDetailsKey, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                Label friendsDetailsNameLabel = new Label(result);
                friendsDetailsPanel.setWidget(1, 1, friendsDetailsNameLabel);
            }
        });
        
        turtlenet.getPDATA("birthday", friendsDetailsKey, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                Label friendsDetailsBirthdayLabel = new Label(result);
                friendsDetailsPanel.setWidget(2, 1, friendsDetailsBirthdayLabel);
            }
        });
        
        turtlenet.getPDATA("sex", friendsDetailsKey, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                Label friendsDetailsGenderLabel = new Label(result);
                friendsDetailsPanel.setWidget(3, 1, friendsDetailsGenderLabel);
            }
        });

        turtlenet.getPDATA("email", friendsDetailsKey, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                Label friendsDetailsEmailLabel = new Label(result);
                friendsDetailsPanel.setWidget(4, 1, friendsDetailsEmailLabel);
            }
        });

        Label friendsDetailsKeyLabel = new Label(friendsDetailsKey);
        friendsDetailsPanel.setWidget(5, 1, friendsDetailsKeyLabel);

        // Add style name for CSS
        friendsDetailsPanel.addStyleName("gwt-friends-details");
    }

    private void loadWall(String key) {
        // Setup basic page
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        // Create main panel
        FlowPanel wallPanel = new FlowPanel();
        RootPanel.get().add(wallPanel);
        // Create a container for controls
        HorizontalPanel wallControlPanel = new HorizontalPanel();
        FlowPanel.add(wallControlPanel);
        // Add widgets to container
        
        // TODO LUKETODO "Name of user" should be replaced with a call to a method
        // that returns the name of a user when given their public key.
        // This method takes a string called key so give it that.
        wallControlPanel.add(new Label("Name of user"));
        
        // TODO LUKETODO "my key" should be replaced with the users key
        if(key.equals("my key")) {
            Anchor myDetails = new Anchor("My details");
            wallControlPanel.add(myDetails);
            myDetails.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    myDetails();
                }
            });
            
        } else {
            Anchor userDetails = new Anchor("Friend's details");
            wallControlPanel.add(userDetails);
            userDetails.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    friendsDetails(key);
                }
            });
        }

        // Add style name for CSS
        wallPanel.addStyleName("gwt-wall");

        // Add click handlers for anchors
    }



    private void settingsPanelSetup() {
        // Create widgets

        // Add widgets to panel

        // Add style name for CSS
        settingsPanel.addStyleName("gwt-settings-panel");

        // Add click handlers for anchors

    }
    
    //must be global because it must be referenced from callback
    private TextArea newConvoInput = new TextArea();
    private void newConversationPanelSetup() {
        final ListBox currentFriends = new ListBox();
        currentFriends.setVisibleItemCount(11);
        currentFriends.setWidth("150px");
        newConversationPanel.setWidget(0, 0, currentFriends);
        
        newConvoInput.setCharacterWidth(80);
        newConvoInput.setVisibleLines(10); 
        newConversationPanel.setWidget(0, 1, newConvoInput);
        
        final ListBox chooseFriend = new ListBox();
        
        turtlenet.getPeople(new AsyncCallback<String[][]>() {
            String[][] result;
            String[] memberKeys;
            int i;
            public void onFailure(Throwable caught) {
                //TODO Error
            }
            public void onSuccess(String[][] _result) {
                result = _result;
                for (i = 0; i < result.length; i++) {
                    //fill combo box
                    chooseFriend.addItem(result[i][0]);
                    String friendKey = (result[i][1]);
                    chooseFriend.setValue(i, friendKey);
                }
                chooseFriend.setVisibleItemCount(1);
                
                FlexTable subPanel = new FlexTable();
                newConversationPanel.setWidget(1, 1, subPanel);
                subPanel.setWidget(1, 0, new Label("Choose a friend: "));
                subPanel.setWidget(1, 1, chooseFriend);
                
                Button addFriend = new Button("Add to the conversation");
                subPanel.setWidget(1, 2, addFriend);
                addFriend.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        currentFriends.addItem(chooseFriend.getItemText(chooseFriend.getSelectedIndex()));
                        currentFriends.setValue((currentFriends.getItemCount() - 1), chooseFriend.getValue(chooseFriend.getSelectedIndex()));
                    }
                });
                
                Button send = new Button("Send");
                newConversationPanel.setWidget(0, 2, send);
                
                memberKeys = new String[currentFriends.getItemCount()];
                for (int i = 0; i < currentFriends.getItemCount(); i++) {
                    memberKeys[i] = currentFriends.getValue(i);
                }
                
                send.addClickHandler(new ClickHandler() {
                    String[] createChatReturn;
                    public void onClick(ClickEvent event) {
                        turtlenet.createCHAT(memberKeys, new AsyncCallback<String[]>() {
                            int i;
                            public void onFailure(Throwable caught) {
                                //TODO Error
                            }
                            public void onSuccess(String[] _ret) {
                                createChatReturn = _ret;
                                if (createChatReturn[0].equals("success")) {
                                    turtlenet.addMessageToCHAT(newConvoInput.getText(), createChatReturn[1], new AsyncCallback<String>() {
                                        public void onFailure(Throwable caught) {
                                            //TODO Error
                                        }
                                        public void onSuccess(String success) {
                                            if (success.equals("success")) {
                                                loadConversation(createChatReturn[1]);
                                            } else {
                                                //TODO Error
                                            }
                                        }
                                    });
                                } else {
                                    //TODO Error
                                }
                            }
                        });
                    }
                });
            }
        });
        
        // Add style name for CSS
        newConversationPanel.addStyleName("gwt-conversation");
    }
    
    private String convoPanelSetup_convosig; //needed in inner class
    private TextArea convoPanelSetup_input = new TextArea();
    private void conversationPanelSetup(String conversationID) {
        convoPanelSetup_convosig = conversationID;
        conversationPanel.setCellSpacing(10);
        conversationPanel.setWidget(0, 0, new Label("Participants: "));
        
        final ListBox currentFriends = new ListBox();
        currentFriends.setVisibleItemCount(1);
        currentFriends.setWidth("150px");
        conversationPanel.setWidget(0, 1, currentFriends);
        
        turtlenet.getConversation(convoPanelSetup_convosig, new AsyncCallback<Conversation>() {
            Conversation result;
            int i;
            public void onFailure(Throwable caught) {
                //TODO Error
            }
            public void onSuccess(Conversation _result) {
                result = _result;
                
                for (i = 0; i < result.users.length; i++) {
                    currentFriends.addItem(result.users[i]);
                }
                
                turtlenet.getConversationMessages(convoPanelSetup_convosig, new AsyncCallback<String[][]>() {
                    String[][] messages;
                    int i;
                    public void onFailure(Throwable caught) {
                        //TODO Error
                    }
                    public void onSuccess(String[][] msgs) {
                        messages = msgs;
                        
                        for (int i = 0; i < messages.length; i++) {
                            Label postedBy = new Label(messages[i][0]);
                            postedBy.getElement().getStyle().setFontWeight(FontWeight.BOLD);
                            conversationPanel.setWidget((i + 1), 0, postedBy);
                            Label messageContents = new Label(messages[i][2]);
                            conversationPanel.setWidget((i + 1), 1, messageContents);
                        }
                        
                        convoPanelSetup_input.setCharacterWidth(80);
                        convoPanelSetup_input.setVisibleLines(10); 
                        int row = (conversationPanel.getRowCount() + 1);
                        conversationPanel.setWidget(row, 1, convoPanelSetup_input);
                        
                        Button send = new Button("Send"); 
                        conversationPanel.setWidget(row, 2, send);
                        send.addClickHandler(new ClickHandler() {
                            public void onClick(ClickEvent event) {
                                turtlenet.addMessageToCHAT(convoPanelSetup_input.getText(), convoPanelSetup_convosig, new AsyncCallback<String>() {
                                    public void onFailure(Throwable caught) {
                                        //TODO Error
                                    }
                                    public void onSuccess(String postingSuccess) {
                                        //Reload the conversation after the new message has been added
                                        loadConversation(convoPanelSetup_convosig);
                                    }
                                });
                            }
                        }); 
                    }
                });
            }
        });

        // Add style name for CSS
        conversationPanel.addStyleName("gwt-conversation");
    }
    
    TextBox newGroup_nameInput = new TextBox();
    private void newGroup() {
        // TODO LOUISTODO
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        FlexTable newGroupPanel = new FlexTable();
        RootPanel.get().add(newGroupPanel);
        
        newGroupPanel.setWidget(0, 0, new Label("Category name: "));
        newGroupPanel.setWidget(0, 1, newGroup_nameInput);
        
        Button createGroup = new Button("Create category");
        newGroupPanel.setWidget(0, 2, createGroup);
        
        createGroup.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                turtlenet.addCategory(newGroup_nameInput.getText(), new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        //TODO error
                    }
                    public void onSuccess(String result) {
                        if (result.equals("success")) {
                            addToGroup(newGroup_nameInput.getText());
                        } else {
                            //TODO Error
                        }
                    }
                });
            }
        });
        
        newGroupPanel.addStyleName("gwt-group");        
    }
    
    private void addToGroup(final String groupID) {
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        FlexTable addToGroupPanel = new FlexTable();
        RootPanel.get().add(addToGroupPanel);
        
        addToGroupPanel.setWidget(1, 0, new Label("Currently in category: "));
        final ListBox currentMembers = new ListBox();
        currentMembers.setVisibleItemCount(10);
        currentMembers.setWidth("150px");
        addToGroupPanel.setWidget(1, 1, currentMembers);
        
        turtlenet.getCategoryMembers(groupID, new AsyncCallback<String[][]>() {
            String[][] result;
            int i;
            public void onFailure(Throwable caught) {
                //TODO Error
            }
            public void onSuccess(String[][] _result) {
                result = _result;
                for (i = 0; i < result.length; i++) {
                    currentMembers.addItem(result[i][0]);
                }
            }
        });
        
        addToGroupPanel.setWidget(2, 0, new Label("Add a friend: "));
        final ListBox allFriends = new ListBox();
        allFriends.setVisibleItemCount(1);
        allFriends.setWidth("150px");
        addToGroupPanel.setWidget(2, 1, allFriends);
        
        turtlenet.getPeople(new AsyncCallback<String[][]>() {
            String[][] result;
            int i;
            public void onFailure(Throwable caught) {
                //TODO Error
            }
            public void onSuccess(String[][] _result) {
                result = _result;
                for (i = 0; i < result.length; i++) {
                    String friendKey = new String(result[i][1]);
                    allFriends.addItem(result[i][0]);
                    allFriends.setValue(i, friendKey);
                }
            }
        });
        
        Button addFriend = new Button("Add friend");
        addToGroupPanel.setWidget(2, 2, addFriend);
        addFriend.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                turtlenet.addToCategory(groupID, allFriends.getValue(allFriends.getSelectedIndex()), new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        //TODO Error
                    }
                    public void onSuccess(String result) {
                        if (result.equals("success")) {
                            addToGroup(groupID);
                        } else {
                            //TODO Error
                        }
                    }
                });
            }
        });
        
        addToGroupPanel.addStyleName("gwt-group");  
    }
    
    TextBox addFriend_keyInput = new TextBox();
    private void addFriend() {
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        FlexTable addFriendPanel = new FlexTable();
        RootPanel.get().add(addFriendPanel);
        
        addFriendPanel.setWidget(0, 0, new Label("Enter the key of the person you wish to add:"));
        addFriend_keyInput.setVisibleLength(100);
        addFriendPanel.setWidget(1, 0, addFriend_keyInput);
        FlexTable subPanel = new FlexTable();
        addFriendPanel.setWidget(2, 0, subPanel);
        
        Button submit = new Button("Add key");
        subPanel.setWidget(0, 0, submit);
        final Label success = new Label("");
        subPanel.setWidget(0, 1, success);
        
        submit.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                turtlenet.addKey(addFriend_keyInput.getText(), new AsyncCallback<String>() {
                    public void onFailure(Throwable caught) {
                        success.setText("Key could not be added");
                    }
                    public void onSuccess(String result) {
                        if (result.equals("success")) {
                                success.setText("Key has been added");
                        } else {
                            success.setText("Key could not be added");
                        }
                    }
                });
            }
        });
        
        addFriendPanel.addStyleName("gwt-friend"); 
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
        RootPanel.get().add(conversationListPanel);
        RootPanel.get().add(myDetailsPanel);
        RootPanel.get().add(navigationPanel);
        RootPanel.get().add(myDetailsPanel);
        RootPanel.get().add(conversationPanel);
        RootPanel.get().add(newConversationPanel);
        RootPanel.get().add(myDetailsPermissionsPanel);
        RootPanel.get().add(friendsListPanel);
    }

    private void loadLogin() {
        RootPanel.get().clear();
        RootPanel.get().add(loginPanel);
    }

    private void loadConversationList() {
        conversationListPanel.clear();
        
        conversationListPanelSetup();
        
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        RootPanel.get().add(conversationListPanel);
    }

    private void loadConversation(String conversationID) {
        //TODO
        conversationPanel.clear();
        conversationPanelSetup(conversationID);
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        RootPanel.get().add(conversationPanel);
    }
    
    private void loadNewConversation() {
        newConversationPanelSetup();
        
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        RootPanel.get().add(newConversationPanel);
    }

    private void loadFriendsList(String currentGroupID) {
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        
        // Add panels to page
        friendsListPanelSetup("All");
        RootPanel.get().add(friendsListPanel);
    }
}
