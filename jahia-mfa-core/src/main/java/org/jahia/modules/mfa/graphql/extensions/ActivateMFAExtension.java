package org.jahia.modules.mfa.graphql.extensions;

import graphql.annotations.annotationTypes.*;
import org.jahia.modules.graphql.provider.dxm.DXGraphQLProvider;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.decorator.JCRUserNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search entry point for v1
 */
@GraphQLTypeExtension(DXGraphQLProvider.Query.class)
public class ActivateMFAExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateMFAExtension.class);

    private ActivateMFAExtension() {
    }

    // Suppress 8 param warning
    @GraphQLField
    @GraphQLName("activateMFA")
    @GraphQLDescription("Activate MFA")
    public static boolean activateMFAExtension(
            @GraphQLName(MFAConstants.PARAM_PROVIDER) @GraphQLDescription("MFA Provider") @GraphQLNonNull String provider,
            @GraphQLName(MFAConstants.PARAM_ACTIVATE) @GraphQLDescription("Activate or Deactivate MFA") @GraphQLNonNull Boolean activation
    ) {
        JahiaMFAService jahiaMFAService = (JahiaMFAService) SpringContextSingleton.getBean("jahiaMFAServiceImpl");
        final JCRUserNode userNode = Utils.getUserNode(JCRSessionFactory.getInstance().getCurrentUser());
        if (jahiaMFAService != null) {
            try {
                if (provider != null && Utils.isCorrectUser(userNode)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(String.format("ActivateMFAAction for user %s", userNode.getName()));
                    }
                    if (Boolean.TRUE.equals(activation)) {
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("activating MFA");
                        }
                        jahiaMFAService.activateMFA(userNode, provider);
                    } else {
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("deactivating MFA");
                        }
                        jahiaMFAService.deactivateMFA(userNode, provider);
                    }
                    return true;
                }
            } catch (Exception ex) {
                LOGGER.error(String.format("Impossible to prepare MFA for user %s", userNode.getPath()), ex);
                return false;
            }
        }
        return false;
    }
}
