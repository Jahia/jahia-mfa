package org.jahia.modules.mfa.graphql.extensions;

import graphql.annotations.annotationTypes.*;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.graphql.provider.dxm.DXGraphQLProvider;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GraphQLTypeExtension(DXGraphQLProvider.Query.class)
public final class VerifyMFAEnforcementExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyMFAEnforcementExtension.class);

    private VerifyMFAEnforcementExtension() {
    }

    // Suppress 8 param warning
    @GraphQLField
    @GraphQLName("verifyMFAEnforcement")
    @GraphQLDescription("verify MFA Enforcement")
    public static boolean verifyMFAEnforcementExtension(
            @GraphQLName(MFAConstants.PARAM_USERNAME) @GraphQLDescription("username of current user") @GraphQLNonNull String username,
            @GraphQLName(MFAConstants.PARAM_SITEKEY) @GraphQLDescription("site key") @GraphQLNonNull String siteKey
    ) {
        boolean siteEnforceMFA = false;
        boolean userHasMFA = false;
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("verifying MFA Enforcement");
        }

        final JahiaMFAService jahiaMFAService = (JahiaMFAService) SpringContextSingleton.getBean("jahiaMFAServiceImpl");
        if (jahiaMFAService != null) {
            if (!StringUtils.isEmpty(siteKey)) {
                try {
                    final JCRSessionWrapper session = JCRSessionFactory.getInstance().getCurrentSystemSession(null, null, null);

                    final JCRSiteNode sitenode = (JCRSiteNode) session.getNode("/sites/" + siteKey);
                    if (sitenode.isNodeType(MFAConstants.MIXIN_MFA_SITE) && sitenode.hasProperty(MFAConstants.PROP_ENFORCEMFA)
                            && sitenode.getPropertyAsString(MFAConstants.PROP_ENFORCEMFA).equals("true")) {
                        siteEnforceMFA = true;
                    }

                } catch (RepositoryException ex) {
                    LOGGER.error("MFA Enforcement could not find site matching that servername", ex);
                }
            }

            if (!StringUtils.isEmpty(username)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(String.format("VerifyMFAEnforcementAction for user %s", username));
                }
                JCRUserNode usernode = JahiaUserManagerService.getInstance().lookupUser(username);
                if (usernode != null && jahiaMFAService.hasMFA(usernode)) {
                    userHasMFA = true;
                }

            }
        }
        return siteEnforceMFA && userHasMFA;
    }
}
