/*
 * Copyright (c) 2013, the Dart project authors.
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
package edu.depaul.cdm.madl.engine.internal.search.listener;

import edu.depaul.cdm.madl.engine.search.SearchListener;
import edu.depaul.cdm.madl.engine.search.SearchMatch;

/**
 * Instances of the class <code>ScopedSearchListener</code> implement a search listener that
 * delegates to another search listener after removing matches that are outside a given scope.
 * 
 * @coverage dart.engine.search
 */
public abstract class WrappedSearchListener implements SearchListener {
  /**
   * The listener being wrapped.
   */
  private final SearchListener baseListener;

  /**
   * Initialize a newly created search listener to wrap the given listener.
   * 
   * @param listener the search listener being wrapped
   */
  public WrappedSearchListener(SearchListener listener) {
    baseListener = listener;
  }

  @Override
  public void searchComplete() {
    baseListener.searchComplete();
  }

  /**
   * Pass the given match on to the wrapped listener.
   * 
   * @param match the match to be propagated
   */
  protected void propagateMatch(SearchMatch match) {
    baseListener.matchFound(match);
  }
}
