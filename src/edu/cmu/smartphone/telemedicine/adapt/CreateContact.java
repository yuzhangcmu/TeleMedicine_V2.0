package edu.cmu.smartphone.telemedicine.adapt;

import edu.cmu.smartphone.telemedicine.entities.Contact;
import android.content.Context;


public interface CreateContact {
    public void addContact(String name, String loginID);
    public Contact addContact(Context context, String userID);
    public Contact addContactToLocal(Context context, String userID);
}
