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
public class VerifyTokenExtension {
    private static final Logger logger = LoggerFactory.getLogger(VerifyTokenExtension.class);

    @GraphQLField
    @GraphQLName("verifyToken")
    @GraphQLDescription("verify Token")
    public static boolean verifyTokenExtension(
            @GraphQLName(MFAConstants.PARAM_PASSWORD) @GraphQLDescription("password") @GraphQLNonNull String password,
            @GraphQLName(MFAConstants.PARAM_PROVIDER) @GraphQLDescription("site key") @GraphQLNonNull String provider,
            @GraphQLName(MFAConstants.PARAM_TOKEN) @GraphQLDescription("MFA Token") @GraphQLNonNull String token
            ){
        logger.info("veriyfing token");
        JahiaMFAService jahiaMFAService = (JahiaMFAService) SpringContextSingleton.getBean("jahiaMFAServiceImpl");
        if (jahiaMFAService != null) {
                final JCRUserNode userNode = Utils.getUserNode(JCRSessionFactory.getInstance().getCurrentUser());
                try {
                    if (password != null && provider != null && token != null && Utils.isCorrectUser(userNode)) {
                        return jahiaMFAService.verifyToken(userNode, provider, token, password);
                    }
                } catch (Exception ex) {
                    logger.error(String.format("Impossible to verity token for user %s", userNode.getPath()), ex);
                }
            }
        return false;
    }
}

