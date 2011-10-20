package fr.logisima.contact.struts.actions.contact;

import fr.logisima.contact.struts.actions.SmileActionSupport;
import fr.logisima.contact.struts.model.DummyDatabase;

public class ContactDeleteAction extends SmileActionSupport {

    private String id;

    public String execute() {
        DummyDatabase.delete(Integer.parseInt(id));
        return SUCCESS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
