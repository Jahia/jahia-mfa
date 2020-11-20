package org.jahia.modules.mfa.actions;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.impl.JahiaMFAServiceImpl;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to deactivate MFA for a user
 */
public final class DeactivateMFAAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateMFAAction.class);

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
            JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        final JCRUserNode userNode = session.getUserNode();
        try {
            final String provider = Utils.retrieveParameterValue(parameters, MFAConstants.PARAM_PROVIDER);

            if (provider != null && Utils.isCorrectUser(userNode)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("ActivateMFAAction for user %s", userNode.getName()));
                }
                final JahiaMFAServiceImpl jahiaMFAServiceImpl = JahiaMFAServiceImpl.getInstance();
                jahiaMFAServiceImpl.deactivateMFA(userNode, provider);
                return ActionResult.OK_JSON;
            }
        } catch (Exception ex) {
            LOGGER.error(String.format("Impossible to deactivate MFA for user %s", userNode.getPath()), ex);
            return ActionResult.BAD_REQUEST;
        }
        return ActionResult.BAD_REQUEST;
    }

}
