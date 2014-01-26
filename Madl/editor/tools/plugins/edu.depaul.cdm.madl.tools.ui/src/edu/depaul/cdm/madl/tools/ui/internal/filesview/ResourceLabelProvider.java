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

import edu.depaul.cdm.madl.engine.element.LibraryElement;
import edu.depaul.cdm.madl.engine.source.SourceKind;
import edu.depaul.cdm.madl.tools.core.MadlCore;
import edu.depaul.cdm.madl.tools.core.MadlCoreDebug;
import edu.depaul.cdm.madl.tools.core.analysis.model.AnalysisEvent;
import edu.depaul.cdm.madl.tools.core.analysis.model.AnalysisListener;
import edu.depaul.cdm.madl.tools.core.analysis.model.ProjectManager;
import edu.depaul.cdm.madl.tools.core.analysis.model.ResolvedEvent;
// import edu.depaul.cdm.madl.tools.core.analysis.model.ResolvedHtmlEvent;
import edu.depaul.cdm.madl.tools.core.internal.builder.AnalysisWorker;
import edu.depaul.cdm.madl.tools.core.utilities.io.FilenameUtils;
import edu.depaul.cdm.madl.tools.ui.MadlToolsPlugin;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Label provider for resources in the {@link FilesView}.
 */
public class ResourceLabelProvider implements IStyledLabelProvider, ILabelProvider,
    AnalysisListener {

  private static final String IGNORE_FILE_ICON = "icons/full/madl16/madl_excl.png"; //$NON-NLS-1$

  private static final String IGNORE_FOLDER_ICON = "icons/full/madl16/flder_obj_excl.png"; //$NON-NLS-1$

  private static final String PACKAGES_FOLDER_ICON = "icons/full/madl16/fldr_obj_pkg.png"; //$NON-NLS-1$
  private static final String LIBRARY_ICON = "icons/full/madl16/madl_library.png"; //$NON-NLS-1$
  private static final String BUILD_FILE_ICON = "icons/full/madl16/build_madl.png"; //$NON-NLS-1$
  private static final String PACKAGE_ICON = "icons/full/obj16/package_obj.gif"; //$NON-NLS-1$

  /**
   * Get a resource label provider instance.
   */
  public static ResourceLabelProvider createInstance() {
    return new ResourceLabelProvider();
  }

  private final WorkbenchLabelProvider workbenchLabelProvider = new WorkbenchLabelProvider();

  private List<ILabelProviderListener> listeners = new ArrayList<ILabelProviderListener>();

  private boolean disposed;

  @Override
  public void addListener(ILabelProviderListener listener) {
    listeners.add(listener);

    if (listeners.size() == 1) {
      AnalysisWorker.addListener(this);
    }
  }

  @Override
  public void complete(AnalysisEvent event) {
    notifyListeners();
  }

  @Override
  public void dispose() {
    disposed = true;

    workbenchLabelProvider.dispose();

    if (listeners.size() > 0) {
      AnalysisWorker.removeListener(this);
    }
  }

  @Override
  public Image getImage(Object element) {
    if (element instanceof IResource) {

      IResource resource = (IResource) element;
//ss
      /*
       * if (!MadlCore.isAnalyzed(resource)) { if (resource instanceof IFile) { return
       * MadlToolsPlugin.getImage(IGNORE_FILE_ICON); }
       * 
       * if (resource instanceof IFolder) { return MadlToolsPlugin.getImage(IGNORE_FOLDER_ICON); } }
       */

      if (resource instanceof IFile) {
        IFile file = (IFile) resource;

        if (MadlCore.isBuildMadl(file)) {
          return MadlToolsPlugin.getImage(BUILD_FILE_ICON);
        }

        //ss
        /*
         * if (MadlCoreDebug.EXPERIMENTAL) { SourceKind kind =
         * MadlCore.getProjectManager().getSourceKind(file); if (kind == SourceKind.LIBRARY) {
         * return MadlToolsPlugin.getImage(LIBRARY_ICON); } }
         */
      }

      if (element instanceof IFolder) {
        IFolder folder = (IFolder) element;

        if (MadlCore.isPackagesDirectory(folder)) {
          return MadlToolsPlugin.getImage(PACKAGES_FOLDER_ICON);
        }

        if (MadlCore.isPackagesResource(folder)) {
          return MadlToolsPlugin.getImage(PACKAGE_ICON);
        }
      }
    }

    if (element instanceof IFileStore && ((IFileStore) element).getName().equals("lib")) {
      return MadlToolsPlugin.getImage(PACKAGE_ICON);
    }
    return workbenchLabelProvider.getImage(element);
  }

  @Override
  public StyledString getStyledText(Object element) {
    if (element instanceof IResource) {
      IResource resource = (IResource) element;

      // Un-analyzed resources are grey.
      //ss 
      /*
       * if (!MadlCore.isAnalyzed(resource)) { return new StyledString(resource.getName(),
       * StyledString.QUALIFIER_STYLER); }
       */

      StyledString string = new StyledString(resource.getName());

      try {
        if (resource instanceof IFolder) {
          if (MadlCore.isPackagesDirectory((IFolder) resource)) {
            string.append(" [package:]", StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
          }

          try {
            String packageVersion = resource.getPersistentProperty(MadlCore.PUB_PACKAGE_VERSION);

            if (packageVersion != null) {
              string.append(" [" + packageVersion + "]", StyledString.QUALIFIER_STYLER); //$NON-NLS-1$ //$NON-NLS-2$
              return string;
            }
          } catch (CoreException ce) {
            // ignore
          }
        }

        if (resource instanceof IFile) {
          IFile file = (IFile) resource;

          // If it's a build.madl file, and auto-building is disabled, render the text in grey.
          if (MadlCore.isBuildMadl(file)
              && MadlCore.getPlugin().getDisableMadlBasedBuilder(file.getProject())) {
            return new StyledString(file.getName(), StyledString.QUALIFIER_STYLER);
          }

          // If we resource has been remapped by build.madl, display that info as a decoration.
          String remappingPath = MadlCore.getResourceRemapping(file);

          if (remappingPath != null) {
            StyledString str = new StyledString(file.getName());
            str.append(" [" + getRelativePath(file, remappingPath) + "]",
                StyledString.QUALIFIER_STYLER);
            return str;
          }

          // Append the library name to library units.
          ProjectManager projectManager = MadlCore.getProjectManager();
          SourceKind kind = projectManager.getSourceKind((IFile) resource);
//ss
          /*
           * if (kind == SourceKind.LIBRARY) { LibraryElement libraryElement =
           * projectManager.getLibraryElementOrNull((IFile) resource);
           * 
           * if (libraryElement != null) { String name = libraryElement.getName();
           * 
           * if (name == null || name.length() == 0) {
           * 
           * if (libraryElement.getEntryPoint() != null) { name =
           * FilenameUtils.removeExtension(resource.getName()); } }
           * 
           * string.append(" [" + name + "]", StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
           * //$NON-NLS-2$ } }
           */
        }
      } catch (Throwable th) {
        MadlToolsPlugin.log(th);
      }

      return string;
    }
//ss
    /*
     * if (element instanceof MadlLibraryNode && ((MadlLibraryNode) element).getCategory() != null)
     * { StyledString string = new StyledString(((MadlLibraryNode) element).getLabel());
     * string.append(" [" + ((MadlLibraryNode) element).getCategory() + "]", //$NON-NLS-1$
     * //$NON-NLS-2$ StyledString.QUALIFIER_STYLER);
     * 
     * return string; }
     */

    //ss
    /*
     * if (element instanceof MadlPackageNode) { StyledString string = new
     * StyledString(((MadlPackageNode) element).getLabel()); string.append(" [" + ((MadlPackageNode)
     * element).getVersion() + "]", //$NON-NLS-1$ //$NON-NLS-2$ StyledString.QUALIFIER_STYLER);
     * return string; }
     */

    return workbenchLabelProvider.getStyledText(element);
  }

  @Override
  public String getText(Object element) {
    return workbenchLabelProvider.getText(element);
  }

  @Override
  public boolean isLabelProperty(Object element, String property) {
    return workbenchLabelProvider.isLabelProperty(element, property);
  }

  @Override
  public void removeListener(ILabelProviderListener listener) {
    listeners.remove(listener);

    if (listeners.isEmpty()) {
      AnalysisWorker.removeListener(this);
    }
  }

  @Override
  public void resolved(ResolvedEvent event) {
    // ignored
  }

  //ss
  /*
   * @Override public void resolvedHtml(ResolvedHtmlEvent event) { // ignored }
   */

  private String getRelativePath(IFile file, String mappingPath) {
    String parentPath = file.getParent().getFullPath().toPortableString();

    if (mappingPath.startsWith(parentPath)) {
      mappingPath = mappingPath.substring(parentPath.length());
    }

    if (mappingPath.startsWith("/")) {
      mappingPath = mappingPath.substring(1);
    }

    return mappingPath;
  }

  private void notifyListeners() {
    if (disposed) {
      return;
    }

    try {
      for (final ILabelProviderListener listener : listeners) {
        uiExec(new Runnable() {
          @Override
          public void run() {
            if (!disposed) {
              listener.labelProviderChanged(new LabelProviderChangedEvent(
                  ResourceLabelProvider.this));
            }
          }
        });
      }
    } catch (Throwable t) {
      MadlToolsPlugin.log(t);
    }
  }

  private void uiExec(Runnable runnable) {
    try {
      Display.getDefault().asyncExec(runnable);
    } catch (SWTException e) {
      // Ignore -- might occur if async events get dispatched after the WS is closed
    }
  }
}
