/*
 * Copyright (c) 2012, the Dart project authors.
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

package edu.depaul.cdm.madl.tools.ui.internal.intro;

import org.eclipse.osgi.util.NLS;

public class IntroMessages extends NLS {
  private static final String BUNDLE_NAME = "com.google.dart.tools.ui.internal.intro.IntroMessages"; //$NON-NLS-1$
  public static String IntroEditor_name;
  public static String IntroEditor_tooltip;
  public static String IntroEditor_projectName;

  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, IntroMessages.class);
  }

  private IntroMessages() {
  }
}
