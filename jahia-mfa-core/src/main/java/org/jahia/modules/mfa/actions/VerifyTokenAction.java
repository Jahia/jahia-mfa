package org.jahia.modules.mfa.actions;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.graphql.extensions.Utils;
import org.jahia.modules.mfa.impl.JahiaMFAServiceImpl;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Action to verify the sent token against the TOTP key of the user
 */
@Deprecated
public class VerifyTokenAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyTokenAction.class);

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
            JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        final JCRUserNode userNode = session.getUserNode();
        try {
            final String password = Utils.retrieveParameterValue(parameters, MFAConstants.PARAM_PASSWORD);
            final String provider = Utils.retrieveParameterValue(parameters, MFAConstants.PARAM_PROVIDER);
            final String token = Utils.retrieveParameterValue(parameters, MFAConstants.PARAM_TOKEN);

            if (password != null && provider != null && token != null && Utils.isCorrectUser(userNode)) {
                final JahiaMFAServiceImpl jahiaMFAServiceImpl = JahiaMFAServiceImpl.getInstance();
                final boolean result = jahiaMFAServiceImpl.verifyToken(userNode, provider, token, password);
                return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject().put("result", result));
            }
        } catch (Exception ex) {
            LOGGER.error(String.format("Impossible to verity token for user %s", userNode.getPath()), ex);
            return ActionResult.BAD_REQUEST;
        }
        return ActionResult.BAD_REQUEST;
    }

}
