package org.jahia.modules.mfa.graphql.extensions;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import javax.jcr.RepositoryException;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.impl.JahiaMFAServiceImpl;
import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PersonalApiTokens mutation type
 */
@GraphQLName("MFAQuery")
@GraphQLDescription("Queries MFA")
public class GqlMFAQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(GqlMFAQuery.class);

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

        final JahiaMFAService jahiaMFAService = JahiaMFAServiceImpl.getInstance();
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
                JCRUserNode usernode;
                // lookup for the user at the system level
                usernode = JahiaUserManagerService.getInstance().lookupUser(username);
                if (usernode == null) {
                    // if not found, lookup for the user at the site level
                    usernode = JahiaUserManagerService.getInstance().lookupUser(username,siteKey);
                }
                if (usernode != null && jahiaMFAService.hasMFA(usernode)) {
                    userHasMFA = true;
                }

            }
        }
        return siteEnforceMFA || userHasMFA;
    }

    @GraphQLField
    @GraphQLName("verifyToken")
    @GraphQLDescription("verify Token")
    public boolean verifyTokenExtension(
            @GraphQLName(MFAConstants.PARAM_PASSWORD) @GraphQLDescription("password") @GraphQLNonNull String password,
            @GraphQLName(MFAConstants.PARAM_PROVIDER) @GraphQLDescription("provider") @GraphQLNonNull String provider,
            @GraphQLName(MFAConstants.PARAM_TOKEN) @GraphQLDescription("MFA Token") @GraphQLNonNull String token
    ) {
        LOGGER.info("verifying token");
        final JahiaMFAService jahiaMFAService = JahiaMFAServiceImpl.getInstance();
        if (jahiaMFAService != null) {
            final JCRUserNode userNode = Utils.getUserNode(JCRSessionFactory.getInstance().getCurrentUser());
            try {
                if (password != null && provider != null && token != null && Utils.isCorrectUser(userNode)) {
                    return jahiaMFAService.verifyToken(userNode, provider, token, password);
                }
            } catch (Exception ex) {
                LOGGER.error(String.format("Impossible to verity token for user %s", userNode.getPath()), ex);
            }
        }
        return false;
    }

}
