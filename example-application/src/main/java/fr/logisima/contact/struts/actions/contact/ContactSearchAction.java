package fr.logisima.contact.struts.actions.contact;

import java.util.List;

import fr.logisima.contact.struts.actions.SmileActionSupport;
import fr.logisima.contact.struts.model.Contact;
import fr.logisima.contact.struts.model.DummyDatabase;

public class ContactSearchAction extends SmileActionSupport {

    private List<Contact> contacts;

    private String        searchString;

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public String execute() throws Exception {
        contacts = DummyDatabase.search(searchString);
        return SUCCESS;
    }

}
