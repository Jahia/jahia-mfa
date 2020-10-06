package org.jahia.modules.mfa.valve;

import java.util.Map;
import javax.jcr.RepositoryException;
import javax.security.auth.login.Configuration;
import javax.servlet.http.HttpServletRequest;
import org.jahia.bin.Login;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.params.valves.AuthValveContext;
import org.jahia.params.valves.BaseAuthValve;
import org.jahia.params.valves.LoginEngineAuthValveImpl;
import org.jahia.pipelines.Pipeline;
import org.jahia.pipelines.PipelineException;
import org.jahia.pipelines.valves.Valve;
import org.jahia.pipelines.valves.ValveContext;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Valve.class, scope = ServiceScope.SINGLETON, immediate = true)
public final class AuthenticationValve extends BaseAuthValve {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationValve.class);
    private Pipeline authPipeline;
    private String mafEntry;

    @Reference(service = Pipeline.class, target = "(type=authentication)")
    public void setAuthPipeline(Pipeline authPipeline) {
        this.authPipeline = authPipeline;
    }

    @Activate
    public void activate(Map<String, ?> props) {
        setId(MFAConstants.AUTH_VALVE_ID);
        mafEntry = (String) props.get("mafEntry");
        removeValve(authPipeline);
        if (Configuration.getConfiguration().getAppConfigurationEntry(mafEntry) != null) {
            addValve(authPipeline, 0, null, null);
        }
    }

    @Override
    public void invoke(Object context, ValveContext valveContext) throws PipelineException {

        if (!isEnabled()) {
            valveContext.invokeNext(context);
            return;
        }

        final AuthValveContext authContext = (AuthValveContext) context;
        final HttpServletRequest httpServletRequest = authContext.getRequest();

        JCRUserNode user = null;
        boolean ok = false;

        if (isLoginRequested(httpServletRequest)) {

            final String username = httpServletRequest.getParameter("username");
            final String passwordAndToken = httpServletRequest.getParameter("password");
            final String site = httpServletRequest.getParameter("site");

            if (username != null && passwordAndToken != null && passwordAndToken.length() > MFAConstants.TOKEN_SIZE) {
                final String password = passwordAndToken.substring(0, passwordAndToken.length() - MFAConstants.TOKEN_SIZE - 1);
                final String token = passwordAndToken.substring(password.length() - 1, passwordAndToken.length() - 1);
                // Check if the user has site access ( even though it is not a user of this site )
                final JahiaUserManagerService userManagerService = JahiaUserManagerService.getInstance();
                user = userManagerService.lookupUser(username, site);
                if (user != null) {
                    if (user.verifyPassword(password) && verifyToken(user, token)) {
                        if (!user.isAccountLocked()) {
                            ok = true;
                        } else {
                            LOGGER.warn("Login failed: account for user {} is locked.", user.getName());
                            httpServletRequest.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.ACCOUNT_LOCKED);
                        }
                    } else {
                        LOGGER.warn("Login failed: password and token verification failed for user {}", user.getName());
                        httpServletRequest.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.BAD_PASSWORD);
                    }
                } else if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Login failed. Unknown username {}", username.replaceAll("[\r\n]", ""));
                    httpServletRequest.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.UNKNOWN_USER);
                }
            } else {
                valveContext.invokeNext(context);
                return;
            }
        }

        if (ok) {
            LOGGER.debug("User {} logged in.", user);

            JahiaUser jahiaUser = user.getJahiaUser();

            if (httpServletRequest.getSession(false) != null) {
                httpServletRequest.getSession().invalidate();
            }

            httpServletRequest.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.OK);
            authContext.getSessionFactory().setCurrentUser(jahiaUser);
        } else {
            valveContext.invokeNext(context);
        }
    }

    private boolean verifyToken(JCRUserNode user, String token) {
        try {
            if (user.hasNode(MFAConstants.NODE_NAME_MFA)) {
                final JCRNodeWrapper node = user.getNode(MFAConstants.NODE_NAME_MFA);
                if (node.hasProperty(MFAConstants.PROP_ACTIVATED) && node.getProperty(MFAConstants.PROP_ACTIVATED).getBoolean() && node.hasProperty(MFAConstants.PROP_PROVIDER)) {
                    //code to call the related provider and return true/false
                }
            }
        } catch (RepositoryException ex) {
            LOGGER.warn("Unable to read tokens for user: " + user.getName(), ex);
            return false;
        }
        return false;
    }

    private boolean isLoginRequested(HttpServletRequest request) {
        String doLogin = request.getParameter(LoginEngineAuthValveImpl.LOGIN_TAG_PARAMETER);
        if (doLogin != null) {
            return Boolean.valueOf(doLogin) || "1".equals(doLogin);
        } else if ("/cms".equals(request.getServletPath())) {
            return Login.getMapping().equals(request.getPathInfo());
        }

        return false;
    }
}
