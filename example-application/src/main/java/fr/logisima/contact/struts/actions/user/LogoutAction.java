package fr.logisima.contact.struts.actions.user;

import fr.logisima.contact.Constante;
import fr.logisima.contact.struts.actions.SmileActionSupport;

public class LogoutAction extends SmileActionSupport {

    public String execute() throws Exception {
        getSession().remove(Constante.SESSION_USERNAME);
        getSession().remove(Constante.SESSION_USERROLE);
        return SUCCESS;
    }
}
