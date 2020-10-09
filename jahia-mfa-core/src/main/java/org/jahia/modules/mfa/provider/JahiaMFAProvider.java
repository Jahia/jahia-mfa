package org.jahia.modules.mfa.provider;

import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.services.content.decorator.JCRUserNode;

public abstract class JahiaMFAProvider {

    private final String key;
    private JahiaMFAService jahiaMFAService;

    public JahiaMFAProvider(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public final void setJahiaMFAService(JahiaMFAService jahiaMFAService) {
        this.jahiaMFAService = jahiaMFAService;
    }

    public final JahiaMFAService getJahiaMFAService() {
        return jahiaMFAService;
    }

    public final void register() {
        jahiaMFAService.addProvider(this);
    }

    public final void unregister() {
        jahiaMFAService.removeProvider(this);
    }

    public abstract boolean verifyToken(JCRUserNode userNode, String token);

    public abstract boolean activateMFA(JCRUserNode userNode);

    public abstract boolean deactivateMFA(JCRUserNode userNode);
}
