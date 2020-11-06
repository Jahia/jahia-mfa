package org.jahia.modules.mfa.otp.provider.actions;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.actions.Utils;
import org.jahia.modules.mfa.otp.provider.JahiaMFAOtpProvider;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecryptOtpKeyAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecryptOtpKeyAction.class);

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource,
            JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        final JCRUserNode userNode = session.getUserNode();
        try {
            final String password = Utils.retrieveParameterValue(parameters, MFAConstants.PARAM_PASSWORD);

            if (password != null && Utils.isCorrectUser(userNode)) {
                final JCRNodeWrapper mfaNode = userNode.getNode(MFAConstants.NODE_NAME_MFA);
                final String encryptedSecretKey = mfaNode.getPropertyAsString(org.jahia.modules.mfa.otp.provider.Constants.PROP_SECRET_KEY);
                final String result = JahiaMFAOtpProvider.decryptTotpSecretKey(encryptedSecretKey, password, userNode.getIdentifier());
                return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject().put("result", result));
            }
        } catch (Exception ex) {
            LOGGER.error(String.format("Impossible to decrypt OPT key for user %s", userNode.getPath()), ex);
            return ActionResult.BAD_REQUEST;
        }
        return ActionResult.BAD_REQUEST;
    }

}
