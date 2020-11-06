package org.jahia.modules.mfa.service;

import org.jahia.modules.mfa.provider.JahiaMFAProvider;
import org.jahia.services.content.decorator.JCRUserNode;

public interface JahiaMFAService {

    /**
     * Add a Jahia MFA provider to the MFA service
     *
     * @param provider provided by another module
     */
    void addProvider(JahiaMFAProvider provider);

    /**
     * Remove a Jahia MFA provider to the MFA service
     *
     * @param provider provided by another module
     */
    void removeProvider(JahiaMFAProvider provider);

    /**
     * Verify the token sent by the user against the TOTP key thanks to the related Jahia MFA provider
     *
     * @param userNode the user to check
     * @param provider selected by the user
     * @param token sent by the user
     * @param password used to decrypt the TOTP key
     * @return true if the preparation is successful, false otherwise
     */
    boolean verifyToken(JCRUserNode userNode, String provider, String token, String password);

    /**
     * Prepare MFA of the related user without activating, thanks to the related Jahia MFA provider
     *
     * @param userNode the user to check
     * @param provider selected by the user
     * @param password used to encrypt the TOTP key
     * @return true if the preparation is successful, false otherwise
     */
    void prepareMFA(JCRUserNode userNode, String provider, String password);

    /**
     * Activate MFA of the related user thanks to the related Jahia MFA provider
     *
     * @param userNode the user to check
     * @param provider selected by the user
     * @return true if the activation is successful, false otherwise
     */
    void activateMFA(JCRUserNode userNode, String provider);

    /**
     * Deactivate MFA of the related user thanks to the related Jahia MFA provider
     *
     * @param userNode the user to check
     * @param provider selected by the user
     * @return true if the deactivation is successful, false otherwise
     */
    void deactivateMFA(JCRUserNode userNode, String provider);

    /**
     * Check if the user has activated MFA
     *
     * @param userNode the user to check
     * @return true if the user has activated MFA, false otherwise
     */
    boolean hasMFA(JCRUserNode userNode);
}
