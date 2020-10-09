package org.jahia.modules.mfa.valve;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import org.jahia.api.usermanager.JahiaUserManagerService;
import org.jahia.bin.Login;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.params.valves.AuthValveContext;
import org.jahia.params.valves.BaseAuthValve;
import org.jahia.params.valves.LoginUrlProvider;
import org.jahia.params.valves.LoginEngineAuthValveImpl;
import org.jahia.pipelines.Pipeline;
import org.jahia.pipelines.PipelineException;
import org.jahia.pipelines.valves.ValveContext;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.usermanager.JahiaUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthenticationValve extends BaseAuthValve implements LoginUrlProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationValve.class);
    private Pipeline authPipeline;
    private JahiaUserManagerService jahiaUserManagerService;
    private JahiaMFAService jahiaMFAService;

    public void setAuthPipeline(Pipeline authPipeline) {
        this.authPipeline = authPipeline;
    }

    public void setJahiaUserManagerService(JahiaUserManagerService jahiaUserManagerService) {
        this.jahiaUserManagerService = jahiaUserManagerService;
    }

    public void setJahiaMFAService(JahiaMFAService jahiaMFAService) {
        this.jahiaMFAService = jahiaMFAService;
    }

    public void start() {
        setId(MFAConstants.AUTH_VALVE_ID);
        removeValve(authPipeline);
        addValve(authPipeline, -1, null, "LoginEngineAuthValve");
    }

    public void stop() {
        removeValve(authPipeline);
    }

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

            if (username != null && passwordAndToken != null) {
                // Check if the user has site access ( even though it is not a user of this site )
                user = jahiaUserManagerService.lookupUser(username, site);
                if (user == null) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Login failed. Unknown username {}", username.replaceAll("[\r\n]", ""));
                    }
                    httpServletRequest.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.UNKNOWN_USER);
                } else if (jahiaMFAService.hasMFA(user)) {
                    if (passwordAndToken.length() > MFAConstants.TOKEN_SIZE) {
                        final String password = passwordAndToken.substring(0, passwordAndToken.length() - MFAConstants.TOKEN_SIZE);
                        final String token = passwordAndToken.substring(password.length(), passwordAndToken.length());
                        if (user.verifyPassword(password) && verifyToken(user, token)) {
                            if (!user.isAccountLocked()) {
                                ok = true;
                            } else {
                                LOGGER.warn("Login failed: account for user {} is locked.", user.getName());
                                httpServletRequest.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.ACCOUNT_LOCKED);
                                return;
                            }
                        } else {
                            LOGGER.warn("Login failed: password and token verification failed for user {}", user.getName());
                            httpServletRequest.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.BAD_PASSWORD);
                            return;
                        }
                    } else {
                        LOGGER.warn("Login failed: password and token verification failed for user {}", user.getName());
                        httpServletRequest.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.BAD_PASSWORD);
                        return;
                    }
                }
            }
        } else {
            valveContext.invokeNext(context);
            return;
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
            return;
        }
    }

    private boolean verifyToken(JCRUserNode user, String token) {
        try {
            if (user.hasNode(MFAConstants.NODE_NAME_MFA)) {
                final JCRNodeWrapper node = user.getNode(MFAConstants.NODE_NAME_MFA);
                if (node.hasProperty(MFAConstants.PROP_ACTIVATED) && node.getProperty(MFAConstants.PROP_ACTIVATED).getBoolean() && node.hasProperty(MFAConstants.PROP_PROVIDER)) {
                    return jahiaMFAService.verifyToken(user, node.getPropertyAsString(MFAConstants.PROP_PROVIDER), token);
                }
            }
        } catch (RepositoryException ex) {
            LOGGER.warn("Unable to read MFA configuration for user: " + user.getName(), ex);
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

    @Override
    public String getLoginUrl(HttpServletRequest request) {
        return request.getServerName() + ":8080/modules/jahia-mfa-core/jsp/login.jsp";
    }

    @Override
    public boolean hasCustomLoginUrl() {
        return true;
    }

}
