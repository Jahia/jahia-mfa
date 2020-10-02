package org.jahia.modules.mfa.provider;

import org.jahia.services.content.decorator.JCRUserNode;

public abstract class JahiaMFAProvider {

    private final String key;

    public JahiaMFAProvider(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public abstract boolean verifyToken(JCRUserNode userNode, String token);
}
