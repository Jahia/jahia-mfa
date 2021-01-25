package org.jahia.modules.mfa.graphql.extensions;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLTypeExtension;
import org.jahia.modules.graphql.provider.dxm.DXGraphQLProvider;

/**
 * Admin mutation extension
 */
@GraphQLTypeExtension(DXGraphQLProvider.Query.class)
@GraphQLDescription("Queries for MFA")
public class MFAQueryExtension {
    
    private MFAQueryExtension(){
    }

    /**
     * mfa
     *
     * @return mfa queries
     */
    @GraphQLField
    @GraphQLName("mfa")
    @GraphQLDescription("MFA queries")
    public static GqlMFAQuery mfa() {
        return new GqlMFAQuery();
    }
}
