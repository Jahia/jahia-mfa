package org.jahia.modules.mfa.graphql.extensions;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.decorator.JCRUserNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MFA mutation type
 */
@GraphQLName("MFAMutation")
@GraphQLDescription("Mutations MFA")
public class GqlMFAMutation {

    private static final Logger LOGGER = LoggerFactory.getLogger(GqlMFAMutation.class);

    protected GqlMFAMutation() {
    }

    // Suppress 8 param warning
    @GraphQLField
    @GraphQLName("activateMFA")
    @GraphQLDescription("Activate MFA")
    public boolean activateMFA(
            @GraphQLName(MFAConstants.PARAM_PROVIDER) @GraphQLDescription("MFA Provider") @GraphQLNonNull String provider,
            @GraphQLName(MFAConstants.PARAM_ACTIVATE) @GraphQLDescription("Activate or Deactivate MFA") @GraphQLNonNull Boolean activation
    ) {
        JahiaMFAService jahiaMFAService = (JahiaMFAService) SpringContextSingleton.getBean(MFAConstants.BEAN_MFA_SERVICE);
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

    @GraphQLField
    @GraphQLName("prepareMFA")
    @GraphQLDescription("Preparing MFA")
    public boolean prepareMFA(
            @GraphQLName(MFAConstants.PARAM_PASSWORD) @GraphQLDescription("password") @GraphQLNonNull String password,
            @GraphQLName(MFAConstants.PARAM_PROVIDER) @GraphQLDescription("provider") String provider
    ) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("preparing MFA Enforcement");
        }
        JahiaMFAService jahiaMFAService = (JahiaMFAService) SpringContextSingleton.getBean(MFAConstants.BEAN_MFA_SERVICE);
        if (jahiaMFAService != null) {
            final JCRUserNode userNode = Utils.getUserNode(JCRSessionFactory.getInstance().getCurrentUser());
            try {
                if (password != null && provider != null && Utils.isCorrectUser(userNode)) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(String.format("ActivateMFAAction for user %s", userNode.getName()));
                    }
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
