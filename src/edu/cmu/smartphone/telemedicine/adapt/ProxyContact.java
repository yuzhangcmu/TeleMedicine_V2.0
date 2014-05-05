package edu.cmu.smartphone.telemedicine.adapt;

import java.util.LinkedHashMap;

import android.content.Context;
import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.DBLayout.Dao_parse;
import edu.cmu.smartphone.telemedicine.entities.Contact;

public class ProxyContact {
    private static LinkedHashMap<String, Contact> contactHash = 
            new LinkedHashMap<String, Contact>();
    
    public void addContact(String name, String loginID) {
        Contact contact = new Contact(name, loginID);
        
        // add a new contact into the hash map list.
        contactHash.put(loginID, contact);
    }
    
    public Contact addContactToLocal(Context context, String userID) {
        // add a new friend.
        Contact contact = new Contact(userID, userID);
        contact.setSortKey(userID);
        
        // add the new friend to local database. 
        Dao_Sqlite dao = new Dao_Sqlite(context);
        dao.addContact(contact);
        
        return contact;
    }
    
    public void deleteContactFromLocal(Context context, String userID) {
        // delete the friend from local database. 
        Dao_Sqlite dao = new Dao_Sqlite(context);
        dao.deleteContact(userID);
    }
    
    public Contact addContact(Context context, String userID) {
        Contact contact = addContactToLocal(context, userID);
        
        // also add the new friend to the cloud service.
        Dao_parse daoparse = new Dao_parse(context);
        daoparse.addContactCloud(contact);
        
        return contact;
    }
    
    /**
     * delete a contact from local database, also delete it from the cloud.
     * @param context
     * @param userID
     */
    public void delContact(Context context, String userID) {
        deleteContactFromLocal(context, userID);
        
        // delete a friend from the cloud service.
        Dao_parse daoparse = new Dao_parse(context);
        daoparse.deleteContactCloud(userID);
    }
    
    public Contact getContact(String loginID) {
        return contactHash.get(loginID);
    }
    
    
    public String getContactName(String loginID) {
        return contactHash.get(loginID).getName();
    }
    
    public void updateContact(String loginID, String intro) {
        Contact contact = getContact(loginID);
        contact.setIntro(intro);
    }
}
