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
package edu.depaul.cdm.madl.tools.ui.internal.formatter;

import edu.depaul.cdm.madl.tools.core.MessageConsole;

/*import edu.depaul.cdm.madl.tools.core.dart2js.ProcessRunner;
import edu.depaul.cdm.madl.tools.core.model.MadlSdkManager;*/

import edu.depaul.cdm.madl.tools.ui.MadlToolsPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Point;

/*import org.json.JSONException;
import org.json.JSONObject;*/

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Launches the <code>dartfmt</code> process collecting stdout, stderr, and exit code information.
 */
public class MadlFormatter {

//  /**
//   * Run the formatter on the given input path.
//   * 
//   * @param path the path to pass to the formatter
//   * @param monitor the monitor for displaying progress
//   * @param console the console to which output should be directed
//   * @throws IOException if an exception was thrown during execution
//   * @throws CoreException if an exception occurs in file refresh
//   */
//  public static void format(IPath path, IProgressMonitor monitor, MessageConsole console)
//      throws IOException, CoreException {
//
//    File dartfmt = MadlSdkManager.getManager().getSdk().getMadlFmtExecutable();
//    if (!dartfmt.canExecute()) {
//      return;
//    }
//
//    ProcessBuilder builder = new ProcessBuilder();
//
//    List<String> args = new ArrayList<String>();
//    args.add(dartfmt.getPath());
//    args.addAll(buildArguments(path));
//
//    builder.command(args);
//    builder.redirectErrorStream(true);
//
//    ProcessRunner runner = new ProcessRunner(builder);
//    runner.runSync(monitor);
//
//    if (runner.getExitCode() == 0) {
//      ResourcesPlugin.getWorkspace().getRoot().getFile(path).refreshLocal(
//          IResource.DEPTH_INFINITE,
//          monitor);
//    }
//
//    StringBuilder sb = new StringBuilder();
//
//    if (!runner.getStdOut().isEmpty()) {
//      sb.append(runner.getStdOut() + "\n");
//    }
//
//    //TODO (pquitslund): better error handling
//    if (!runner.getStdErr().isEmpty()) {
//      sb.append(runner.getStdErr() + "\n");
//    }
//
//    console.print(sb.toString());
//
//  }

  /**
   * Holder for formatted source and selection info.
   */
  public static class FormattedSource {
    public int selectionOffset;
    public int selectionLength;
    public String source;
  }

  //ss
/*  private static final String ARGS_MACHINE_FORMAT_FLAG = "-m";
  private static final String ARGS_SOURCE_FLAG = "-s";

  private static final String JSON_LENGTH_KEY = "length";
  private static final String JSON_OFFSET_KEY = "offset";
  private static final String JSON_SELECTION_KEY = "selection";
  private static final String JSON_SOURCE_KEY = "source";*/

  /**
   * Run the formatter on the given input source.
   * 
   * @param source the source to pass to the formatter
   * @param selection the selection info to pass into the formatter
   * @param monitor the monitor for displaying progress
   * @param console the console to which output should be directed
   * @throws IOException if an exception was thrown during execution
   * @throws CoreException if an exception occurs in file refresh
   * @return the formatted source (or null in case formatting could not be executed)
   */
  public static FormattedSource format(final String source, final Point selection,
      IProgressMonitor monitor, MessageConsole console) throws IOException, CoreException {
	 
	  FormattedSource result = new FormattedSource();
	  return result;

	 // ss
 /*   File dartfmt = MadlSdkManager.getManager().getSdk().getMadlFmtExecutable();
    if (!dartfmt.canExecute()) {
      return null;
    }

    ProcessBuilder builder = new ProcessBuilder();

    List<String> args = new ArrayList<String>();
    args.add(dartfmt.getPath());
    args.add(ARGS_SOURCE_FLAG + " " + selection.x + "," + selection.y);
    args.add(ARGS_MACHINE_FORMAT_FLAG);

    builder.command(args);
    builder.redirectErrorStream(true);

    ProcessRunner runner = new ProcessRunner(builder) {
      @Override
      protected void processStarted(Process process) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
            process.getOutputStream(),
            "UTF-8"), source.length());
        writer.append(source);
        writer.close();
      }
    };

    runner.runSync(monitor);

    StringBuilder sb = new StringBuilder();

    if (!runner.getStdOut().isEmpty()) {
      sb.append(runner.getStdOut());
    }

    //TODO (pquitslund): better error handling
    if (runner.getExitCode() != 0) {
      throw new IOException(runner.getStdErr());
    }

    String formattedSource = sb.toString();
    try {
      JSONObject json = new JSONObject(formattedSource);
      String sourceString = (String) json.get(JSON_SOURCE_KEY);
      JSONObject selectionJson = (JSONObject) json.get(JSON_SELECTION_KEY);
      //TODO (pquitslund): figure out why we (occasionally) need to remove an extra trailing NEWLINE
      if (sourceString.endsWith("\n\n")) {
        sourceString = sourceString.substring(0, sourceString.length() - 1);
      }
      FormattedSource result = new FormattedSource();
      result.source = sourceString;
      result.selectionOffset = selectionJson.getInt(JSON_OFFSET_KEY);
      result.selectionLength = selectionJson.getInt(JSON_LENGTH_KEY);
      return result;
    } catch (JSONException e) {
      MadlToolsPlugin.log(e);
      throw new IOException(e);
    }*/

  }

  public static boolean isAvailable() {
	  return false;
    //return MadlSdkManager.getManager().getSdk().getMadlFmtExecutable().canExecute();
  }

//  private static List<String> buildArguments(IPath path) {
//    ArrayList<String> args = new ArrayList<String>();
//    args.add("-w");
//    args.add(path.toOSString());
//    return args;
//  }

}
