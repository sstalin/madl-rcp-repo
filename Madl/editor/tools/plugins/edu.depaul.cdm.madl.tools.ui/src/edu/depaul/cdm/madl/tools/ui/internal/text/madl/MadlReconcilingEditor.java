/*
 * Copyright (c) 2013, the Madl project authors.
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
package edu.depaul.cdm.madl.tools.ui.internal.text.madl;

import edu.depaul.cdm.madl.engine.ast.CompilationUnit;
import edu.depaul.cdm.madl.engine.context.AnalysisContext;
import edu.depaul.cdm.madl.engine.source.Source;
import edu.depaul.cdm.madl.tools.core.analysis.model.Project;

import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;

/**
 * Interface used by {@link MadlReconcilingStrategy} to interact with the editor.
 */
public interface MadlReconcilingEditor {

  /**
   * Add a listener to be notified when the editor's viewer is disposed.
   * 
   * @param listener the listener to be notified (not {@code null})
   */
  void addViewerDisposeListener(DisposeListener listener);

  /**
   * Add a listener to be notified when the editor's view gains or looses focus.
   * 
   * @param listener the listener to be notified (not <code>null</code>)
   */
  void addViewerFocusListener(FocusListener listener);

  /**
   * Update the editor to use the specified compilation unit.
   * 
   * @param unit the unit or {@code null} if none
   */
  //SS commented out
  void applyResolvedUnit(CompilationUnit unit);

  /**
   * Answer the analysis context to be used when resolving the source displayed in the editor.
   * 
   * @return the {@link AnalysisContext} corresponding to this editor or {@code null} if none
   */
  //SS commented out
  AnalysisContext getInputAnalysisContext();

  /**
   * Answer the project containing the source being displayed in this editor.
   * 
   * @return the {@link Project} or {@code null} if none
   */
  //SS commented out
  Project getInputProject();

  /**
   * Answer the source being displayed in this editor.
   * 
   * @return the {@link Source} or {@code null} if none
   */
  //SS commented out
  Source getInputSource();

  /**
   * Answer the short name for the file being edited
   * 
   * @return the title or {@code null}
   */
  String getTitle();

  /**
   * Set the reconciling strategy associated with this editor
   * 
   * @param madlReconcilingStrategy the strategy or {@code null} if none
   */
  //SS commented out
  void setMadlReconcilingStrategy(MadlReconcilingStrategy madlReconcilingStrategy);
}
