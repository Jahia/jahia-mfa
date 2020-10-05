package org.jahia.modules.mfa.otp.provider;

import org.jahia.modules.mfa.provider.JahiaMFAProvider;
import org.jahia.services.content.decorator.JCRUserNode;

public class JahiaMFAOtpProvider extends JahiaMFAProvider {

    private static final String KEY = "jahia-mfa-otp-provider";

    public JahiaMFAOtpProvider() {
        super(KEY);
    }

    @Override
    public boolean verifyToken(JCRUserNode userNode, String token) {
        // TODO: implement token verification code
        return true;
    }
}
