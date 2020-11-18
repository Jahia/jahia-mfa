package org.jahia.modules.mfa.graphql.extensions;

import graphql.annotations.annotationTypes.*;
import java.util.Locale;
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
import org.jahia.utils.LanguageCodeConverters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search entry point for v1
 */
@GraphQLTypeExtension(DXGraphQLProvider.Query.class)
public class MFAGraphQLExtension {
    private static Logger logger = LoggerFactory.getLogger(MFAGraphQLExtension.class);

    // Suppress 8 param warning
    @GraphQLField
    @GraphQLName("verifyMFAEnforcement")
    @GraphQLDescription("verify MFA Enforcement")
    public static boolean verifyMFAEnforcement(
            @GraphQLName("username") @GraphQLDescription("username of current user") @GraphQLNonNull String username,
            @GraphQLName("sitekey") @GraphQLDescription("site key") String siteKey
    ) throws RepositoryException {
        boolean siteEnforceMFA = false;
        boolean userHasMFA = false;
        logger.info("Running search");
        JahiaMFAService jahiaMFAService = (JahiaMFAService) SpringContextSingleton.getBean("jahiaMFAServiceImpl");
        if (jahiaMFAService != null) {
            String language = "en";
            String workspace = "live";
            Locale locale = LanguageCodeConverters.languageCodeToLocale(language);
            //JCRSessionWrapper session = JCRSessionFactory.getInstance().getCurrentUserSession(workspace.getValue(), locale);
            try {

                //final String siteKey = ServerNameToSiteMapper.getSiteKeyByServerName(httpServletRequest);


                if (!StringUtils.isEmpty(siteKey)) {
                    try {
                        // ServiceRegistry.getIn
                        // final JahiaSitesService siteService = JahiaSitesService.getInstance();
                        JCRSessionWrapper session = JCRSessionFactory.getInstance().getCurrentSystemSession(null, null,null);

                        JCRSiteNode sitenode = (JCRSiteNode) session.getNode("/sites/" + siteKey);
                        if (sitenode.isNodeType(MFAConstants.MIXIN_MFA_SITE)){
                            if (sitenode.hasProperty(MFAConstants.PROP_ENFORCEMFA) &&
                                    sitenode.getPropertyAsString(MFAConstants.PROP_ENFORCEMFA).equals("true")){
                                siteEnforceMFA = true;
                            }
                        }
                    } catch (RepositoryException ex) {
                        logger.error(String.format("MFA Enforcement could not find site matching that servername"), ex);
                    }
                }

                if (!StringUtils.isEmpty(username)) {
                    logger.debug("VerifyMFAEnforcementAction for user "+username);
                    JCRUserNode usernode = JahiaUserManagerService.getInstance().lookupUser(username);
                    if(usernode!=null && jahiaMFAService.hasMFA(usernode)){
                        userHasMFA = true;
                    }

                }
    } catch (Exception e) {
                // ${TODO} Auto-generated catch block
                e.printStackTrace();
            }

        }
        return siteEnforceMFA && userHasMFA;
    }
}

