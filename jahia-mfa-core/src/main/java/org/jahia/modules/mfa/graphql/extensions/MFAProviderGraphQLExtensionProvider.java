/*
 * ==========================================================================================
 * =                            JAHIA'S ENTERPRISE DISTRIBUTION                             =
 * ==========================================================================================
 *
 *                                  http://www.jahia.com
 *
 * JAHIA'S ENTERPRISE DISTRIBUTIONS LICENSING - IMPORTANT INFORMATION
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2020 Jahia Solutions Group. All rights reserved.
 *
 *     This file is part of a Jahia's Enterprise Distribution.
 *
 *     Jahia's Enterprise Distributions must be used in accordance with the terms
 *     contained in the Jahia Solutions Group Terms &amp; Conditions as well as
 *     the Jahia Sustainable Enterprise License (JSEL).
 *
 *     For questions regarding licensing, support, production usage...
 *     please contact our team at sales@jahia.com or go to http://www.jahia.com/license.
 *
 * ==========================================================================================
 */
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