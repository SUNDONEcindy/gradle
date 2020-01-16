/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.internal.deprecation;

import com.google.common.base.Preconditions;
import org.gradle.api.internal.DocumentationRegistry;

abstract class DocumentationReference {
    static final DocumentationReference NO_DOCUMENTATION = new NullDocumentationReference();

    static DocumentationReference create(String id, String section) {
        return new DefaultDocumentationReference(id, section);
    }

    abstract String documentationUrl();

    String consultDocumentationMessage() {
        return String.format("See %s for more details.", documentationUrl());
    }

     private static class NullDocumentationReference extends DocumentationReference {

        private NullDocumentationReference() {}

        @Override
        String documentationUrl() {
            return null;
        }

        @Override
        String consultDocumentationMessage() {
            return null;
        }
    }

    private static class DefaultDocumentationReference extends DocumentationReference {

        private static final DocumentationRegistry DOCUMENTATION_REGISTRY = new DocumentationRegistry();

        private final String id;
        private final String section;

        private DefaultDocumentationReference(String id, String section) {
            this.id = Preconditions.checkNotNull(id);
            this.section = section;
        }

        @Override
        String documentationUrl() {
            if (section != null) {
                return DOCUMENTATION_REGISTRY.getDocumentationFor(id, section);
            }
            return DOCUMENTATION_REGISTRY.getDocumentationFor(id);
        }
    }
}



