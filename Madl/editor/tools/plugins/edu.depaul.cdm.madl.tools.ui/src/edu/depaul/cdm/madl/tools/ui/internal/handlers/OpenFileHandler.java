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

import edu.depaul.cdm.madl.tools.core.MadlCore;
import edu.depaul.cdm.madl.tools.core.internal.util.Extensions;
import edu.depaul.cdm.madl.tools.core.internal.util.ResourceUtil;
// import edu.depaul.cdm.madl.tools.core.model.MadlLibrary;
import edu.depaul.cdm.madl.tools.core.model.MadlModelException;
import edu.depaul.cdm.madl.tools.ui.MadlToolsPlugin;
import edu.depaul.cdm.madl.tools.ui.MadlUI;
import edu.depaul.cdm.madl.tools.ui.Messages;
import edu.depaul.cdm.madl.tools.ui.internal.actions.WorkbenchRunnableAdapter_OLD;
import edu.depaul.cdm.madl.tools.ui.internal.text.editor.EditorUtility;
import edu.depaul.cdm.madl.tools.ui.internal.util.ExceptionHandler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Prompt the user for a file to be opened, and open that file in an editor. If a Madl application
 * or library file is selected, then open that library or application otherwise find and open the
 * application or library containing the selected file.
 */
@Deprecated
public class OpenFileHandler extends AbstractHandler {

  public static final String COMMAND_ID = MadlUI.class.getPackage().getName() + ".file.open"; //$NON-NLS-1$

  private static final String FILTER_PATH_KEY = "openFileFilterPath"; //$NON-NLS-1$

  /**
   * Opens {@link MadlLibrary} using {@link MadlCore#openLibrary(File, IProgressMonitor)} and then
   * opens the given file from this library in an editor.
   */
  public static void openFile(Shell shell, final File file) throws ExecutionException {
   // final MadlLibrary[] library = new MadlLibrary[1];
    final IFile[] resource = new IFile[1];
    try {
      PlatformUI.getWorkbench().getProgressService().run(true, true,
          new WorkbenchRunnableAdapter_OLD(new IWorkspaceRunnable() {
            @Override
            public void run(IProgressMonitor monitor) throws CoreException {

              monitor.beginTask(HandlerMessages.OpenFile_taskName, 1);
              //ss
           /*   library[0] = MadlCore.openLibrary(file, new NullProgressMonitor());
              if (library[0] != null) {
                library[0].setTopLevel(true);
              }*/
             // monitor.worked(1);
              IResource[] resources = ResourceUtil.getResources(file);
              if (resources.length == 0) {
                resource[0] = null;
              } else if (resources.length == 1 && resources[0] instanceof IFile) {
                resource[0] = (IFile) resources[0];
              } /*else if (library[0] != null) {
                for (IResource r : resources) {
                  if (r instanceof IFile
                      && r.getProject().equals(library[0].getMadlProject().getProject())) {
                    resource[0] = (IFile) r;
                  }
                }
              }*/
              monitor.worked(1);
              monitor.done();
            }
          })); // workspace lock

    } catch (InvocationTargetException e) {
      ExceptionHandler.handle(e, shell, HandlerMessages.OpenFile_label,
          HandlerMessages.OpenFile_errorMessage);
    } catch (InterruptedException e) {
      // canceled by user
    }

    try {
      if (resource[0] != null) {
        EditorUtility.openInEditor(resource[0], true);
      } /*else if (library[0] == null) {
        MessageDialog.openError(shell, HandlerMessages.OpenFile_label,
            Messages.format(HandlerMessages.OpenFile_errorFileNotInLibrary, file.getName()));
      }*/
    } catch (PartInitException e) {
      throwFailedToOpen(file, e);
    } catch (MadlModelException e) {
      throwFailedToOpen(file, e);
    }
  }

  private static void throwFailedToOpen(File file, Exception e) throws ExecutionException {
    throw new ExecutionException("Failed to open " + file, e);
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    Shell shell = HandlerUtil.getActiveShell(event);
    return execute(shell);
  }

  public Object execute(Shell shell) throws ExecutionException {
    String selectedFilePath = promptForFile(shell);
    if (selectedFilePath == null) {
      return null;
    }
    final File selectedFile = new File(selectedFilePath);
    if (!selectedFile.exists()) {
      return null;
    }
    openFile(shell, selectedFile);
    return null;
  }

  /**
   * Answer the path to the Madl samples directory or <code>null</code> if it cannot be found
   */
  private String getSamplesPath() {
    String userHome = System.getProperty("user.home");
    if (userHome == null) {
      return null;
    }
    IPath madlPath = new Path(userHome).append("Documents").append(Extensions.MADL);
    if (!madlPath.toFile().exists()) {
      return null;
    }
    File dir = madlPath.append("sdk/samples").toFile();
    if (dir.exists()) {
      return dir.getPath();
    }
    dir = madlPath.append("samples").toFile();
    if (dir.exists()) {
      return dir.getPath();
    }
    return madlPath.toOSString();
  }

  private boolean isWindowsPlatform() {
    String platform = SWT.getPlatform();
    return platform.equals("win32") || platform.equals("wpf");
  }

  /**
   * Prompt the user to select a file to be opened.
   *
   * @return The absolute path of the selected file or <code>null</code> if the user canceled the
   *         operation.
   */
  private String promptForFile(Shell shell) {
    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    String allFilesFilter = isWindowsPlatform() ? "*.*" : "*";
    dialog.setFilterNames(new String[] {"Madl Files", "All Files (" + allFilesFilter + ")"});
    dialog.setFilterExtensions(new String[] {"*.madl", allFilesFilter});
    IDialogSettings settings = MadlToolsPlugin.getDefault().getDialogSettings();
    String filterPath = settings.get(FILTER_PATH_KEY);
    if (filterPath == null) {
      filterPath = getSamplesPath();
    }
    dialog.setFilterPath(filterPath);
    String result = dialog.open();
    if (result != null) {
      settings.put(FILTER_PATH_KEY, dialog.getFilterPath());
    }
    return result;
  }
}
