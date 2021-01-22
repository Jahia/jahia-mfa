package org.jahia.modules.mfa.valve;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.lang3.StringUtils;
import org.jahia.api.usermanager.JahiaUserManagerService;
import org.jahia.bin.Login;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.modules.mfa.servlet.MFAServlet;
import org.jahia.params.valves.*;
import org.jahia.pipelines.Pipeline;
import org.jahia.pipelines.PipelineException;
import org.jahia.pipelines.valves.ValveContext;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.usermanager.JahiaUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthenticationValve extends AutoRegisteredBaseAuthValve implements LoginUrlProvider {

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

        final AuthValveContext authContext = (AuthValveContext) context;
        final HttpServletRequest request = authContext.getRequest();
        final String username = request.getParameter("username");
        String passwordAndToken = request.getParameter("password");
        
        
        LOGGER.debug("jahia-mfa-core authentication valve");
        if (isEnabled() && isLoginRequested(request) && username != null && passwordAndToken != null) {
            
            JCRUserNode user = null;

            final String site = request.getParameter("site");

            // Check if the user has site access ( even though it is not a user of this site )
            user = jahiaUserManagerService.lookupUser(username, site);
            if (user != null && jahiaMFAService.hasMFA(user)) {
                final boolean tokenIncluded = request.getParameter("digit-1") == null;
                final String token;
                final String password;
                if (tokenIncluded && passwordAndToken.length() > MFAConstants.TOKEN_SIZE) {
                    password = passwordAndToken.substring(0, passwordAndToken.length() - MFAConstants.TOKEN_SIZE);
                    token = passwordAndToken.substring(password.length(), passwordAndToken.length());
                } else {
                    password = passwordAndToken;
                    token = extractTokenFromRequest(request);
                }
                
                if (verifyCredentials(user, password, token)) {
                    LOGGER.debug("User {} logged in.", user);

                    JahiaUser jahiaUser = user.getJahiaUser();

                    if (request.getSession(false) != null) {
                        request.getSession().invalidate();
                    }

                    request.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.OK);
                    authContext.getSessionFactory().setCurrentUser(jahiaUser);
                    return;
                } else {
                    LOGGER.warn("Login failed: password and token verification failed for user {}", user.getName());
                    request.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.BAD_PASSWORD);
                    return;
                }
            }
        }

        valveContext.invokeNext(context);
    }

    private boolean verifyCredentials(JCRUserNode user, String password, String token) {
        return user.verifyPassword(password) && verifyToken(user, token, password);
    }

    private boolean verifyToken(JCRUserNode user, String token, String password) {
        try {
            if (user.hasNode(MFAConstants.NODE_NAME_MFA)) {
                final JCRNodeWrapper node = user.getNode(MFAConstants.NODE_NAME_MFA);
                if (node.hasProperty(MFAConstants.PROP_ACTIVATED) && node.getProperty(MFAConstants.PROP_ACTIVATED).getBoolean()
                        && node.hasProperty(MFAConstants.PROP_PROVIDER)) {
                    return jahiaMFAService.verifyToken(user, node.getPropertyAsString(MFAConstants.PROP_PROVIDER), token, password);
                }
            }
        } catch (RepositoryException ex) {
            LOGGER.warn(String.format("Unable to read MFA configuration for user: %s", user.getName()), ex);
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
        return getContextRequestURL(request) + "/" + MFAServlet.CONTEXT;
    }

    @Override
    public boolean hasCustomLoginUrl() {
        return true;
    }

    private static String extractTokenFromRequest(HttpServletRequest request) {
        final String digit1 = request.getParameter("digit-1");
        final String digit2 = request.getParameter("digit-2");
        final String digit3 = request.getParameter("digit-3");
        final String digit4 = request.getParameter("digit-4");
        final String digit5 = request.getParameter("digit-5");
        final String digit6 = request.getParameter("digit-6");
        return new StringBuilder().append(StringUtils.defaultIfEmpty(digit1, "0"))
                .append(StringUtils.defaultIfEmpty(digit2, "0"))
                .append(StringUtils.defaultIfEmpty(digit3, "0"))
                .append(StringUtils.defaultIfEmpty(digit4, "0"))
                .append(StringUtils.defaultIfEmpty(digit5, "0"))
                .append(StringUtils.defaultIfEmpty(digit6, "0"))
                .toString();

    }

    private static String getContextRequestURL(HttpServletRequest httpServletRequest) {
        String baseRequestURL;
        baseRequestURL = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName();
        if (("http".equals(httpServletRequest.getScheme()) && (httpServletRequest.getServerPort() == HttpURL.DEFAULT_PORT))
                || ("https".equals(httpServletRequest.getScheme()) && (httpServletRequest.getServerPort() == HttpsURL.DEFAULT_PORT))) {
            // normal case, don't add the port
        } else {
            baseRequestURL += ":" + httpServletRequest.getServerPort();
        }
        return baseRequestURL + httpServletRequest.getContextPath();
    }

}
