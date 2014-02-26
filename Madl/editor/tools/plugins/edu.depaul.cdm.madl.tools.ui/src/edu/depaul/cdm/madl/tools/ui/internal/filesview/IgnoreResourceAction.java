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
package edu.depaul.cdm.madl.tools.ui.internal.filesview;

import edu.depaul.cdm.madl.tools.core.MadlCore;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionListenerAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Action to add (or remove) resources from the madl ignore list.
 */
public class IgnoreResourceAction extends SelectionListenerAction {

  private final Shell shell;
  private List<IResource> resources = Arrays.asList(new IResource[0]);

  protected IgnoreResourceAction(Shell shell) {
    super(FilesViewMessages.IgnoreResourcesAction_dont_analyze_label);
    this.shell = shell;
  }

  @Override
  public void run() {
    try {
      for (IResource r : resources) {
        toggleIgnoreState(r);
      }
    } catch (IOException e) {
      MessageDialog.openError(shell, "Error Ignoring Resource", e.getMessage());
      MadlCore.logInformation("Could not access ignore file", e);
    } catch (CoreException e) {
      MessageDialog.openError(shell, "Error Deleting Markers", e.getMessage()); //$NON-NLS-1$
    } finally {
      updateLabel();
    }
  }

  @Override
  protected List<IResource> getSelectedResources() {
    @SuppressWarnings("unchecked")
    List<Object> res = super.getSelectedResources();
    ArrayList<IResource> resources = new ArrayList<IResource>();
    for (Object r : res) {
      resources.add((IResource) r);
    }
    return resources;
  }

  @Override
  protected boolean updateSelection(IStructuredSelection selection) {

    resources = getSelectedResources();

    if (resources.isEmpty() || !sameAnalysisState(resources)) {
      return false;
    }

    updateLabel();

    return true;
  }

  void updateLabel() {
    //ss
   /* if (MadlCore.isAnalyzed(resources.get(0))) {
      setText(FilesViewMessages.IgnoreResourcesAction_dont_analyze_label);
    } else {
      setText(FilesViewMessages.IgnoreResourcesAction_do_analyze_label);
    }*/
  }

  /**
   * Ensures that all selected resources are in the same state of analysis.
   */
  private boolean sameAnalysisState(List<IResource> resources) {
    if (resources.isEmpty()) {
      return true;
    }

    //ss
  /*  boolean isAnalyzed = MadlCore.isAnalyzed(resources.get(0));

    for (IResource resource : resources.subList(0, resources.size())) {
      if (MadlCore.isAnalyzed(resource) != isAnalyzed) {
        return false;
      }
    }*/

    return true;
  }

  private void toggleIgnoreState(IResource resource) throws IOException, CoreException {
    //ss
 /*   if (MadlCore.isAnalyzed(resource)) {
      MadlCore.addToIgnores(resource);
    } else {
      MadlCore.removeFromIgnores(resource);
    }*/
  }

}
