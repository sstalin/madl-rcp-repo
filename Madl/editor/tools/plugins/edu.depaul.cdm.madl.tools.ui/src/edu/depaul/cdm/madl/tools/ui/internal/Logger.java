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
package edu.depaul.cdm.madl.tools.ui.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

import java.util.StringTokenizer;

/**
 * Small convenience class to log messages to plugin's log file and also, if desired, the console.
 * This class should only be used by classes in this plugin. Other plugins should make their own
 * copy, with appropriate ID.
 */
public class Logger {
  private static final String PLUGIN_ID = "com.google.madl.tools.ui"; //$NON-NLS-1$

  private static final String TRACEFILTER_LOCATION = "/debug/tracefilter"; //$NON-NLS-1$

  public static final int OK = IStatus.OK; // 0
  public static final int INFO = IStatus.INFO; // 1
  public static final int WARNING = IStatus.WARNING; // 2
  public static final int ERROR = IStatus.ERROR; // 4

  public static final int OK_DEBUG = 200 + OK;
  public static final int INFO_DEBUG = 200 + INFO;
  public static final int WARNING_DEBUG = 200 + WARNING;
  public static final int ERROR_DEBUG = 200 + ERROR;

  /**
   * @return true if the platform is debugging
   */
  public static boolean isDebugging() {
    return Platform.inDebugMode();
  }

  /**
   * Determines if currently tracing a category
   * 
   * @param category
   * @return true if tracing category, false otherwise
   */
  public static boolean isTracing(String category) {
    if (!isDebugging()) {
      return false;
    }

    String traceFilter = Platform.getDebugOption(PLUGIN_ID + TRACEFILTER_LOCATION);
    if (traceFilter != null) {
      StringTokenizer tokenizer = new StringTokenizer(traceFilter, ","); //$NON-NLS-1$
      while (tokenizer.hasMoreTokens()) {
        String cat = tokenizer.nextToken().trim();
        if (category.equals(cat)) {
          return true;
        }
      }
    }
    return false;
  }

  public static void log(int level, String message) {
    _log(level, message, null);
  }

  public static void log(int level, String message, Throwable exception) {
    _log(level, message, exception);
  }

  public static void logException(String message, Throwable exception) {
    _log(ERROR, message, exception);
  }

  public static void logException(Throwable exception) {
    _log(ERROR, exception.getMessage(), exception);
  }

  public static void trace(String category, String message) {
    _trace(category, message, null);
  }

  public static void traceException(String category, String message, Throwable exception) {
    _trace(category, message, exception);
  }

  public static void traceException(String category, Throwable exception) {
    _trace(category, exception.getMessage(), exception);
  }

  /**
   * Adds message to log.
   * 
   * @param level severity level of the message (OK, INFO, WARNING, ERROR, OK_DEBUG, INFO_DEBUG,
   *          WARNING_DEBUG, ERROR_DEBUG)
   * @param message text to add to the log
   * @param exception exception thrown
   */
  protected static void _log(int level, String message, Throwable exception) {
    if (level == OK_DEBUG || level == INFO_DEBUG || level == WARNING_DEBUG || level == ERROR_DEBUG) {
      if (!isDebugging()) {
        return;
      }
    }

    int severity = IStatus.OK;
    switch (level) {
      case INFO_DEBUG:
      case INFO:
        severity = IStatus.INFO;
        break;
      case WARNING_DEBUG:
      case WARNING:
        severity = IStatus.WARNING;
        break;
      case ERROR_DEBUG:
      case ERROR:
        severity = IStatus.ERROR;
    }
    message = (message != null) ? message : "null"; //$NON-NLS-1$
    Status statusObj = new Status(severity, PLUGIN_ID, severity, message, exception);
    Bundle bundle = Platform.getBundle(PLUGIN_ID);
    if (bundle != null) {
      Platform.getLog(bundle).log(statusObj);
    }
  }

  /**
   * Prints message to log if category matches /debug/tracefilter option.
   * 
   * @param message text to print
   * @param category category of the message, to be compared with /debug/tracefilter
   */
  protected static void _trace(String category, String message, Throwable exception) {
    if (isTracing(category)) {
      message = (message != null) ? message : "null"; //$NON-NLS-1$
      Status statusObj = new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, message, exception);
      Bundle bundle = Platform.getBundle(PLUGIN_ID);
      if (bundle != null) {
        Platform.getLog(bundle).log(statusObj);
      }
    }
  }
}
