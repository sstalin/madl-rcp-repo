/*
 * Copyright (c) 2011, the Madl project authors.
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
package edu.depaul.cdm.madl.tools.core.internal.model;

import edu.depaul.cdm.madl.tools.core.MadlCore;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Instances of the class <code>MadlProjectNature</code> implement the nature of a Madl project.
 *
 * @coverage madl.tools.core.model
 */
public class MadlProjectNature implements IProjectNature {
  /**
   * Return <code>true</code> if the given project has the Madl project nature, or
   * <code>false</code> if it either doesn't have the Madl nature or if we cannot determine whether
   * or not it has the Madl nature. This is a convenience method that handles
   * <code>CoreException</code>s by returning <code>false</code>.
   *
   * @param project the project being tested
   * @return <code>true</code> if the given project has the Madl project nature
   */
  public static boolean hasMadlNature(IProject project) {
    try {
      if (project == null) {
        return false;
      }

      return project.hasNature(MadlCore.MADL_PROJECT_NATURE);
    } catch (CoreException exception) {
      return false;
    }
  }

  /**
   * Return <code>true</code> if the given resource's project has the Madl project nature, or
   * <code>false</code> if it either doesn't have the Madl nature or if we cannot determine whether
   * or not it has the Madl nature.
   *
   * @param resource the resource being tested
   * @return <code>true</code> if the given resource's project has the Madl nature
   */
  public static boolean hasMadlNature(IResource resource) {
    if (resource == null) {
      return false;
    }

    return hasMadlNature(resource.getProject());
  }

  /**
   * The project being represented by this object.
   */
  private IProject project;

  /**
   * Initialize a newly created project nature to represent the nature for a yet unspecified
   * project.
   */
  public MadlProjectNature() {
    super();
  }

  @Override
  public void configure() throws CoreException {
    addBuilderToBuildSpec();
  }

  @Override
  public void deconfigure() throws CoreException {
    removeBuilderFromBuildSpec();
  }

  @Override
  public IProject getProject() {
    return project;
  }

  @Override
  public void setProject(IProject project) {
    this.project = project;
  }

  /**
   * Add the Madl builder to the build specification of the underlying project.
   *
   * @throws CoreException if the builder could not be added for some reason
   */
  private void addBuilderToBuildSpec() throws CoreException {
    IProjectDescription description = project.getDescription();
    int index = getMadlCommandIndex(description.getBuildSpec());
    if (index < 0) {
      ICommand command = description.newCommand();
      command.setBuilderName(MadlCore.MADL_BUILDER_ID);
      setMadlCommand(description, command);
    }
  }

  private int getMadlCommandIndex(ICommand[] buildSpec) {
    for (int i = 0; i < buildSpec.length; i++) {
      if (buildSpec[i].getBuilderName().equals(MadlCore.MADL_BUILDER_ID)) {
        return i;
      }
    }
    return -1;
  }

  private void removeBuilderFromBuildSpec() throws CoreException {
    IProjectDescription description = project.getDescription();
    ICommand[] oldCommands = description.getBuildSpec();
    int length = oldCommands.length;
    for (int i = 0; i < length; i++) {
      if (oldCommands[i].getBuilderName().equals(MadlCore.MADL_BUILDER_ID)) {
        ICommand[] newCommands = new ICommand[length - 1];
        System.arraycopy(oldCommands, 0, newCommands, 0, i);
        System.arraycopy(oldCommands, i + 1, newCommands, i, length - i - 1);
        description.setBuildSpec(newCommands);
        project.setDescription(description, null);
        return;
      }
    }
  }

  private void setMadlCommand(IProjectDescription description, ICommand command)
      throws CoreException {
    ICommand[] oldCommands = description.getBuildSpec();
    int length = oldCommands.length;
    ICommand[] newCommands = new ICommand[length + 1];
    System.arraycopy(oldCommands, 0, newCommands, 0, length);
    newCommands[length] = command;
    description.setBuildSpec(newCommands);
    project.setDescription(description, null);
  }
}
