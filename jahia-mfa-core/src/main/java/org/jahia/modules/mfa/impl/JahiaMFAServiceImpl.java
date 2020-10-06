package org.jahia.modules.mfa.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.RepositoryException;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.provider.JahiaMFAProvider;
import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.decorator.JCRUserNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JahiaMFAServiceImpl implements JahiaMFAService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JahiaMFAServiceImpl.class);
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

    @Override
    public void activateMFA(JCRUserNode userNode, String provider) {
        try {
            JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Object>() {
                @Override
                public Object doInJCR(JCRSessionWrapper jcrsession) throws RepositoryException {
                    final JCRNodeWrapper defautUserNode = jcrsession.getNode(userNode.getPath());
                    defautUserNode.addMixin(MFAConstants.MIXIN_MFA_USER);
                    final JCRNodeWrapper mfaNode = defautUserNode.addNode(MFAConstants.NODE_NAME_MFA, MFAConstants.NODE_TYPE_MFA);
                    mfaNode.setProperty(MFAConstants.PROP_ACTIVATED, Boolean.TRUE);
                    mfaNode.setProperty(MFAConstants.PROP_PROVIDER, provider);
                    jcrsession.save();
                    return null;
                }
            });
        } catch (RepositoryException ex) {
            LOGGER.error(String.format("Impossible to actiate MFA for user %s and provider %s", userNode.getUserKey(), provider), ex);
        }
    }

    @Override
    public void deactivateMFA(JCRUserNode userNode, String providey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
