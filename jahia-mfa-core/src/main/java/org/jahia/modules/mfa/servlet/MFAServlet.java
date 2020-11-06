package org.jahia.modules.mfa.servlet;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet used to override default Jahia login page
 *
 */
@Component(service = Servlet.class, property = "alias=/mfa")
public class MFAServlet extends HttpServlet {

    private static final long serialVersionUID = 500248201001731L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MFAServlet.class);
    public static final String CONTEXT = "mfa";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Content-Type", "text/html;charset=UTF-8");
            HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);

            wrapper.getRequestDispatcher("/modules/jahia-mfa-core/login.jsp").include(wrapper, response);
        } catch (ServletException | IOException ex) {
            LOGGER.error("Impossible to include resource", ex);
        }
    }
}
