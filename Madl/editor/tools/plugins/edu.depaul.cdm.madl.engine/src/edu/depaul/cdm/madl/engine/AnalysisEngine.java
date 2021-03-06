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
package edu.depaul.cdm.madl.engine;

import edu.depaul.cdm.madl.engine.context.AnalysisContext;
//import edu.depaul.cdm.madl.engine.internal.context.DelegatingAnalysisContextImpl;
//import edu.depaul.cdm.madl.engine.internal.context.InstrumentedAnalysisContextImpl;
import edu.depaul.cdm.madl.engine.utilities.instrumentation.Instrumentation;
import edu.depaul.cdm.madl.engine.utilities.io.FileUtilities;
import edu.depaul.cdm.madl.engine.utilities.logging.Logger;

/**
 * The unique instance of the class {@code AnalysisEngine} serves as the entry point for the
 * functionality provided by the analysis engine.
 * 
 * @coverage dart.engine
 */
public final class AnalysisEngine {
  /**
   * The suffix used for Madl source files.
   */
  public static final String SUFFIX_DART = "dart";

  /**
   * The short suffix used for HTML files.
   */
  public static final String SUFFIX_HTM = "htm";

  /**
   * The long suffix used for HTML files.
   */
  public static final String SUFFIX_HTML = "html";

  /**
   * The unique instance of this class.
   */
  private static final AnalysisEngine UniqueInstance = new AnalysisEngine();

  /**
   * Return the unique instance of this class.
   * 
   * @return the unique instance of this class
   */
  public static AnalysisEngine getInstance() {
    return UniqueInstance;
  }

  /**
   * Return {@code true} if the given file name is assumed to contain Madl source code.
   * 
   * @param fileName the name of the file being tested
   * @return {@code true} if the given file name is assumed to contain Madl source code
   */
  public static boolean isMadlFileName(String fileName) {
    if (fileName == null) {
      return false;
    }
    return FileUtilities.getExtension(fileName).equalsIgnoreCase(SUFFIX_DART);
  }

  /**
   * Return {@code true} if the given file name is assumed to contain HTML.
   * 
   * @param fileName the name of the file being tested
   * @return {@code true} if the given file name is assumed to contain HTML
   */
  public static boolean isHtmlFileName(String fileName) {
    if (fileName == null) {
      return false;
    }
    String extension = FileUtilities.getExtension(fileName);
    return extension.equalsIgnoreCase(SUFFIX_HTML) || extension.equalsIgnoreCase(SUFFIX_HTM);
  }

  /**
   * The logger that should receive information about errors within the analysis engine.
   */
  private Logger logger = Logger.NULL;

  /**
   * Prevent the creation of instances of this class.
   */
  private AnalysisEngine() {
    super();
  }

  /**
   * Create a new context in which analysis can be performed.
   * 
   * @return the analysis context that was created
   */
  public AnalysisContext createAnalysisContext() {
    //
    // If instrumentation is ignoring data, return an uninstrumented analysis context.
    //
	 //ss 
   /* if (Instrumentation.isNullLogger()) {
      return new DelegatingAnalysisContextImpl();
    }
    return new InstrumentedAnalysisContextImpl(new DelegatingAnalysisContextImpl());*/
	  return null;
  }

  /**
   * Return the logger that should receive information about errors within the analysis engine.
   * 
   * @return the logger that should receive information about errors within the analysis engine
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Set the logger that should receive information about errors within the analysis engine to the
   * given logger.
   * 
   * @param logger the logger that should receive information about errors within the analysis
   *          engine
   */
  public void setLogger(Logger logger) {
    this.logger = logger == null ? Logger.NULL : logger;
  }
}
