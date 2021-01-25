package org.jahia.modules.mfa.graphql.extensions;

import org.jahia.modules.graphql.provider.dxm.DXGraphQLExtensionsProvider;
import org.osgi.service.component.annotations.Component;

/**
 * Extension provider for GraphQL
 */
@Component(service = DXGraphQLExtensionsProvider.class, immediate = true)
public class MFAProviderGraphQLExtensionProvider implements DXGraphQLExtensionsProvider {
    /* @Override
    public Collection<Class<?>> getExtensions() {
        return Arrays.asList(
                verifyMFAEnforcementExtension.class,
                verifyTokenExtension.class,
                prepareMFAExtension.class,
                activateMFAExtension.class
        );
    }*/
}
