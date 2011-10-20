package fr.logisima.contact.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import fr.logisima.contact.Constante;

public class RoleFilter implements Filter {

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        // nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
     * javax.servlet.FilterChain)
     */
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        chain.doFilter(new SecurityHttpServletRequest(req), response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(final FilterConfig filterConfig) {
        // nothing to do
    }
}

class SecurityHttpServletRequest extends HttpServletRequestWrapper {

    public SecurityHttpServletRequest(final HttpServletRequest req) {
        super(req);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.http.HttpServletRequestWrapper#isUserInRole(java.lang.String )
     */
    @Override
    public boolean isUserInRole(final String roleName) {
        HttpSession session = this.getSession();
        if (session.getAttribute(Constante.SESSION_USERROLE) != null
                && session.getAttribute(Constante.SESSION_USERROLE).equals(roleName)) {
            return true;
        }
        else {
            return false;
        }
    }
}
