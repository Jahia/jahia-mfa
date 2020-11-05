package org.jahia.modules.mfa.actions;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jahia.api.Constants;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.impl.JahiaMFAServiceImpl;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyTokenAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyTokenAction.class);

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
            JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        final JCRUserNode userNode = session.getUserNode();
        try {

            String password = null;
            if (parameters.containsKey(MFAConstants.PARAM_PASSWORD)) {
                final List<String> passwordValues = parameters.get(MFAConstants.PARAM_PASSWORD);
                if (!passwordValues.isEmpty()) {
                    password = passwordValues.get(0);
                }
            }

            String provider = null;
            if (parameters.containsKey(MFAConstants.PARAM_PROVIDER)) {
                final List<String> providerValues = parameters.get(MFAConstants.PARAM_PROVIDER);
                if (!providerValues.isEmpty()) {
                    provider = providerValues.get(0);
                }
            }

            String token = null;
            if (parameters.containsKey(MFAConstants.PARAM_TOKEN)) {
                final List<String> tokenValues = parameters.get(MFAConstants.PARAM_TOKEN);
                if (!tokenValues.isEmpty()) {
                    token = tokenValues.get(0);
                }
            }

            if (password != null && provider != null && token != null && userNode != null && !userNode.getJahiaUser().getUsername().equals(Constants.GUEST_USERNAME)) {
                final JahiaMFAServiceImpl jahiaMFAServiceImpl = JahiaMFAServiceImpl.getInstance();
                final boolean result = jahiaMFAServiceImpl.verifyToken(userNode, provider, token, password);
                final ActionResult actionResult = new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject().put("result", result));
                return actionResult;
            }
        } catch (Exception ex) {
            LOGGER.error(String.format("Impossible to verity token for user %s", userNode.getPath()), ex);
            return ActionResult.BAD_REQUEST;
        }
        return ActionResult.BAD_REQUEST;
    }

}
