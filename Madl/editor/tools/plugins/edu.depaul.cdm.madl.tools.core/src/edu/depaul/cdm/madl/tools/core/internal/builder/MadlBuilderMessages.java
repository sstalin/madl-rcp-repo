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

package edu.depaul.cdm.madl.tools.core.internal.builder;

import org.eclipse.osgi.util.NLS;

/**
 * @coverage madl.tools.core.builder
 */
public class MadlBuilderMessages extends NLS {
  private static final String BUNDLE_NAME = "edu.depaul.cdm.madl.tools.core.internal.builder.MadlBuilderMessages"; //$NON-NLS-1$
  public static String MadlBuilder_console_js_file_description;
  public static String MadlBuilder_console_html_file_description;

  public static String CompileOptmized_title;
  public static String CompileOtimized_errorMessage;

  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, MadlBuilderMessages.class);
  }

  private MadlBuilderMessages() {
  }
}
