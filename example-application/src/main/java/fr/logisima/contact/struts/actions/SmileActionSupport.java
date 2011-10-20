package fr.logisima.contact.struts.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

public class SmileActionSupport extends ActionSupport implements SessionAware {

    // ---- SessionAware ----

    /**
     * <p>
     * Field to store session context, or its proxy.
     * </p>
     */
    private Map session;

    /**
     * <p>
     * Store a new session context.
     * </p>
     * 
     * @param value
     *            A Map representing session state
     */
    public void setSession(Map value) {
        session = value;
    }

    /**
     * <p>
     * Provide session context.
     * </p>
     * 
     * @return session context
     */
    public Map getSession() {
        return session;
    }

}
