/*
 * Copyright 2025 the original author or authors.
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

package org.gradle.internal.component.resolution.failure.type;

import org.gradle.api.internal.catalog.problems.ResolutionFailureProblemId;
import org.gradle.internal.component.resolution.failure.SelectionReasonAssessor.AssessedSelection;

import java.util.List;

/**
 * A failure that indicates that a requested module was rejected during graph construction.
 */
public final class ModuleRejectedFailure extends AbstractComponentSelectionFailure {
    private final List<String> resolutions;
    private final String legacyErrorMsg;

    public ModuleRejectedFailure(ResolutionFailureProblemId problemId, AssessedSelection assessedSelection, List<String> resolutions, String legacyErrorMsg) {
        super(problemId, assessedSelection);
        this.resolutions = resolutions;
        this.legacyErrorMsg = legacyErrorMsg;
    }

    @Override
    public String describeRequestTarget() {
        return getModuleIdentifier().toString();
    }

    public List<String> getResolutions() {
        return resolutions;
    }

    public String getLegacyErrorMsg() {
        return legacyErrorMsg;
    }
}
