package org.jahia.modules.mfa.filter;

import org.jahia.modules.mfa.MFAConstants;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRSiteNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.RenderFilter;
import org.osgi.service.component.annotations.Component;

@Component(service = RenderFilter.class, immediate = true)
public class MFAFilter extends AbstractFilter {
    public MFAFilter() {
        setPriority(98);
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        JCRSessionWrapper session = resource.getNode().getSession();
        JCRSiteNode site = renderContext.getSite();
        JCRNodeWrapper user = session.getNode(renderContext.getUser().getLocalPath());
        if (!renderContext.isLoggedIn() || !site.isNodeType(MFAConstants.MIXIN_MFA_SITE)) {
            return super.prepare(renderContext, resource, chain);
        }

        if (site.hasProperty(MFAConstants.PROP_ENFORCEMFA) && site.getProperty(MFAConstants.PROP_ENFORCEMFA).getBoolean() && site.hasProperty(MFAConstants.PROP_PAGE_MFA_ACTIVATION)) {
            JCRNodeWrapper node = user.getNode(MFAConstants.NODE_NAME_MFA);
            if (!node.hasProperty(MFAConstants.PROP_ACTIVATED) || !node.getProperty(MFAConstants.PROP_ACTIVATED).getBoolean()) {
                String activationPath = site.getProperty(MFAConstants.PROP_PAGE_MFA_ACTIVATION).getNode().getPath();
                if (renderContext.getMainResource().getNode() != site.getProperty(MFAConstants.PROP_PAGE_MFA_ACTIVATION).getNode()) {
                    renderContext.setRedirect(renderContext.getURLGenerator().getContext() + activationPath + ".html");
                    return "";
                }
            }
        }
        return super.prepare(renderContext, resource, chain);
    }

}
