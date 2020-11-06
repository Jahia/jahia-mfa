package org.jahia.modules.mfa.actions;

import java.util.List;
import java.util.Map;
import org.jahia.api.Constants;
import org.jahia.services.content.decorator.JCRUserNode;

public final class Utils {

    private Utils() {
    }

    public static String retrieveParameterValue(Map<String, List<String>> parameters, String name) {
        if (parameters.containsKey(name)) {
            final List<String> values = parameters.get(name);
            if (!values.isEmpty()) {
                return values.get(0);
            }
        }
        return null;
    }

    public static boolean isCorrectUser(JCRUserNode userNode) {
        return userNode != null && !userNode.getJahiaUser().getUsername().equals(Constants.GUEST_USERNAME);
    }
}
