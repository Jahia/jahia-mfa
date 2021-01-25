package org.jahia.modules.mfa.provider;

import org.jahia.modules.mfa.service.JahiaMFAService;
import org.jahia.services.content.decorator.JCRUserNode;

public abstract class JahiaMFAProvider {

    private final String key;
    private JahiaMFAService jahiaMFAService;

    protected JahiaMFAProvider(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public final void setJahiaMFAService(JahiaMFAService jahiaMFAService) {
        this.jahiaMFAService = jahiaMFAService;
    }

    public final JahiaMFAService getJahiaMFAService() {
        return jahiaMFAService;
    }

    public final void register() {
        jahiaMFAService.addProvider(this);
    }

    public final void unregister() {
        jahiaMFAService.removeProvider(this);
    }

    /**
     * Activate MFA of the related user
     *
     * @param userNode the user to check
     * @return true if the activation is successful, false otherwise
     */
    public abstract boolean activateMFA(JCRUserNode userNode);

    /**
     * Deactivate MFA of the related user
     *
     * @param userNode the user to check
     * @return true if the deactivation is successful, false otherwise
     */
    public abstract boolean deactivateMFA(JCRUserNode userNode);

    /**
     * Prepare MFA of the related user without activating
     *
     * @param userNode the user to check
     * @param password used to encrypt the TOTP key
     * @return true if the preparation is successful, false otherwise
     */
    public abstract boolean prepareMFA(JCRUserNode userNode, String password);

    /**
     * Verify the token sent by the user against the TOTP key
     *
     * @param userNode the user to check
     * @param token sent by the user
     * @param password used to decrypt the TOTP key
     * @return true if the preparation is successful, false otherwise
     */
    public abstract boolean verifyToken(JCRUserNode userNode, String token, String password);
}
