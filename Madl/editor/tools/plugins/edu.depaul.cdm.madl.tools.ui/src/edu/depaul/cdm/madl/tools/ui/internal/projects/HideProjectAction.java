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
package edu.depaul.cdm.madl.tools.ui.internal.projects;

import edu.depaul.cdm.madl.tools.ui.MadlToolsPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.CloseResourceAction;
import org.eclipse.ui.ide.IDE;

/**
 * Standard action for hiding the currently selected project(s).
 */
public class HideProjectAction extends CloseResourceAction {

  /**
   * Create the action.
   */
  public HideProjectAction(IShellProvider shellProvider) {
    super(shellProvider, ProjectMessages.HideProjectAction_text);
    setToolTipText(ProjectMessages.HideProjectAction_tooltip);
  }

  @Override
  protected String getOperationMessage() {
    return ProjectMessages.HideProjectAction_operation_msg;
  }

  @Override
  protected String getProblemsMessage() {
    return ProjectMessages.HideProjectAction_problems_msg;
  }

  @Override
  protected String getProblemsTitle() {
    return ProjectMessages.HideProjectAction_problems_title;
  }

  @Override
  protected void invokeOperation(IResource resource, IProgressMonitor monitor) throws CoreException {

    try {
      //preemptively close associated editors
      closeEditors(resource);

    } catch (Throwable e) {
      //if we hit any exceptions, log them and trust super to do the right thing
      MadlToolsPlugin.log(e);
    } finally {

      ((IProject) resource).delete(false, true, monitor);
    }
  }

  private void closeEditors(final IResource project) throws Throwable {
    final Throwable[] ex = new Throwable[1];
    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        try {
          // Prompt occurs in super#run(), don't nag again
          IDE.saveAllEditors(new IResource[] {project}, false);
        } catch (Throwable th) {
          ex[0] = th;
        }
      }
    });
    if (ex[0] != null) {
      throw ex[0];
    }
  }

}
