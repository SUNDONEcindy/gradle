/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.artifacts.configurations;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

/**
 * This class defines a set of configuration roles which each describe an intermediate state between a current role
 * and a future role which will replace it in the next major Gradle version. These roles represent a narrowing migration
 * from one role to another by marking usages which are present in the current role but not present in the eventual role
 * as deprecated.
 * <p>
 * The roles here are meant to be a temporary roles used for migration only.
 * Upon major version releases, configurations in a migration role should be updated
 * to their destination role.
 * after Detached Configurations are no longer a special case.
 * <p>
 * This is meant to only support <strong>narrowing migrations</strong>, that restrict usage that was previously
 * allowed. The migrations should transition from a role defined in {@link ConfigurationRoles} to another
 * role defined in {@link ConfigurationRoles}. This is <strong>not</strong> meant to support general-case
 * migrations from any usage pattern to any other.</p>
 */
public final class ConfigurationRolesForMigration {
    private ConfigurationRolesForMigration() { /* Private to prevent instantiation. */ }

    /**
     * A configuration that is retired and not meant to be used for any purpose.  This is primarily useful in creating a migrating configuration role in {@link ConfigurationRolesForMigration}.
     */
    public static final ConfigurationRole NONE = new DefaultConfigurationRole("Deprecated", false, false, false, false, false, false);

    /**
     * A resolvable dependency scope that will become a resolvable configuration in the next major version.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static final ConfigurationRole RESOLVABLE_DEPENDENCY_SCOPE_TO_RESOLVABLE = difference(ConfigurationRoles.RESOLVABLE_DEPENDENCY_SCOPE, ConfigurationRoles.RESOLVABLE);

    /**
     * A resolvable dependency scope that will become a resolvable configuration in the next major version.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static final ConfigurationRole RESOLVABLE_DEPENDENCY_SCOPE_TO_DEPENDENCY_SCOPE = difference(ConfigurationRoles.RESOLVABLE_DEPENDENCY_SCOPE, ConfigurationRoles.DEPENDENCY_SCOPE);

    /**
     * A legacy configuration that will become a resolvable dependency scope configuration in the next major version.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public static final ConfigurationRole LEGACY_TO_RESOLVABLE_DEPENDENCY_SCOPE = difference(ConfigurationRoles.ALL, ConfigurationRoles.RESOLVABLE_DEPENDENCY_SCOPE);

    /**
     * A consumable configuration that has been deprecated and will be removed in the next major version.
     */
    public static final ConfigurationRole CONSUMABLE_TO_RETIRED = difference(ConfigurationRoles.CONSUMABLE, NONE);

    /**
     * All known migration roles.
     */
    public static final Set<ConfigurationRole> ALL = ImmutableSet.of(
        RESOLVABLE_DEPENDENCY_SCOPE_TO_RESOLVABLE,
        RESOLVABLE_DEPENDENCY_SCOPE_TO_DEPENDENCY_SCOPE,
        LEGACY_TO_RESOLVABLE_DEPENDENCY_SCOPE,
        CONSUMABLE_TO_RETIRED
    );

    /**
     * Computes the difference between two roles, such that any usage that is allowed in the
     * initial role but not allowed in the eventual role will be deprecated in the returned role.
     */
    private static ConfigurationRole difference(ConfigurationRole initialRole, ConfigurationRole eventualRole) {
        Preconditions.checkArgument(
            !initialRole.isConsumptionDeprecated() && !initialRole.isResolutionDeprecated() && !initialRole.isDeclarationAgainstDeprecated(),
            "The initial role must not contain deprecated usages."
        );
        Preconditions.checkArgument(
            !eventualRole.isConsumptionDeprecated() && !eventualRole.isResolutionDeprecated() && !eventualRole.isDeclarationAgainstDeprecated(),
            "The eventual role must not contain deprecated usages."
        );

        /*
         * Since we're assuming strictly narrowing usage from a non-deprecated initial role, for each usage we want this migration
         * role to deprecate a usage if that usage will change from allowed -> disallowed when migrating from the initial role to the
         * eventual role.
         */
        boolean consumptionDeprecated = initialRole.isConsumable() && !eventualRole.isConsumable();
        boolean resolutionDeprecated = initialRole.isResolvable() && !eventualRole.isResolvable();
        boolean declarationAgainstDeprecated = initialRole.isDeclarable() && !eventualRole.isDeclarable();

        return new DefaultConfigurationRole(
            initialRole.getName(),
            initialRole.isConsumable(),
            initialRole.isResolvable(),
            initialRole.isDeclarable(),
            consumptionDeprecated,
            resolutionDeprecated,
            declarationAgainstDeprecated
        );
    }
}
