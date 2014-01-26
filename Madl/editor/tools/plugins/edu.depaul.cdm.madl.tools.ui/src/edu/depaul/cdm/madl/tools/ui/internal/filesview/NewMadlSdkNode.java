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

// import edu.depaul.cdm.madl.engine.sdk.SdkLibrary;
// import edu.depaul.cdm.madl.tools.core.model.MadlSdkManager;
import edu.depaul.cdm.madl.tools.ui.MadlToolsPlugin;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.IAdapterFactory;
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

  private MadlLibraryNode[] libraries;

  public NewMadlSdkNode() {
    libraries = getLibraries();
  }

  @Override
  public IFileStore getFileStore() {
    //ss
    /*
     * if (MadlSdkManager.getManager().hasSdk()) { File sdkLibDir =
     * MadlSdkManager.getManager().getSdk().getLibraryDirectory(); return
     * EFS.getLocalFileSystem().fromLocalFile(sdkLibDir); }
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
    if (libraries == null) {
      List<MadlLibraryNode> libs = new ArrayList<MadlLibraryNode>();

      File libFile;
//ss
      /*
       * if (!MadlSdkManager.getManager().hasSdk()) { libFile = null; } else { SdkLibrary[]
       * systemLibraries = MadlSdkManager.getManager().getNewSdk().getSdkLibraries(); //TODO
       * (pquitslund): fix how we're getting the SDK directory File sdkDirectory =
       * MadlSdkManager.getManager().getSdk().getDirectory(); IFileSystem fileSystem =
       * EFS.getLocalFileSystem(); for (SdkLibrary systemLibrary : systemLibraries) { if
       * (systemLibrary.isDocumented()) { libFile = new File(sdkDirectory, "lib"); //$NON-NLS-1$
       * String pathToLib = systemLibrary.getPath(); if (pathToLib.indexOf("/") != -1) {
       * //$NON-NLS-1$ libFile = new File(libFile, new
       * Path(pathToLib).removeLastSegments(1).toOSString()); } if (!systemLibrary.isShared()) {
       * libs.add(new MadlLibraryNode(this, fileSystem.fromLocalFile(libFile),
       * systemLibrary.getShortName(), systemLibrary.getCategory().toLowerCase())); } else {
       * libs.add(new MadlLibraryNode(this, fileSystem.fromLocalFile(libFile),
       * systemLibrary.getShortName())); }
       * 
       * } }
       * 
       * }
       */

      libraries = libs.toArray(new MadlLibraryNode[libs.size()]);
    }
    return libraries;
  }

  @Override
  public IMadlNode getParent() {
    return null;
  }

  @Override
  public String toString() {
    return getLabel();
  }
}
