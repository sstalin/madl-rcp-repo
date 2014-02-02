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

package edu.depaul.cdm.madl.tools.core.internal.util;

import java.io.File;

/**
 * The class <code>Extensions</code> defines utility methods for working with various file
 * extensions.
 * 
 * @coverage madl.tools.core
 */
public final class Extensions {
  /**
   * The file extension used by Madl source files, without the leading dot.
   */
  public static final String MADL = "madl";

  /**
   * The file extension used by Madl source files, with the leading dot.
   */
  public static final String DOT_MADL = "." + MADL;

  /**
   * Return <code>true</code> if the given file is a Madl source file.
   * 
   * @param file the file being tested
   * @return <code>true</code> if the given file is a Madl source file
   */
  public static boolean isMadlFile(File file) {
    return file.getName().endsWith(DOT_MADL);
  }

  /**
   * Prevent the creation of instances of this class.
   */
  private Extensions() {
    super();
  }
}
