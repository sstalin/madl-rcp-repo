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
package edu.depaul.cdm.madl.engine.context;

import edu.depaul.cdm.madl.engine.source.Source;
import edu.depaul.cdm.madl.engine.source.SourceContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of the class {@code ChangeSet} indicate what sources have been added, changed, or
 * removed.
 * 
 * @coverage dart.engine
 */
public class ChangeSet {
  /**
   * A list containing the sources that have been added.
   */
  private ArrayList<Source> added = new ArrayList<Source>();

  /**
   * A list containing the sources that have been changed.
   */
  private ArrayList<Source> changed = new ArrayList<Source>();

  /**
   * A list containing the sources that have been removed.
   */
  private ArrayList<Source> removed = new ArrayList<Source>();

  /**
   * A list containing the source containers specifying additional sources that have been removed.
   */
  private ArrayList<SourceContainer> removedContainers = new ArrayList<SourceContainer>();

  /**
   * Initialize a newly created change set to be empty.
   */
  public ChangeSet() {
    super();
  }

  /**
   * Record that the specified source has been added and that it's content is the default contents
   * of the source.
   * 
   * @param source the source that was added
   */
  public void added(Source source) {
    added.add(source);
  }

  /**
   * Record that the specified source has been changed and that it's content is the default contents
   * of the source.
   * 
   * @param source the source that was changed
   */
  public void changed(Source source) {
    changed.add(source);
  }

  /**
   * Return a collection of the sources that have been added.
   * 
   * @return a collection of the sources that have been added
   */
  public List<Source> getAdded() {
    return added;
  }

  /**
   * Return a collection of sources that have been changed.
   * 
   * @return a collection of sources that have been changed
   */
  public List<Source> getChanged() {
    return changed;
  }

  /**
   * Return a list containing the sources that were removed.
   * 
   * @return a list containing the sources that were removed
   */
  public List<Source> getRemoved() {
    return removed;
  }

  /**
   * Return a list containing the source containers that were removed.
   * 
   * @return a list containing the source containers that were removed
   */
  public List<SourceContainer> getRemovedContainers() {
    return removedContainers;
  }

  /**
   * Return {@code true} if this change set does not contain any changes.
   * 
   * @return {@code true} if this change set does not contain any changes
   */
  public boolean isEmpty() {
    return added.isEmpty() && changed.isEmpty() && removed.isEmpty() && removedContainers.isEmpty();
  }

  /**
   * Record that the specified source has been removed.
   * 
   * @param source the source that was removed
   */
  public void removed(Source source) {
    if (source != null) {
      removed.add(source);
    }
  }

  /**
   * Record that the specified source container has been removed.
   * 
   * @param container the source container that was removed
   */
  public void removedContainer(SourceContainer container) {
    if (container != null) {
      removedContainers.add(container);
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    boolean needsSeparator = appendSources(builder, added, false, "added");
    needsSeparator = appendSources(builder, changed, needsSeparator, "changed");
    appendSources(builder, removed, needsSeparator, "removed");
    int count = removedContainers.size();
    if (count > 0) {
      if (removed.isEmpty()) {
        if (needsSeparator) {
          builder.append("; ");
        }
        builder.append("removed: from ");
        builder.append(count);
        builder.append(" containers");
      } else {
        builder.append(", and more from ");
        builder.append(count);
        builder.append(" containers");
      }
    }
    return builder.toString();
  }

  /**
   * Append the given sources to the given builder, prefixed with the given label and possibly a
   * separator.
   * 
   * @param builder the builder to which the sources are to be appended
   * @param sources the sources to be appended
   * @param needsSeparator {@code true} if a separator is needed before the label
   * @param label the label used to prefix the sources
   * @return {@code true} if future lists of sources will need a separator
   */
  private boolean appendSources(StringBuilder builder, ArrayList<Source> sources,
      boolean needsSeparator, String label) {
    if (sources.isEmpty()) {
      return needsSeparator;
    }
    if (needsSeparator) {
      builder.append("; ");
    }
    builder.append(label);
    String prefix = " ";
    for (Source source : sources) {
      builder.append(prefix);
      builder.append(source.getFullName());
      prefix = ", ";
    }
    return true;
  }
}
