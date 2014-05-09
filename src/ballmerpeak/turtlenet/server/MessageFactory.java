//Can not be Message constructors because of GWT
//These methods can't be static like they should be because of GWT

package ballmerpeak.turtlenet.server;
import ballmerpeak.turtlenet.shared.Message;
import ballmerpeak.turtlenet.server.Crypto;
import java.security.*;


public class MessageFactory {
    public MessageFactory(){
    }

    /** Create a new message.
     * Calculates timestamp and signautre for you.
     * \param cmd An appropriate value for the cmd field, e.g.: POST, ADDKEY.
     * \param content The main body of the message.
     * \return A message with correct timestamp and signature.
     */
    public  Message newMessage(String cmd, String content) {
        long timestamp = System.currentTimeMillis();
        Message msg = new Message(cmd, content, timestamp, "");
        msg.signature = Crypto.sign(msg);
        return msg;
    }
    
    /** Create a new claim message.
     * Calculates timestamp and signautre for you.
     * \param username The name to claim.
     * \return A message claiming the given username with correct timestamp and signature.
     */
    public Message newCLAIM(String username) {
        return newMessage("CLAIM", username);
    }
    
    /** Create a new revoke message.
     * Calculates timestamp and signautre for you.
     * \param time The time from which you key ought not be trusted.
     * \return A message revoking your key with correct timestamp and signature.
     */
    public Message newREVOKE(long time) {
        return newMessage("REVOKE", ""+time);
    }
    
    /** Create a new pdata message.
     * Calculates timestamp and signautre for you.
     * \param field The field you wish to set.
     * \param value The value you wish to give that field.
     * \return A message updating your profile information with correct timestamp and signature.
     */
    public Message newPDATA(String field, String value) {
        return newMessage("PDATA", field + ":" + value + ";");
    }
    
    /** Create a new pdata message.
     * Calculates timestamp and signautre for you.
     * \param fields The fields you wish to set.
     * \param values The values you wish to give the respective field.
     * \return A message updating your profile information with correct timestamp and signature.
     */
    public Message newPDATA(String[] fields, String[] values) {
        String content = "";
        for (int i = 0; i < fields.length; i++)
            content += (values[i] + ":" + fields[i] + ";");
        Logger.write("VERBOSE", "MsgF", "constructed pdata message: " + content);
        return newMessage("PDATA", content);
    }
    
    /** Create a message that creates a new conversation.
     * Calculates timestamp and signautre for you.
     * \param keys The people in the conversation.
     * \return A message that creates the specified conversation with correct timestamp and signature.
     */
    public Message newCHAT(PublicKey[] keys) {
        String keyString = "";
        String delim = "";
        for (int i = 0; i < keys.length; i++) {
            keyString += delim + Crypto.encodeKey(keys[i]);
            delim = ":"; /*intentional*/
        }
        return newMessage("CHAT", keyString);
    }
    
    /** Create a message that creates a new conversation.
     * Calculates timestamp and signautre for you.
     * \param keys The people in the conversation, encoded as strings.
     * \return A message that creates the specified conversation with correct timestamp and signature.
     */
    public Message newCHAT(String[] keys) {
        String keyString = "";
        String delim = "";
        for (int i = 0; i < keys.length; i++) {
            keyString += delim + keys[i];
            delim = ":"; /*intentional*/
        }
        return newMessage("CHAT", keyString);
    }
    
    /** Create a message that adds a post to an extant conversation.
     * Calculates timestamp and signautre for you.
     * \param convoSig The conversation to add a message to.
     * \param msg The text of the message to add.
     * \return A message that adds the specified message to the specified conversation with correct timestamp and signature.
     */
    public Message newPCHAT(String convoSig, String msg) {
        return newMessage("PCHAT", convoSig + ":" + msg);
    }
    
    /** Create a message that adds a post to an wall.
     * Calculates timestamp and signautre for you.
     * \param msg The text of the post.
     * \param wall The key of the wall to add a post to.
     * \param visibleTo The keys of everyone who is able to see the post.
     * \return A message that adds the specified post to the specified wall with correct timestamp and signature.
     */
    public Message newPOST(String msg, String wall, String[] visibleTo) {
        String content = wall;
        for (int i = 0; i < visibleTo.length; i++)
            content += (":" + visibleTo[i]);
        content += (":" + msg);
        return newMessage("POST", content);
    }
    
    /** Create a message that adds a comment to a comment or post.
     * Calculates timestamp and signautre for you.
     * \param comment The text of the comment.
     * \param itemSig The signature of the post or comment you want to add a comment to.
     * \return A message that adds the specified comment to the specified post or comment with correct timestamp and signature.
     */
    public Message newCMNT(String itemSig, String comment) {
        return newMessage("CMNT", itemSig + ":" + comment);
    }
    
    /** Create a message that adds a like to a comment or post.
     * Calculates timestamp and signautre for you.
     * \param itemSig The signature of the post or comment you want to add a like to.
     * \return A message that adds a like to the specified post or comment with correct timestamp and signature.
     */
    public Message newLIKE(String itemSig) {
        return newMessage("LIKE", itemSig);
    }
    
    /** Create a message that removes a like from a comment or post.
     * Calculates timestamp and signautre for you.
     * \param itemSig The signature of the post or comment you want to remove a like from.
     * \return A message that removes a like from the specified post or comment with correct timestamp and signature.
     */
    public Message newUNLIKE(String itemSig) {
        return newMessage("UNLIKE", itemSig);
    }
    
    /** Create a message that adds a new event.
     * Calculates timestamp and signautre for you.
     * \param start The timestamp (in ms since midnight jan 1st 1970) of the start of the event.
     * \param end The timestamp (in ms since midnight jan 1st 1970) of the end of the event.
     * \param descrip A description of the event.
     * \return A message that adds a new event with correct timestamp and signature.
     */
    public Message newEVNT(long start, long end, String descrip) {
        return newMessage("EVNT", start + ":" + end + ":" + descrip);
    }
    
    /** Create a message that adds a new category.
     * Calculates timestamp and signautre for you.
     * \param name The name of the new category.
     * \param canSeePDATA true is the new categroy should be able to see your profile information, false otherwise.
     * \return A message that adds a new category as specified with correct timestamp and signature.
     */
    public Message newADDCAT(String name, boolean canSeePDATA) {
        return newMessage("ADDCAT", (canSeePDATA?"true":"false") + ":" + name);
    }
    
    /** Create a message that updates where a category can see your profile information.
     * Calculates timestamp and signautre for you.
     * \param name The name of the category.
     * \param canSeePDATA true is the new categroy should be able to see your profile information, false otherwise.
     * \return A message that updates the specified category with correct timestamp and signature.
     */
    public Message newUPDATECAT(String category, boolean value) {
        return newMessage("UPDATECAT", (value?"true":"false") + ":" + category);
    }
    
    /** Create a message that adds a user to an extant category.
     * Calculates timestamp and signautre for you.
     * \param category The name of the category.
     * \param key The key of the user to be added to the category.
     * \return A message that adds the specified user to the specified category with correct timestamp and signature.
     */
    public Message newADDTOCAT(String category, String key) {
        return newMessage("ADDTOCAT", key + ":" + category);
    }
    
    /** Create a message that removes a user from an extant category.
     * Calculates timestamp and signautre for you.
     * \param category The name of the category.
     * \param key The key of the user to be removed from the category.
     * \return A message that removes the specified user from the specified category with correct timestamp and signature.
     */
    public Message newREMFROMCAT(String category, String key) {
        return newMessage("REMFROMCAT", key + ":" + category);
    }
    
    /** Create a message that adds a key to the database.
     * Calculates timestamp and signautre for you.
     * \param key The key of the user to be added to the database.
     * \return A message that adds the specified user to the database with correct timestamp and signature.
     */
    public Message newADDKEY(String key) {
        return newMessage("ADDKEY", key);
    }
}
