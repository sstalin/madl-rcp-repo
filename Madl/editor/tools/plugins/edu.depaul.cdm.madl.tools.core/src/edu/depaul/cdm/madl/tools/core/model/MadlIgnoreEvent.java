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
package edu.depaul.cdm.madl.tools.core.model;

/**
 * An event that encapsulates an ignore action.
 *
 * @coverage madl.tools.core.model
 */
public class MadlIgnoreEvent {

  private final String[] ignoresAdded;
  private final String[] ignoresRemoved;

  public MadlIgnoreEvent(String[] ignoresAdded, String[] ignoresRemoved) {

    this.ignoresAdded = ignoresAdded;
    this.ignoresRemoved = ignoresRemoved;
  }

  public String[] getAdded() {
    return ignoresAdded;
  }

  public String[] getRemoved() {
    return ignoresRemoved;
  }
}
