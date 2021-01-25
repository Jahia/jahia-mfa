package org.jahia.modules.mfa.otp.graphql.extensions;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLTypeExtension;
import org.jahia.modules.graphql.provider.dxm.DXGraphQLProvider;

/**
 * Admin mutation extension
 */
@GraphQLTypeExtension(DXGraphQLProvider.Query.class)
@GraphQLDescription("Queries for MFA OTP")
public class MFAOTPQueryExtension {
    
    private  MFAOTPQueryExtension(){
    }

    /**
     * mfa
     *
     * @return mfa queries
     */
    @GraphQLField
    @GraphQLName("mfaOTP")
    @GraphQLDescription("MFA queries")
    public GqlMFAOTPQuery mfaOTP() {
        return new GqlMFAOTPQuery();
    }
}
