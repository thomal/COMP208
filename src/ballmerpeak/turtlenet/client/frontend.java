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
    HorizontalPanel settingsPanel = new HorizontalPanel();
    FlexTable friendsListPanel = new FlexTable();
    FlexTable conversationListPanel = new FlexTable();
    FlexTable myDetailsPanel = new FlexTable();
    FlexTable myDetailsPermissionsPanel = new FlexTable();
    FlexTable friendsDetailsPanel = new FlexTable();
    FlexTable conversationPanel = new FlexTable();
    FlexTable newConversationPanel = new FlexTable();
    FlowPanel inputPanel = new FlowPanel();
    FlowPanel outputPanel = new FlowPanel();
    HorizontalPanel navigationPanel = new HorizontalPanel();
    FlowPanel commentsControlPanel = new FlowPanel();
    FlowPanel myWallControlPanel = new FlowPanel();
    FlowPanel friendsWallControlPanel = new FlowPanel();
    FlowPanel groupsControlPanel = new FlowPanel();
    

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
        myWallControlPanelSetup();
        friendsWallControlPanelSetup();
        settingsPanelSetup();
        conversationListPanelSetup();
        myDetailsPanelSetup();
        inputPanelSetup();
        outputPanelSetup();
        myDetailsPanelSetup();
        conversationPanelSetup("conversationID");
        commentsControlPanelSetup();
        myDetailsPermissionsPanelSetup();
        newConversationPanelSetup();
        /*
         * "publicKey" here should be replaced with the key of the friend's 
         * details we want to look up
         */
        friendsDetailsPanelSetup("<falsekey2>");

        // Call method to load the initial login page
        loadLogin();
        
        /* For now we can compromise and leave both of these enabled. That way
         * the still works(or lets you enter anything as it does now) and I can
         * see all of the panels without us contantly battling over which one
         * is enabled
         */
        
        // Louis temp
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
                            loadMyWall();
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
                    loadMyWall();
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
    
        turtlenet.getCategoryMembers(currentGroupID, new AsyncCallback<String[][]>() {
            String[][] result;
            int i;
            public void onFailure(Throwable caught) {
                //TODO Error
            }
            public void onSuccess(String[][] _result) {
                result = _result;
                for (i = 0; i <= result.length; i++) {
                    //list names/keys
                    Anchor linkFriendsWall = new Anchor(result[i][0]);
                    friendsListPanel.setWidget((i + 1), 0, linkFriendsWall);
                    friendsListPanel.setWidget((i + 1), 1, new Label(result[i][1]));
                    //link names to walls
                    linkFriendsWall.addClickHandler(new ClickHandler() {
                        public void onClick(ClickEvent event) {
                            loadFriendsWall(result[i][1]);
                        }
                    });
                }
            }
        });
        
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
                for (i = 0; i <= result.length; i++) {
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
        
        Button newConversation = new Button("New Conversation");
        newConversation.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                loadNewConversation();
            }
        });
        
        conversationListPanel.setWidget((conversationListPanel.getRowCount() + 1), 1, newConversation);
        
        // Add style name for CSS
        conversationListPanel.addStyleName("gwt-conversation-list");
    }

    private void myDetailsPanelSetup() {
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
        turtlenet.getMyPDATA("RealName", new AsyncCallback<String>() {
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
                turtlenet.updatePDATA("RealName", editName.getText(), new AsyncCallback<String>() {
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
        turtlenet.getMyPDATA("DOB", new AsyncCallback<String>() {
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
                turtlenet.updatePDATA("DOB", editBirthday.getText(), new AsyncCallback<String>() {
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
        turtlenet.getMyPDATA("Gender", new AsyncCallback<String>() {
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
                turtlenet.updatePDATA("Gender", editGender.getText(), new AsyncCallback<String>() {
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
        turtlenet.getMyPDATA("Email", new AsyncCallback<String>() {
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
                turtlenet.updatePDATA("Email", editEmail.getText(), new AsyncCallback<String>() {
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
                for (i = 0; i <= result.length; i++) {
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
        
        // TODO LOUISTODO Add link to myDetailsPanel
        myDetailsPermissionsPanel.addStyleName("gwt-my-details-permissions");
    }
    
    private void friendsDetailsPanelSetup(String friendsDetailsKey) {
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

        turtlenet.getPDATA("RealName", friendsDetailsKey, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                Label friendsDetailsNameLabel = new Label(result);
                friendsDetailsPanel.setWidget(1, 1, friendsDetailsNameLabel);
            }
        });
        
        turtlenet.getPDATA("DOB", friendsDetailsKey, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                Label friendsDetailsBirthdayLabel = new Label(result);
                friendsDetailsPanel.setWidget(2, 1, friendsDetailsBirthdayLabel);
            }
        });
        
        turtlenet.getPDATA("Gender", friendsDetailsKey, new AsyncCallback<String>() {
            public void onFailure(Throwable caught) {
                //TODO error
            }
            public void onSuccess(String result) {
                Label friendsDetailsGenderLabel = new Label(result);
                friendsDetailsPanel.setWidget(3, 1, friendsDetailsGenderLabel);
            }
        });

        turtlenet.getPDATA("Email", friendsDetailsKey, new AsyncCallback<String>() {
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
                for (i = 0; i <= result.length; i++) {
                    //fill combo box
                    String friendKey = (result[i][1]);
                    chooseFriend.addItem(result[i][0]);
                    chooseFriend.setValue(i, friendKey);
                    chooseFriend.setVisibleItemCount(1);
                }
                
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
                newConversationPanel.setWidget(1, 2, send);
                
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
    
     private void conversationPanelSetup(String conversationID) {
        // Create widgets

        // Add widgets to panel

        // Add style name for CSS
        conversationPanel.addStyleName("gwt-conversation");

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
        RootPanel.get().add(conversationListPanel);
        RootPanel.get().add(myDetailsPanel);
        RootPanel.get().add(inputPanel);
        RootPanel.get().add(outputPanel);
        RootPanel.get().add(navigationPanel);
        RootPanel.get().add(commentsControlPanel);
        RootPanel.get().add(myWallControlPanel);
        RootPanel.get().add(friendsWallControlPanel);
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

    private void loadMyWall() {
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        
        //Gonna need some kind of loop here
        RootPanel.get().add(outputPanel);
        RootPanel.get().add(myWallControlPanel);

        // Some kind of way of accepting other peoples posts on your wall
        //TODO
    }

    private void loadFriendsWall(String friendKey) {
        //TODO
    }

    private void loadMyDetails() {
        //TODO
    }

    private void loadFriendsDetails() {
        //TODO
    }

    private void loadComments() {
        //TODO
    }

    private void loadConversationList() {
        conversationListPanelSetup();
        
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        RootPanel.get().add(conversationListPanel);
    }

    private void loadConversation(String conversationID) {
        //TODO
        // conversationPanelSetup(conversationID);
        RootPanel.get().clear();
    }
    
    private void loadNewConversation() {
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        RootPanel.get().add(newConversationPanel);
    }

    private void loadFriendsList(String currentGroupID) {
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        
        // Prepare panels to add to page
        friendsListPanelSetup(currentGroupID);
        groupsControlPanelSetup(currentGroupID);
        
        // Add panels to page
        friendsListPanelSetup("all");
        RootPanel.get().add(friendsListPanel);
        RootPanel.get().add(groupsControlPanel);
    }

    private void loadAddKey() {
        //TODO
    }


    private void loadEditGroups() {
        //TODO
    }

    private void loadSettings() {
        myDetailsPanelSetup();
        myDetailsPermissionsPanelSetup();
        
        RootPanel.get().clear();
        RootPanel.get().add(navigationPanel);
        RootPanel.get().add(myDetailsPanel);
        RootPanel.get().add(myDetailsPermissionsPanel);
    }

    private void loadCreatePost() {
        //TODO
    }

    private void loadCreateComment() {
        //TODO
    }

    private void loadCreateMessages() {
        //TODO
    }
}
