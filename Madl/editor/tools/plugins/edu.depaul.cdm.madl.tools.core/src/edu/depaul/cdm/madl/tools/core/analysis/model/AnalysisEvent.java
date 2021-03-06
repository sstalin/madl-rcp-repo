/*
 * Copyright 2013 Dart project authors.
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
package edu.depaul.cdm.madl.tools.core.analysis.model;

import edu.depaul.cdm.madl.engine.context.AnalysisContext;

/**
 * Instances of {@link AnalysisEvent} contain information about analysis progress.
 * 
 * @coverage dart.tools.core.model
 */
public interface AnalysisEvent {

  /**
   * Answer the context in which the analysis was performed.
   * 
   * @return the context (not {@code null})
   */
  AnalysisContext getContext();

  /**
   * Answer the manager of the context in which the analysis was performed.
   * 
   * @return the manager (not {@code null})
   */
  ContextManager getContextManager();
}
