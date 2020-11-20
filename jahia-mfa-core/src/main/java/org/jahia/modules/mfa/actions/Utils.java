package org.jahia.modules.mfa.actions;

import java.util.List;
import java.util.Map;
import org.jahia.api.Constants;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;

/**
 * Utilities class used in the actions
 */
public final class Utils {

    private Utils() {
    }

    /**
     * Check if the user has the MFA OTP provider activated
     *
     * @param parameters passed to the Jahia action
     * @param name of the parameter to retrieve
     * @return the parameter value, null if it does not exists
     */
    public static String retrieveParameterValue(Map<String, List<String>> parameters, String name) {
        if (parameters.containsKey(name)) {
            final List<String> values = parameters.get(name);
            if (!values.isEmpty()) {
                return values.get(0);
            }
        }
        return null;
    }

    
    /**
     * Check if the user exists and is not guest
     *
     * @param userNode defined in the session
     * @return true if the user exists and is not guest, false otherwise
     */
    public static boolean isCorrectUser(JCRUserNode userNode) {
        return userNode != null && !userNode.getJahiaUser().getUsername().equals(Constants.GUEST_USERNAME);
    }

    /**
     * Convert JahiaUser into JCRUserNode
     *
     * @param jahiaUser
     * @return JCRUserNode
     */
    public static JCRUserNode getUserNode(JahiaUser jahiaUser) {
        if (jahiaUser == null)
            return null;
        return JahiaUserManagerService.getInstance().lookupUserByPath(jahiaUser.getLocalPath());
    }
}
