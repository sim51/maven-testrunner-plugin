package fr.logisima.contact.struts.actions.user;

import fr.logisima.contact.Constante;
import fr.logisima.contact.struts.actions.SmileActionSupport;

public class LoginAction extends SmileActionSupport {

    private String username;
    private String password;

    public String form() {
        return INPUT;
    }

    public String execute() throws Exception {
        if (username.equals(password)) {
            getSession().put(Constante.SESSION_USERNAME, username);
            getSession().put(Constante.SESSION_USERROLE, username);
            return SUCCESS;
        }
        else {
            this.addFieldError("username", getText("error.login.bad"));
            return INPUT;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}