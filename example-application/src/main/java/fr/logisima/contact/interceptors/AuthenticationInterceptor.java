package fr.logisima.contact.interceptors;

import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import fr.logisima.contact.Constante;

public class AuthenticationInterceptor implements Interceptor {

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        Map session = invocation.getInvocationContext().getSession();
        boolean isAuthenticated = false;

        String username = (String) session.get(Constante.SESSION_USERNAME);

        if (username != null) {
            isAuthenticated = true;
            return invocation.invoke();
        } else {
            return Action.LOGIN;
        }
    }
}
