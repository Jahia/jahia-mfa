package org.jahia.modules.mfa.impl;

import java.util.HashMap;
import java.util.Map;
import org.jahia.modules.mfa.provider.JahiaMFAProvider;
import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.services.content.decorator.JCRUserNode;

public final class JahiaMFAServiceImpl implements JahiaMFAService {

    private Map<String, JahiaMFAProvider> providers = new HashMap<>();

    @Override
    public void addProvider(JahiaMFAProvider provider) {
        providers.put(provider.getKey(), provider);
    }

    @Override
    public void removeProvider(JahiaMFAProvider provider) {
        providers.remove(provider.getKey());
    }

    @Override
    public boolean verifyToken(JCRUserNode userNode, String provider, String token) {
        return providers.get(provider).verifyToken(userNode, token);
    }
}
