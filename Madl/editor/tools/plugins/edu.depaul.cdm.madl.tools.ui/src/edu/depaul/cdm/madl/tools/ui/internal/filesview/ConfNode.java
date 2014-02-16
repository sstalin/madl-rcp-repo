package edu.depaul.cdm.madl.tools.ui.internal.filesview;

import edu.depaul.cdm.madl.tools.ui.MadlToolsPlugin;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;

import java.util.ArrayList;
import java.util.List;

public class ConfNode implements IMadlNode{
  private MadlSdkNode parent;
  private IFileStore root;
  private String name;
  private String category;

  public ConfNode(MadlSdkNode parent, IFileStore root, String name) {
    this.parent = parent;
    this.root = root;
    this.name = name;
  }

  public ConfNode(MadlSdkNode parent, IFileStore root, String name, String category) {
    this.parent = parent;
    this.root = root;
    this.name = name;
    this.category = category;
  }

  public String getCategory() {
    return category;
  }

  public IFileStore[] getFiles() {
    try {
      List<IFileStore> members = filteredMembers(root);

      return members.toArray(new IFileStore[members.size()]);
    } catch (CoreException e) {
      return new IFileStore[0];
    }
  }

  @Override
  public IFileStore getFileStore() {
    return root;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return MadlToolsPlugin.findImageDescriptor("icons/full/obj16/fldr_obj.gif");
  }

  @Override
  public String getLabel() {
    return name;
  }

  @Override
  public IMadlNode getParent() {
    return parent;
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
