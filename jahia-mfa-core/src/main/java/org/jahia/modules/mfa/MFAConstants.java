package org.jahia.modules.mfa;

public final class MFAConstants {

    public static final String AUTH_VALVE_ID = "mfaAuthValve";
    public static final String BEAN_MFA_SERVICE = "jahiaMFAServiceImpl";
    public static final String MIXIN_MFA_USER = "jmix:MFAUser";
    public static final String MIXIN_MFA_SITE = "jmix:MFAsite";
    public static final String NODE_NAME_MFA = "MFA";
    public static final String NODE_TYPE_MFA = "jnt:MFA";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_PROVIDER = "provider";
    public static final String PARAM_ACTIVATE = "activate";
    public static final String PARAM_SITEKEY = "sitekey";
    public static final String PARAM_TOKEN = "token";

    public static final String PROP_ACTIVATED = "activated";
    public static final String PROP_ENFORCEMFA = "enforceMFA";
    public static final String PROP_PAGE_MFA_ACTIVATION = "pageMFAactivation";
    public static final String PROP_PROVIDER = "provider";
    public static final int TOKEN_SIZE = 6;

    private MFAConstants() {
    }

}
