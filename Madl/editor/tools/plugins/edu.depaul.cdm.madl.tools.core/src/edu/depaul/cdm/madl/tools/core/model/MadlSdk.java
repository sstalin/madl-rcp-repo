/*
 * Copyright 2012 Madl project authors.
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
package edu.depaul.cdm.madl.tools.core.model;

import edu.depaul.cdm.madl.tools.core.MadlCore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Represents the Madl SDK...
 *
 * <pre>
 *    madl-sdk/
 *       bin/
 *          madl[.exe]  <-- VM
 *          Chromium/   <-- Madlium
 *       lib/
 *          core/
 *             core.madl
 *             ... other core library files ...
 *          ... other libraries ...
 *       util/
 *          ... Madl utilities ...
 * </pre>
 *
 * @coverage madl.tools.core.model
 */
public class MadlSdk {

  private final File sdkPath;

  protected MadlSdk(File path) {
    sdkPath = path;

    if (sdkPath != null) {
      initializeSdk();
    }
  }

  /**
   * Answer the SDK directory
   */
  public File getDirectory() {
    return sdkPath;
  }

  /**
   * @return the SDK's documentation directory
   */
  public File getDocDirectory() {
    return new File(getDirectory(), "docs");
  }

  /**
   * Return the auxiliary documentation file for the given library. Return null if no such file
   * exists.
   *
   * @param libraryName
   * @return
   */
  public File getDocFileFor(String libraryName) {
    File dir = getDocDirectory();

    if (!dir.exists()) {
      return null;
    }

    File libDir = new File(dir, libraryName);

    File docFile = new File(libDir, libraryName + "_api.json");

    if (docFile.exists()) {
      return docFile;
    } else {
      return null;
    }
  }

  /**
   * @return the SDK's library directory path
   */
  public File getLibraryDirectory() {
    return new File(getDirectory(), "lib");
  }

  /**
   * @return the Madl Editor index file for the SDK libraries
   */
  public File getLibraryIndexFile() {
    return new File(new File(getLibraryDirectory(), "_internal"), "index.idx");
  }

  /**
   * @return the path to the madl2js script in the bin/ directory
   */
  public File getMadl2JsExecutable() {
    return getSdkExecutable("madl2js");
  }

  public File getMadlDocExecutable() {
    return getSdkExecutable("madldoc");
  }

  /**
   * @return the path to the madlfmt script in the bin/ directory.
   */
  public File getMadlFmtExecutable() {
    return getSdkExecutable("madlfmt");
  }

  /**
   * Answer the OS-specific Madlium directory.
   *
   * @param installDir the installation directory
   */
  public File getMadliumDir(File installDir) {
    return new File(installDir, "chromium");
  }

  /**
   * Answer the Madlium executable or <code>null</code> if it does not exist.
   */
  public File getMadliumExecutable() {
    File file = getMadliumBinary(getMadliumWorkingDirectory());

    if (file.exists()) {
      return file;
    } else {
      // As a fall-back, look in the directory where we used to install Madlium.
      file = getMadliumBinary(getMadliumWorkingDirectory_old());

      if (file.exists()) {
        return file;
      }
    }

    return null;
  }

  /**
   * Returns the directory where Madlium can be found.
   */
  public File getMadliumWorkingDirectory() {
    return new File(MadlSdkManager.getEclipseInstallationDirectory(), "chromium");
  }

  /**
   * Returns the old location for Madlium.
   */
  @Deprecated
  public File getMadliumWorkingDirectory_old() {
    return MadlSdkManager.getEclipseInstallationDirectory();
  }

  public File getPubExecutable() {
    String pubName = "pub" + (MadlCore.isWindows() ? ".bat" : "");

    return new File(new File(sdkPath, "bin"), pubName);
  }

  /**
   * @return the revision number of the SDK
   */
  public String getSdkVersion() {
    File revisionFile = new File(getDirectory(), "version");

    try {
      String revision = readFully(revisionFile);

      if (revision != null) {
        return revision;
      }
    } catch (IOException ioe) {

    }

    return "0";
  }

  /**
   * Answer the VM executable or <code>null</code> if it does not exist
   */
  public File getVmExecutable() {
    String vmName = "madl" + (MadlCore.isWindows() ? ".exe" : "");

    return new File(new File(sdkPath, "bin"), vmName);
  }

  /**
   * @return whether this install of the SDK has documentation
   */
  public boolean hasDocumentation() {
    return getDocDirectory().exists();
  }

  /**
   * Checks if madlium binary is available
   */
  public boolean isMadliumInstalled() {
    if (getMadliumExecutable() != null) {
      return true;
    }

    return false;
  }

  protected void dispose() {

  }

  protected File getSdkExecutable(String binaryName) {
    binaryName += (MadlCore.isWindows() ? ".bat" : "");
    return new File(new File(sdkPath, "bin"), binaryName);
  }

  protected void initializeSdk() {
    if (!MadlCore.isWindows()) {
      // TODO(devoncarew): these changes need to be moved into the create_sdk.py script.
      ensureExecutable(getVmExecutable());
      ensureExecutable(getMadl2JsExecutable());
      ensureExecutable(getMadlDocExecutable());
      ensureExecutable(getPubExecutable());
    }
  }

  /**
   * Ensure that the madl vm is executable. If it is not, make it executable and log that it was
   * necessary for us to do so.
   */
  private void ensureExecutable(File binary) {
    if (binary != null && binary.exists()) {
      if (!binary.canExecute()) {
        makeExecutable(binary);

        MadlCore.logError(binary.getPath() + " was not executable");
      }
    }
  }

  private File getMadliumBinary(File dir) {
    if (MadlCore.isWindows()) {
      return new File(dir, "Chrome.exe");
    } else if (MadlCore.isMac()) {
      File appDir = new File(dir, "Chromium.app");

      return new File(new File(new File(appDir, "Contents"), "MacOS"), "Chromium");
    } else {
      // Linux
      return new File(dir, "chrome");
    }
  }

  /**
   * Make the given file executable; returns true on success.
   *
   * @param file
   * @return
   */
  private boolean makeExecutable(File file) {
    // First try and set executable for all users.
    if (file.setExecutable(true, false)) {
      // success

      return true;
    }

    // Then try only for the current user.
    return file.setExecutable(true, true);
  }

  private String readFully(File revisionFile) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(revisionFile));

    try {
      String line = reader.readLine();

      if (line != null) {
        return line.trim();
      } else {
        return null;
      }
    } finally {
      reader.close();
    }
  }

}
