package fr.logisima.contact.struts.actions.contact;

import java.util.List;

import fr.logisima.contact.struts.actions.SmileActionSupport;
import fr.logisima.contact.struts.model.Contact;
import fr.logisima.contact.struts.model.DummyDatabase;

public class ContactListAction extends SmileActionSupport {

    private List<Contact> contacts;

    public List<Contact> getContacts() {
        return contacts;
    }

    public String execute() throws Exception {
        contacts = DummyDatabase.list();
        return SUCCESS;
    }

}
