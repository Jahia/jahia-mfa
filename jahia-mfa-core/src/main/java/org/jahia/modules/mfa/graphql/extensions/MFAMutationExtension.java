package org.jahia.modules.mfa.graphql.extensions;

import graphql.annotations.annotationTypes.GraphQLDescription;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLTypeExtension;
import org.jahia.modules.graphql.provider.dxm.DXGraphQLProvider;

/**
 * Admin mutation extension
 */
@GraphQLTypeExtension(DXGraphQLProvider.Mutation.class)
@GraphQLDescription("Mutations for MFA")
public class MFAMutationExtension {

    private MFAMutationExtension() {
    }

    /**
     * mfa
     *
     * @return mfa mutations
     */
    @GraphQLField
    @GraphQLName("mfa")
    @GraphQLDescription("MFA mutations")
    public static GqlMFAMutation mfa() {
        return new GqlMFAMutation();
    }
}
