package org.jahia.modules.mfa.otp.graphql.extensions;

import graphql.annotations.annotationTypes.*;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.graphql.provider.dxm.DXGraphQLProvider;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.graphql.extensions.Utils;
import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;

@GraphQLTypeExtension(DXGraphQLProvider.Query.class)
public final class VerifyMFAStatusExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyMFAStatusExtension.class);

    private VerifyMFAStatusExtension() {
    }

    // Suppress 8 param warning
    @GraphQLField
    @GraphQLName("verifyMFAStatus")
    @GraphQLDescription("verify the MFA status for the current user")
    public static boolean verifyMFAStatusExtension( ) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("verifying MFA Status");
        }

        final JahiaMFAService jahiaMFAService = (JahiaMFAService) SpringContextSingleton.getBean("jahiaMFAServiceImpl");
        final JCRUserNode userNode = Utils.getUserNode(JCRSessionFactory.getInstance().getCurrentUser());

        if (jahiaMFAService != null && userNode != null) {
                    return jahiaMFAService.hasMFA(userNode);
                    }
        return false;
                }
            }
