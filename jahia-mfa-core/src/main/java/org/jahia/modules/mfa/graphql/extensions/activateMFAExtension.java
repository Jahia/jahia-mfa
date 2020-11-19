package org.jahia.modules.mfa.graphql.extensions;

import graphql.annotations.annotationTypes.*;
import org.jahia.bin.ActionResult;
import org.jahia.modules.graphql.provider.dxm.DXGraphQLProvider;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.actions.Utils;
import org.jahia.modules.mfa.impl.JahiaMFAServiceImpl;
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
public class activateMFAExtension {
    private static Logger LOGGER = LoggerFactory.getLogger(activateMFAExtension.class);

    // Suppress 8 param warning
    @GraphQLField
    @GraphQLName("activateMFA")
    @GraphQLDescription("Activate MFA")
    public static boolean activateMFAExtension(
            @GraphQLName(MFAConstants.PARAM_PROVIDER) @GraphQLDescription("MFA Provider") @GraphQLNonNull String provider,
            @GraphQLName("activateMFA") @GraphQLDescription("Activate or Deactivate MFA") @GraphQLNonNull Boolean activation
    ){
        JahiaMFAService jahiaMFAService = (JahiaMFAService) SpringContextSingleton.getBean("jahiaMFAServiceImpl");
        final JCRUserNode userNode = (JCRUserNode) JCRSessionFactory.getInstance().getCurrentUser();
        if (jahiaMFAService != null) {
            try {
                if (provider != null && userNode != null && Utils.isCorrectUser(userNode)) {
                    LOGGER.debug("ActivateMFAAction for user "+userNode.getName());
                    if (activation){
                        LOGGER.info("activating MFA");
                        jahiaMFAService.activateMFA(userNode, provider);
                    }else{
                        LOGGER.info("deactivating MFA");
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

