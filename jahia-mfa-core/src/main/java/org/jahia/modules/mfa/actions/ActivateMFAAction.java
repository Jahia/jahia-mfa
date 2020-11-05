package org.jahia.modules.mfa.actions;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;

public class ActivateMFAAction extends Action{

    @Override
    public ActionResult doExecute(HttpServletRequest arg0, RenderContext arg1, Resource arg2, JCRSessionWrapper arg3, Map<String, List<String>> arg4, URLResolver arg5) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
