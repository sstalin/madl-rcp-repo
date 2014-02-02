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
package edu.depaul.cdm.madl.tools.ui.internal.handlers;

import edu.depaul.cdm.madl.tools.ui.MadlPluginImages;
import edu.depaul.cdm.madl.tools.ui.MadlToolsPlugin;
import edu.depaul.cdm.madl.tools.ui.MadlUI;
import edu.depaul.cdm.madl.tools.ui.actions.OpenNewFileWizardAction;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.actions.CommandAction;

/**
 * Opens the New File wizard.
 */
@SuppressWarnings("restriction")
public class NewFileHandler extends AbstractHandler {

  public static class NewFileCommandAction extends CommandAction {
    public NewFileCommandAction(IWorkbenchWindow window) {
      super(window, COMMAND_ID);
      setImageDescriptor(MadlPluginImages.DESC_TOOL_NEW_FILE);
    }

    public void updateEnablement() {
      setEnabled(getNumberOfProjects() > 0);
    }

    private int getNumberOfProjects() {
      try {
        return ResourcesPlugin.getWorkspace().getRoot().getProjects().length;
      } catch (Throwable th) {
        MadlToolsPlugin.log(th);
        return 0;
      }
    }

  }

  public static final String COMMAND_ID = MadlUI.class.getPackage().getName() + ".file.new"; //$NON-NLS-1$

  public static NewFileCommandAction createCommandAction(IWorkbenchWindow window) {
    return new NewFileCommandAction(window);
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
    if (window == null) {
      window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    new OpenNewFileWizardAction(window).run();
    return null;
  }

}
