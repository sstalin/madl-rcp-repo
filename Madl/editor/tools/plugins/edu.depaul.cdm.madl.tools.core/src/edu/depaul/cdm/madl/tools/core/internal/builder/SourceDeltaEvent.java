/*
 * Copyright 2013 Madl project authors.
 *
 * Licensed under the Eclipse Public License v1.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package edu.depaul.cdm.madl.tools.core.internal.builder;

import edu.depaul.cdm.madl.engine.source.Source;

/**
 * Used by {@link DeltaProcessor} to communicate changes to source files.
 *
 * @coverage madl.tools.core.builder
 */
public interface SourceDeltaEvent extends ResourceDeltaEvent {

  /**
   * Answer the source associated with the resource that was added, changed or removed
   *
   * @return the source or {@code null} if the resource location is null and the source object could
   *         not be determined
   */
  Source getSource();
}
