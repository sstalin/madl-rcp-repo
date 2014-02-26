/*
 * Copyright (c) 2012, the Madl project authors.
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
package edu.depaul.cdm.madl.tools.core.model;


/**
 * A madl ignore listener is notified of changes to madl ignores (e.g., files flagged to be ignored
 * during analysis) as managed by the {@link MadlIgnoreManager}.
 *
 * @see MadlIgnoreManager#addListener(MadlIgnoreListener)
 * @coverage madl.tools.core.model
 */
public interface MadlIgnoreListener {

  /**
   * Notifies this listener that the ignore list has been changed.
   */
  void ignoresChanged(MadlIgnoreEvent event);

}
