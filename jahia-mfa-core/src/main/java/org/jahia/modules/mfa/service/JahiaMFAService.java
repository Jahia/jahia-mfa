package org.jahia.modules.mfa.service;

import org.jahia.modules.mfa.provider.JahiaMFAProvider;
import org.jahia.services.content.decorator.JCRUserNode;

public interface JahiaMFAService {

    void addProvider(JahiaMFAProvider provider);

    void removeProvider(JahiaMFAProvider provider);

    boolean verifyToken(JCRUserNode userNode, String provider, String token, String password);

    void prepareMFA(JCRUserNode userNode, String provider, String password);

    void activateMFA(JCRUserNode userNode, String provider);

    void deactivateMFA(JCRUserNode userNode, String provider);

    boolean hasMFA(JCRUserNode userNode);
}
