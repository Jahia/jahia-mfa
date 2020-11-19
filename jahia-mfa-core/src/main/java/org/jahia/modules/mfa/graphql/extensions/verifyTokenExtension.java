package org.jahia.modules.mfa.graphql.extensions;

import graphql.annotations.annotationTypes.*;
import org.apache.commons.lang.StringUtils;
import org.jahia.bin.ActionResult;
import org.jahia.modules.graphql.provider.dxm.DXGraphQLProvider;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.actions.Utils;
import org.jahia.modules.mfa.impl.JahiaMFAServiceImpl;
import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;

@GraphQLTypeExtension(DXGraphQLProvider.Query.class)
public class verifyTokenExtension {
    private static Logger logger = LoggerFactory.getLogger(verifyTokenExtension.class);

    @GraphQLField
    @GraphQLName("verifyToken")
    @GraphQLDescription("verify Token")
    public static boolean verifyMFAEnforcement(
            @GraphQLName(MFAConstants.PARAM_PASSWORD) @GraphQLDescription("password") @GraphQLNonNull String password,
            @GraphQLName(MFAConstants.PARAM_PROVIDER) @GraphQLDescription("site key") @GraphQLNonNull String provider,
            @GraphQLName(MFAConstants.PARAM_TOKEN) @GraphQLDescription("MFA Token") @GraphQLNonNull String token
            ){
        logger.info("veriyfing token");
        JahiaMFAService jahiaMFAService = (JahiaMFAService) SpringContextSingleton.getBean("jahiaMFAServiceImpl");
        if (jahiaMFAService != null) {
                final JCRUserNode userNode = (JCRUserNode) JCRSessionFactory.getInstance().getCurrentUser();
                try {
                    if (password != null && provider != null && token != null && Utils.isCorrectUser(userNode)) {
                        final boolean result = jahiaMFAService.verifyToken(userNode, provider, token, password);
                        return result;
                    }
                } catch (Exception ex) {
                    logger.error(String.format("Impossible to verity token for user %s", userNode.getPath()), ex);
                }
            }
        return false;
    }
}

