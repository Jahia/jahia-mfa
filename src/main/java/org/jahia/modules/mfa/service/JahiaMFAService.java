package org.jahia.modules.mfa.service;

import org.jahia.modules.mfa.provider.JahiaMFAProvider;
import org.jahia.services.content.decorator.JCRUserNode;

public interface JahiaMFAService {

    void addProvider(JahiaMFAProvider provider);

    void removeProvider(JahiaMFAProvider provider);

    boolean verifyToken(JCRUserNode userNode, String provider, String token);
}
