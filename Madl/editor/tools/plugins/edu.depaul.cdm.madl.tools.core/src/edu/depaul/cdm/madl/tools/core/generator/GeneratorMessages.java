/*
 * Copyright (c) 2011, the Madl project authors.
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
package edu.depaul.cdm.madl.tools.core.generator;

import org.eclipse.osgi.util.NLS;

/**
 * @coverage madl.tools.core.generator
 */
public class GeneratorMessages extends NLS {
  private static final String BUNDLE_NAME = "com.google.madl.tools.core.generator.GeneratorMessages"; //$NON-NLS-1$
  public static String ApplicationGenerator_description;
  public static String ApplicationGenerator_directoryMessage;
  public static String ApplicationGenerator_fileExists;
  public static String ApplicationGenerator_htmlFileMessage;
  public static String ApplicationGenerator_message;
  public static String ApplicationGenerator_noWhiteSpace;
  public static String ApplicationGenerator_sourceFileMessage;
  public static String MadlFileGenerator_containerDoesNotExist;
  public static String MadlFileGenerator_folderWillBeCreated;
  public static String MadlFileGenerator_projectWillBeCreated;
  public static String MadlFileGenerator_projectFolderWillBeCreated;
  public static String MadlFileGenerator_selectContainer;
  public static String FileGenerator_Description;
  public static String FileGenerator_fileExists;
  public static String FileGenerator_filesCannotBeGenerated;
  public static String FileGenerator_locationDoesNotExist;
  public static String FileGenerator_message;
  public static String FileGenerator_whiteSpaceNotAllowed;
  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, GeneratorMessages.class);
  }

  private GeneratorMessages() {
  }
}
