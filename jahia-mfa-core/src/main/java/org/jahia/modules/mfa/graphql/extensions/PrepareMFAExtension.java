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

@GraphQLTypeExtension(DXGraphQLProvider.Query.class)
public class PrepareMFAExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrepareMFAExtension.class);

    @GraphQLField
    @GraphQLName("prepareMFA")
    @GraphQLDescription("Preparing MFA")
    public static boolean prepareMFAExtension(
            @GraphQLName(MFAConstants.PARAM_PASSWORD) @GraphQLDescription("password") @GraphQLNonNull String password,
            @GraphQLName(MFAConstants.PARAM_PROVIDER) @GraphQLDescription("provider") String provider
    ) {
        LOGGER.info("preparing MFA Enforcement");
        JahiaMFAService jahiaMFAService = (JahiaMFAService) SpringContextSingleton.getBean("jahiaMFAServiceImpl");
        if (jahiaMFAService != null) {
            final JCRUserNode userNode = Utils.getUserNode(JCRSessionFactory.getInstance().getCurrentUser());
            try {
                if (password != null && provider != null && Utils.isCorrectUser(userNode)) {
                    LOGGER.debug("ActivateMFAAction for user " + userNode.getName());
                    jahiaMFAService.prepareMFA(userNode, provider, password);
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
