package org.jahia.modules.mfa.impl;

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
    public boolean verifyToken(JCRUserNode userNode, String provider, String token, String password) {
        return providers.get(provider).verifyToken(userNode, token, password);
    }

    @Override
    public void activateMFA(JCRUserNode userNode, String provider, String password) {
        try {
            JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Object>() {
                @Override
                public Object doInJCR(JCRSessionWrapper jcrsession) throws RepositoryException {
                    final JCRNodeWrapper defaultUserNode = jcrsession.getNode(userNode.getPath());
                    defaultUserNode.addMixin(MFAConstants.MIXIN_MFA_USER);
                    final JCRNodeWrapper mfaNode;
                    if (defaultUserNode.hasNode(MFAConstants.NODE_NAME_MFA)) {
                        mfaNode = defaultUserNode.getNode(MFAConstants.NODE_NAME_MFA);
                    } else {
                        mfaNode = defaultUserNode.addNode(MFAConstants.NODE_NAME_MFA, MFAConstants.NODE_TYPE_MFA);
                    }

                    mfaNode.setProperty(MFAConstants.PROP_ACTIVATED, Boolean.TRUE);
                    mfaNode.setProperty(MFAConstants.PROP_PROVIDER, provider);
                    jcrsession.save();
                    providers.get(provider).activateMFA(userNode, password);
                    return null;
                }
            });
        } catch (RepositoryException ex) {
            LOGGER.error(String.format("Impossible to activate MFA for user %s and provider %s", userNode.getUserKey(), provider), ex);
        }
    }

    @Override
    public void deactivateMFA(JCRUserNode userNode, String provider) {
        try {
            JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Object>() {
                @Override
                public Object doInJCR(JCRSessionWrapper jcrsession) throws RepositoryException {
                    final JCRNodeWrapper defaultUserNode = jcrsession.getNode(userNode.getPath());
                    final JCRNodeWrapper mfaNode = defaultUserNode.getNode(MFAConstants.NODE_NAME_MFA);
                    mfaNode.setProperty(MFAConstants.PROP_ACTIVATED, Boolean.FALSE);
                    jcrsession.save();
                    providers.get(provider).deactivateMFA(userNode);
                    return null;
                }
            });
        } catch (RepositoryException ex) {
            LOGGER.error(String.format("Impossible to activate MFA for user %s and provider %s", userNode.getUserKey(), provider), ex);
        }
    }

    @Override
    public boolean hasMFA(JCRUserNode userNode) {
        try {
            return JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<Boolean>() {
                @Override
                public Boolean doInJCR(JCRSessionWrapper jcrsession) throws RepositoryException {
                    final JCRNodeWrapper defaultUserNode = jcrsession.getNode(userNode.getPath());
                    if (defaultUserNode.hasNode(MFAConstants.NODE_NAME_MFA)) {
                        final JCRNodeWrapper mfaNode = defaultUserNode.getNode(MFAConstants.NODE_NAME_MFA);
                        return mfaNode.hasProperty(MFAConstants.PROP_ACTIVATED) && mfaNode.getProperty(MFAConstants.PROP_ACTIVATED).getBoolean();
                    } else {
                        return false;
                    }
                }
            });
        } catch (RepositoryException ex) {
            LOGGER.error(String.format("Impossible to get MFA status for user %s", userNode.getUserKey()), ex);
            return false;
        }
    }
}
