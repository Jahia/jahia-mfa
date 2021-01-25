package org.jahia.modules.mfa.otp.graphql.extensions;

import org.jahia.modules.graphql.provider.dxm.DXGraphQLExtensionsProvider;
import org.osgi.service.component.annotations.Component;

/**
 * Extension provider for GraphQL
 */
@Component(service = DXGraphQLExtensionsProvider.class, immediate = true)
public class OTPGraphQLExtensionProvider implements DXGraphQLExtensionsProvider {
    /* @Override
    public Collection<Class<?>> getExtensions() {
        return Arrays.asList(

        );
    }*/
}
