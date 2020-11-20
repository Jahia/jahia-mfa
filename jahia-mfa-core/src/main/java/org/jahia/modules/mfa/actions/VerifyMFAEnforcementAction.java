/*
 * ==========================================================================================
 * =                            JAHIA'S ENTERPRISE DISTRIBUTION                             =
 * ==========================================================================================
 *
 *                                  http://www.jahia.com
 *
 * JAHIA'S ENTERPRISE DISTRIBUTIONS LICENSING - IMPORTANT INFORMATION
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2020 Jahia Solutions Group. All rights reserved.
 *
 *     This file is part of a Jahia's Enterprise Distribution.
 *
 *     Jahia's Enterprise Distributions must be used in accordance with the terms
 *     contained in the Jahia Solutions Group Terms &amp; Conditions as well as
 *     the Jahia Sustainable Enterprise License (JSEL).
 *
 *     For questions regarding licensing, support, production usage...
 *     please contact our team at sales@jahia.com or go to http://www.jahia.com/license.
 *
 * ==========================================================================================
 */
package org.jahia.modules.mfa.actions;

import java.util.List;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.seo.urlrewrite.ServerNameToSiteMapper;
import org.jahia.services.sites.JahiaSite;
import org.jahia.services.sites.JahiaSitesService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifyMFAEnforcementAction extends Action {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyMFAEnforcementAction.class);
    private org.jahia.api.usermanager.JahiaUserManagerService jahiaUserManagerService;
    private JahiaMFAService jahiaMFAService;
    private JahiaSitesService jahiaSitesService;

    public void setJahiaUserManagerService(org.jahia.api.usermanager.JahiaUserManagerService jahiaUserManagerService) {
        this.jahiaUserManagerService = jahiaUserManagerService;
    }

    public void setJahiaMFAService(JahiaMFAService jahiaMFAService) {
        this.jahiaMFAService = jahiaMFAService;
    }

    public void setJahiaSitesService(JahiaSitesService jahiaSitesService) {
        this.jahiaSitesService = jahiaSitesService;
    }

    @Override
    public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource,
            JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

        boolean siteEnforceMFA = false;
        boolean userHasMFA = false;

        final String username = Utils.retrieveParameterValue(parameters, MFAConstants.PARAM_USERNAME);
        final String siteKey = ServerNameToSiteMapper.getSiteKeyByServerName(httpServletRequest);

        if (!StringUtils.isEmpty(siteKey)) {
            try {
                // final JahiaSitesService siteService = JahiaSitesService.getInstance();
                final JahiaSite site = jahiaSitesService.getSiteByKey(siteKey, JCRSessionFactory.getInstance().getCurrentSystemSession(null, null, null));
                final JCRSiteNode sitenode = (JCRSiteNode) jcrSessionWrapper.getNode("/sites/" + site.getSiteKey());
                if (sitenode.isNodeType(MFAConstants.MIXIN_MFA_SITE) && sitenode.hasProperty(MFAConstants.PROP_ENFORCEMFA)
                        && sitenode.getPropertyAsString(MFAConstants.PROP_ENFORCEMFA).equals("true")) {
                    siteEnforceMFA = true;
                }
            } catch (RepositoryException ex) {
                LOGGER.error(String.format("MFA Enforcement could not find site matching that servername: %s", siteKey), ex);
            }
        }

        if (!StringUtils.isEmpty(username)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(String.format("VerifyMFAEnforcementAction for user %s", username));
            }
            final JCRUserNode usernode = jahiaUserManagerService.lookupUser(username);
            if (usernode != null && jahiaMFAService.hasMFA(usernode)) {
                userHasMFA = true;
            }

        }
        return new ActionResult(HttpServletResponse.SC_OK, null, new JSONObject().put("result", userHasMFA && siteEnforceMFA));

    }
}
