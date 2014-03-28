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
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class frontend implements EntryPoint{

    /**
     * Create a remote service proxy to talk to the server-side Turtlenet service.
     */
    private final TurtlenetAsync turtlenet = GWT.create(Turtlenet.class);
    
    HorizontalPanel navigation = new HorizontalPanel();

    public void onModuleLoad() {        
		//Create Navigation Links
		Hyperlink linkWall = new Hyperlink("Wall", "wall");
		Hyperlink linkMessages = new Hyperlink("Messages", "messages");
		Hyperlink linkFriends = new Hyperlink("Friends", "friends");
		Hyperlink linkEvents = new Hyperlink("Events", "events");
        //Add links to navigation panel
        navigation.add(linkWall);
        navigation.add(linkMessages);
        navigation.add(linkFriends);
        navigation.add(linkEvents);
        //Add style name to navigation for CSS
        navigation.addStyleName("gwt-navigation");
        //Add navigation to page
        RootPanel.get().add(navigation);
	}
}
