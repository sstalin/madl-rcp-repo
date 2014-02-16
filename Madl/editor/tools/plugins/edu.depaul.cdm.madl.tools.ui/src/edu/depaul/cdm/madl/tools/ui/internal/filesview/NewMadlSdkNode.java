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

package edu.depaul.cdm.madl.tools.ui.internal.filesview;

import edu.depaul.cdm.madl.engine.sdk.SdkLibrary;
import edu.depaul.cdm.madl.tools.core.model.MadlSdkManager;
import edu.depaul.cdm.madl.tools.ui.MadlToolsPlugin;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A class used to represent the Madl SDK in the Files view.
 */
class NewMadlSdkNode extends MadlSdkNode {

  static class SdkDirectoryWorkbenchAdapter extends WorkbenchAdapter implements IAdapterFactory {
    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
      if (adapterType == IWorkbenchAdapter.class) {
        return this;
      } else {
        return null;
      }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class[] getAdapterList() {
      return new Class[] {IWorkbenchAdapter.class};
    }

    @Override
    public ImageDescriptor getImageDescriptor(Object object) {
      return ((IMadlNode) object).getImageDescriptor();
    }

    @Override
    public String getLabel(Object object) {
      return ((IMadlNode) object).getLabel();
    }
  }

  static {
    Platform.getAdapterManager().registerAdapters(new SdkDirectoryWorkbenchAdapter(),
        IMadlNode.class);
  }

  private IMadlNode[] resources;

  public NewMadlSdkNode() {
    resources = getResources();
  }

  public IFileStore[] getFiles() {
    try {
      List<IFileStore> members = filteredMembers(getFileStore());

      return members.toArray(new IFileStore[members.size()]);
    } catch (CoreException e) {
      return new IFileStore[0];
    }
  }

  @Override
  public IFileStore getFileStore() {
    //ss

/*    if (MadlSdkManager.getManager().hasSdk()) {
      File sdkLibDir = MadlSdkManager.getManager().getSdk().getLibraryDirectory();
      return EFS.getLocalFileSystem().fromLocalFile(sdkLibDir); }
*/
    return null;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return MadlToolsPlugin.getImageDescriptor("icons/full/madl16/sdk.png"); //$NON-NLS-1$
  }

  @Override
  public String getLabel() {
    return "Madl SDK"; //$NON-NLS-1$
  }

  @Override
  public MadlLibraryNode[] getLibraries() {
    MadlLibraryNode[] libraries;
      List<MadlLibraryNode> libs = new ArrayList<MadlLibraryNode>();
//ss
      File sdkDirectory = MadlSdkManager.getManager().getSdk().getDirectory();
      File libFile =new File(sdkDirectory, "lib"); //$NON-NLS-1$;
      if(libFile.exists()){
        IFileSystem fileSystem = EFS.getLocalFileSystem();
        libs.add(new MadlLibraryNode(this, fileSystem.fromLocalFile(libFile),
            libFile.getName()));
      }


//ss

    /*  if (!MadlSdkManager.getManager().hasSdk()) {
        libFile = null;
      } else {
        SdkLibrary[] systemLibraries = MadlSdkManager.getManager().getNewSdk().getSdkLibraries();
        //TODO
        //(pquitslund): fix how we're getting the SDK directory
        File sdkDirectory = MadlSdkManager.getManager().getSdk().getDirectory();

        IFileSystem fileSystem = EFS.getLocalFileSystem();
        for (SdkLibrary systemLibrary : systemLibraries) {
          if (systemLibrary.isDocumented()) {
            libFile = new File(sdkDirectory, "lib"); //$NON-NLS-1$
            String pathToLib = systemLibrary.getPath();
            if (pathToLib.indexOf("/") != -1) { //$NON-NLS-1$
              libFile = new File(libFile, new Path(pathToLib).removeLastSegments(1).toOSString());
            }
            if (!systemLibrary.isShared()) {
              libs.add(new MadlLibraryNode(this, fileSystem.fromLocalFile(libFile),
                  systemLibrary.getShortName(), systemLibrary.getCategory().toLowerCase()));
            } else {
              libs.add(new MadlLibraryNode(this, fileSystem.fromLocalFile(libFile),
                  systemLibrary.getShortName()));
            }

          }
        }

      }*/

      libraries = libs.toArray(new MadlLibraryNode[libs.size()]);
    return libraries;
  }

  @Override
  public IMadlNode getParent() {
    return null;
  }

  @Override
  public IMadlNode[] getResources() {
    List<IMadlNode> resources = new ArrayList<IMadlNode>();

    File sdkDirectory = MadlSdkManager.getManager().getSdk().getDirectory();
    File libFile =new File(sdkDirectory, "lib"); //$NON-NLS-1$;
    File templates = new File(sdkDirectory, "templates"); //$NON-NLS-1$;
    File conf = new File(sdkDirectory, "conf"); //$NON-NLS-1$;
    IFileSystem fileSystem = EFS.getLocalFileSystem();

    if(libFile.exists()){
      resources.add(new MadlLibraryNode(this, fileSystem.fromLocalFile(libFile),
          libFile.getName()));
    }

    if(templates.exists()){
      resources.add(new PlatformTemplateNode(this, fileSystem.fromLocalFile(templates),
          templates.getName()));
    }

    if(conf.exists()){
      resources.add(new PlatformTemplateNode(this, fileSystem.fromLocalFile(conf),
          conf.getName()));
    }

    return resources.toArray(new IMadlNode[resources.size()]);
  }

  @Override
  public String toString() {
    return getLabel();
  }

  private List<IFileStore> filteredMembers(IFileStore file) throws CoreException {
    List<IFileStore> children = new ArrayList<IFileStore>();

    for (IFileStore child : file.childStores(EFS.NONE, new NullProgressMonitor())) {
      String name = child.getName();
      if (!(name.startsWith("."))) {
        children.add(child);
      }
    }

    return children;
  }

}
