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
package edu.depaul.cdm.madl.engine.sdk;

import edu.depaul.cdm.madl.engine.AnalysisEngine;
import edu.depaul.cdm.madl.engine.context.AnalysisContext;
import edu.depaul.cdm.madl.engine.context.ChangeSet;
import edu.depaul.cdm.madl.engine.internal.context.AnalysisContextImpl;
import edu.depaul.cdm.madl.engine.internal.context.InternalAnalysisContext;
import edu.depaul.cdm.madl.engine.internal.sdk.LibraryMap;
import edu.depaul.cdm.madl.engine.internal.sdk.SdkLibrariesReader;
import edu.depaul.cdm.madl.engine.source.ContentCache;
import edu.depaul.cdm.madl.engine.source.MadlUriResolver;
import edu.depaul.cdm.madl.engine.source.FileBasedSource;
import edu.depaul.cdm.madl.engine.source.Source;
import edu.depaul.cdm.madl.engine.source.SourceFactory;
import edu.depaul.cdm.madl.engine.source.UriKind;
import edu.depaul.cdm.madl.engine.utilities.io.FileUtilities;
import edu.depaul.cdm.madl.engine.utilities.os.OSUtilities;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Instances of the class {@code DirectoryBasedMadlSdk} represent a Madl SDK installed in a
 * specified directory.
 *
 * @coverage madl.engine.sdk
 */
public class DirectoryBasedMadlSdk implements MadlSdk {
  /**
   * The {@link AnalysisContext} which is used for all of the sources in this {@link MadlSdk}.
   */
  private InternalAnalysisContext analysisContext;

  /**
   * The directory containing the SDK.
   */
  private final File sdkDirectory;

  /**
   * The revision number of this SDK, or {@code "0"} if the revision number cannot be discovered.
   */
  private String sdkVersion;

  /**
   * The file containing the Madlium executable.
   */
  private File madliumExecutable;

  /**
   * The file containing the VM executable.
   */
  private File vmExecutable;

  /**
   * A mapping from Madl library URI's to the library represented by that URI.
   */
  private LibraryMap libraryMap;

  /**
   * The name of the directory within the SDK directory that contains executables.
   */
  private static final String BIN_DIRECTORY_NAME = "bin"; //$NON-NLS-1$

  /**
   * The name of the directory within the SDK directory that contains Chromium.
   */
  private static final String CHROMIUM_DIRECTORY_NAME = "chromium"; //$NON-NLS-1$

  /**
   * The name of the file containing the Madlium executable on Linux.
   */
  private static final String MADLIUM_EXECUTABLE_NAME_LINUX = "chrome"; //$NON-NLS-1$

  /**
   * The name of the file containing the Madlium executable on Macintosh.
   */
  private static final String MADLIUM_EXECUTABLE_NAME_MAC = "Chromium.app/Contents/MacOS/Chromium"; //$NON-NLS-1$

  /**
   * The name of the file containing the Madlium executable on Windows.
   */
  private static final String MADLIUM_EXECUTABLE_NAME_WIN = "Chrome.exe"; //$NON-NLS-1$

  /**
   * The name of the {@link System} property whose value is the path to the default Madl SDK
   * directory.
   */
  private static final String DEFAULT_DIRECTORY_PROPERTY_NAME = "edu.depaul.cdm.madl.sdk"; //$NON-NLS-1$

  /**
   * The name of the directory within the SDK directory that contains documentation for the
   * libraries.
   */
  private static final String DOCS_DIRECTORY_NAME = "docs"; //$NON-NLS-1$

  /**
   * The suffix added to the name of a library to derive the name of the file containing the
   * documentation for that library.
   */
  private static final String DOC_FILE_SUFFIX = "_api.json"; //$NON-NLS-1$

  /**
   * The name of the directory within the SDK directory that contains the libraries file.
   */
  private static final String INTERNAL_DIR = "_internal"; //$NON-NLS-1$

  /**
   * The name of the directory within the SDK directory that contains the libraries.
   */
  private static final String LIB_DIRECTORY_NAME = "lib"; //$NON-NLS-1$

  /**
   * The name of the libraries file.
   */
  private static final String LIBRARIES_FILE = "libraries.madl"; //$NON-NLS-1$

  /**
   * The name of the pub executable on windows.
   */
  private static final String PUB_EXECUTABLE_NAME_WIN = "pub.bat"; //$NON-NLS-1$

  /**
   * The name of the pub executable on non-windows operating systems.
   */
  private static final String PUB_EXECUTABLE_NAME = "pub"; //$NON-NLS-1$

  /**
   * The name of the file within the SDK directory that contains the revision number of the SDK.
   */
  private static final String REVISION_FILE_NAME = "revision"; //$NON-NLS-1$

  /**
   * The name of the file containing the VM executable on the Windows operating system.
   */
  private static final String VM_EXECUTABLE_NAME_WIN = "madl.exe"; //$NON-NLS-1$

  /**
   * The name of the file containing the VM executable on non-Windows operating systems.
   */
  private static final String VM_EXECUTABLE_NAME = "madl"; //$NON-NLS-1$

  /**
   * Return the default Madl SDK, or {@code null} if the directory containing the default SDK cannot
   * be determined (or does not exist).
   *
   * @return the default Madl SDK
   */
  public static DirectoryBasedMadlSdk getDefaultSdk() {
    File sdkDirectory = getDefaultSdkDirectory();
    if (sdkDirectory == null) {
      return null;
    }
    return new DirectoryBasedMadlSdk(sdkDirectory);
  }

  /**
   * Return the default directory for the Madl SDK, or {@code null} if the directory cannot be
   * determined (or does not exist). The default directory is provided by a {@link System} property
   * named {@code edu.depaul.cdm.madl.sdk}.
   *
   * @return the default directory for the Madl SDK
   */
  public static File getDefaultSdkDirectory() {
    String sdkProperty = System.getProperty(DEFAULT_DIRECTORY_PROPERTY_NAME);
    if (sdkProperty == null) {
      return null;
    }
    File sdkDirectory = new File(sdkProperty);
    if (!sdkDirectory.exists()) {
      return null;
    }
    return sdkDirectory;
  }

  /**
   * Initialize a newly created SDK to represent the Madl SDK installed in the given directory.
   *
   * @param sdkDirectory the directory containing the SDK
   */
  public DirectoryBasedMadlSdk(File sdkDirectory) {
    this.sdkDirectory = sdkDirectory.getAbsoluteFile();
    initializeSdk();
    initializeLibraryMap();
    //ss
 /*   analysisContext = new AnalysisContextImpl();
    analysisContext.setSourceFactory(new SourceFactory(new MadlUriResolver(this)));
    String[] uris = getUris();
    ChangeSet changeSet = new ChangeSet();
    for (String uri : uris) {
      changeSet.added(analysisContext.getSourceFactory().forUri(uri));
    }
    analysisContext.applyChanges(changeSet);*/
  }

  /**
   * Initialize a newly created SDK to represent the Madl SDK installed in the given directory.
   * <p>
   * Added in order to test AnalysisContextImpl2.
   *
   * @param sdkDirectory the directory containing the SDK
   */
  public DirectoryBasedMadlSdk(File sdkDirectory, boolean ignored) {
    this.sdkDirectory = sdkDirectory.getAbsoluteFile();
    initializeSdk();
    initializeLibraryMap();
    analysisContext = new AnalysisContextImpl();
    analysisContext.setSourceFactory(new SourceFactory(new MadlUriResolver(this)));
    String[] uris = getUris();
    ChangeSet changeSet = new ChangeSet();
    for (String uri : uris) {
      changeSet.added(analysisContext.getSourceFactory().forUri(uri));
    }
    analysisContext.applyChanges(changeSet);
  }

  //ss
 /* @Override
  public Source fromEncoding(ContentCache contentCache, UriKind kind, URI uri) {
    return new FileBasedSource(contentCache, new File(uri), kind);
  }*/

  @Override
  public AnalysisContext getContext() {
    return analysisContext;
  }

  /**
   * Return the directory containing the SDK.
   *
   * @return the directory containing the SDK
   */
  public File getDirectory() {
    return sdkDirectory;
  }

  /**
   * Return the directory containing documentation for the SDK.
   *
   * @return the SDK's documentation directory
   */
  public File getDocDirectory() {
    return new File(sdkDirectory, DOCS_DIRECTORY_NAME);
  }

  /**
   * Return the auxiliary documentation file for the given library, or {@code null} if no such file
   * exists.
   *
   * @param libraryName the name of the library associated with the documentation file to be
   *          returned
   * @return the auxiliary documentation file for the library
   */
  public File getDocFileFor(String libraryName) {
    File dir = getDocDirectory();
    if (!dir.exists()) {
      return null;
    }
    File libDir = new File(dir, libraryName);
    File docFile = new File(libDir, libraryName + DOC_FILE_SUFFIX);
    if (docFile.exists()) {
      return docFile;
    }
    return null;
  }

  /**
   * Return the directory within the SDK directory that contains the libraries.
   *
   * @return the directory that contains the libraries
   */
  public File getLibraryDirectory() {
    return new File(sdkDirectory, LIB_DIRECTORY_NAME);
  }

  /**
   * Return the file containing the Madlium executable, or {@code null} if it does not exist.
   *
   * @return the file containing the Madlium executable
   */
  public File getMadliumExecutable() {
    synchronized (this) {
      if (madliumExecutable == null) {
        File file = new File(getMadliumWorkingDirectory(), getMadliumBinaryName());
        if (file.exists()) {
          madliumExecutable = file;
        }
      }
    }
    return madliumExecutable;
  }

  /**
   * Return the directory where madlium can be found in the Madl SDK (the directory that will be the
   * working directory is Madlium is invoked without changing the default).
   *
   * @return the directory where madlium can be found
   */
  public File getMadliumWorkingDirectory() {
    return new File(sdkDirectory.getParentFile(), CHROMIUM_DIRECTORY_NAME);
  }

  /**
   * Return the file containing the Pub executable, or {@code null} if it does not exist.
   *
   * @return the file containing the Pub executable
   */
  public File getPubExecutable() {
    String pubBinaryName = OSUtilities.isWindows() ? PUB_EXECUTABLE_NAME_WIN : PUB_EXECUTABLE_NAME;

    File file = new File(new File(sdkDirectory, BIN_DIRECTORY_NAME), pubBinaryName);

    return file.exists() ? file : null;
  }

  @Override
  public SdkLibrary[] getSdkLibraries() {
    return libraryMap.getSdkLibraries();
  }

  @Override
  public SdkLibrary getSdkLibrary(String madlUri) {
    return libraryMap.getLibrary(madlUri);
  }

  /**
   * Return the revision number of this SDK, or {@code "0"} if the revision number cannot be
   * discovered.
   *
   * @return the revision number of this SDK
   */
  @Override
  public String getSdkVersion() {
    synchronized (this) {
      if (sdkVersion == null) {
        sdkVersion = DEFAULT_VERSION;
        File revisionFile = new File(sdkDirectory, REVISION_FILE_NAME);
        try {
          String revision = FileUtilities.getContents(revisionFile);
          if (revision != null) {
            sdkVersion = revision;
          }
        } catch (IOException exception) {
          // Fall through to return the default.
        }
      }
    }
    return sdkVersion;
  }

  /**
   * Return an array containing the library URI's for the libraries defined in this SDK.
   *
   * @return the library URI's for the libraries defined in this SDK
   */
  @Override
  public String[] getUris() {
    return libraryMap.getUris();
  }

  /**
   * Return the file containing the VM executable, or {@code null} if it does not exist.
   *
   * @return the file containing the VM executable
   */
  public File getVmExecutable() {
    synchronized (this) {
      if (vmExecutable == null) {
        File file = new File(new File(sdkDirectory, BIN_DIRECTORY_NAME), getVmBinaryName());
        if (file.exists()) {
          vmExecutable = file;
        }
      }
    }
    return vmExecutable;
  }

  /**
   * Return {@code true} if this SDK includes documentation.
   *
   * @return {@code true} if this installation of the SDK has documentation
   */
  public boolean hasDocumentation() {
    return getDocDirectory().exists();
  }

  /**
   * Return {@code true} if the Madlium binary is available.
   *
   * @return {@code true} if the Madlium binary is available
   */
  public boolean isMadliumInstalled() {
    return getMadliumExecutable() != null;
  }

  @Override
  public Source mapMadlUri(String madlUri) {
    SdkLibrary library = getSdkLibrary(madlUri);
    if (library == null) {
      return null;
    }
    return new FileBasedSource(analysisContext.getSourceFactory().getContentCache(), new File(
        getLibraryDirectory(),
        library.getPath()), UriKind.MADL_URI);
  }

  /**
   * Ensure that the madl VM is executable. If it is not, make it executable and log that it was
   * necessary for us to do so.
   */
  private void ensureVmIsExecutable() {
    File madlVm = getVmExecutable();
    if (madlVm != null) {
      if (!madlVm.canExecute()) {
        FileUtilities.makeExecutable(madlVm);
        AnalysisEngine.getInstance().getLogger().logError(madlVm.getPath() + " was not executable");
      }
    }
  }

  /**
   * Return the name of the file containing the Madlium executable.
   *
   * @return the name of the file containing the Madlium executable
   */
  private String getMadliumBinaryName() {
    if (OSUtilities.isWindows()) {
      return MADLIUM_EXECUTABLE_NAME_WIN;
    } else if (OSUtilities.isMac()) {
      return MADLIUM_EXECUTABLE_NAME_MAC;
    } else {
      return MADLIUM_EXECUTABLE_NAME_LINUX;
    }
  }

  /**
   * Return the name of the file containing the VM executable.
   *
   * @return the name of the file containing the VM executable
   */
  private String getVmBinaryName() {
    if (OSUtilities.isWindows()) {
      return VM_EXECUTABLE_NAME_WIN;
    } else {
      return VM_EXECUTABLE_NAME;
    }
  }

  /**
   * Read all of the configuration files to initialize the library maps.
   */
  private void initializeLibraryMap() {
    try {
      File librariesFile = new File(new File(getLibraryDirectory(), INTERNAL_DIR), LIBRARIES_FILE);
      String contents = FileUtilities.getContents(librariesFile);
      libraryMap = new SdkLibrariesReader().readFrom(librariesFile, contents);
    } catch (Exception exception) {
      AnalysisEngine.getInstance().getLogger().logError(exception);
      libraryMap = new LibraryMap();
    }
  }

  /**
   * Initialize the state of the SDK.
   */
  private void initializeSdk() {
    if (!OSUtilities.isWindows()) {
      ensureVmIsExecutable();
    }
  }
}
